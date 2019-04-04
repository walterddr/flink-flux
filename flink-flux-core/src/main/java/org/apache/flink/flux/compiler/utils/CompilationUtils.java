package org.apache.flink.flux.compiler.utils;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.flux.compiler.CompilationVertex;
import org.apache.flink.flux.compiler.FluxContext;
import org.apache.flink.flux.model.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.operators.OneInputStreamOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.apache.flink.flux.compiler.utils.ReflectiveInvokeUtils.*;

public final class CompilationUtils {
    private static final Logger LOG = LoggerFactory.getLogger(CompilationUtils.class);

    public static void compileSource(
            FluxContext fluxContext,
            StreamExecutionEnvironment senv,
            CompilationVertex vertex) throws Exception {
        // Compile vertex
        SourceDef sourceDef = (SourceDef) vertex.getVertex();
        SourceFunction sourceFunction = (SourceFunction) buildObject(sourceDef, fluxContext);
        DataStreamSource dataStreamSource = senv.addSource(sourceFunction, sourceDef.getId());

        // set compilation results
        vertex.setDataStream(dataStreamSource);
        fluxContext.addSource(sourceDef.getId(), dataStreamSource);
    }

    public static void compileOperator(
            FluxContext fluxContext,
            CompilationVertex vertex) throws Exception {
        if (vertex.getIncomingEdge().size() != 1) {
            throw new UnsupportedOperationException(
                    "Cannot compile zero input or multiple input operators as this moment");
        }
        // Fetch upstream
        OperatorDef operatorDef = (OperatorDef) vertex.getVertex();
        String sourceId = vertex.getIncomingEdge().get(0).getFrom();
        CompilationVertex source = fluxContext.getCompilationVertex(sourceId);
        DataStream sourceStream = source.getDataStream();

        // Compile vertex
        OneInputStreamOperator operator = (OneInputStreamOperator) buildObject(operatorDef, fluxContext);
        DataStream stream = sourceStream.transform(operatorDef.getId(), resolveTypeInformation(operatorDef.getTypeInformation()), operator);

        // set compilation results
        vertex.setDataStream(stream);
        fluxContext.addOperator(operatorDef.getId(), operator);
    }

    public static void compileSink(
            FluxContext fluxContext,
            CompilationVertex vertex) throws Exception {
        if (vertex.getIncomingEdge().size() != 1) {
            throw new UnsupportedOperationException(
                    "Cannot compile zero input or multiple input sink as this moment");
        }
        // Fetch upstream
        SinkDef sinkDef = (SinkDef) vertex.getVertex();
        String sourceId = vertex.getIncomingEdge().get(0).getFrom();
        CompilationVertex source = fluxContext.getCompilationVertex(sourceId);
        DataStream sourceStream = source.getDataStream();

        // Compile vertex
        SinkFunction sink = (SinkFunction) buildObject(sinkDef, fluxContext);
        DataStreamSink streamSink = sourceStream.addSink(sink);

        // set compilation results
        fluxContext.addSink(sinkDef.getId(), streamSink);
    }

    public static Object buildObject(ObjectDef def, FluxContext fluxContext) throws Exception {
        Class clazz = Class.forName(def.getClassName());
        Object obj = null;
        if (def.hasConstructorArgs()) {
            LOG.debug("Found constructor arguments in definition: " + def.getConstructorArgs().getClass().getName());
            List<Object> cArgs = def.getConstructorArgs();

            if(def.hasReferences()){
                cArgs = resolveReferences(cArgs, fluxContext);
            }

            Constructor con = findCompatibleConstructor(cArgs, clazz);
            if (con != null) {
                LOG.debug("Found something seemingly compatible, attempting invocation...");
                obj = con.newInstance(getArgsWithListCoercian(cArgs, con.getParameterTypes()));
            } else {
                String msg = String.format("Couldn't find a suitable constructor for class '%s' with arguments '%s'.",
                        clazz.getName(),
                        cArgs);
                throw new IllegalArgumentException(msg);
            }
        } else {
            obj = clazz.newInstance();
        }
        applyProperties(def, obj, fluxContext);
        invokeConfigMethods(def, obj, fluxContext);
        return obj;
    }

    private static void applyProperties(ObjectDef bean, Object instance, FluxContext context) throws Exception {
        List<PropertyDef> props = bean.getProperties();
        Class clazz = instance.getClass();
        if (props != null) {
            for (PropertyDef prop : props) {
                Object value = prop.isReference() ? context.getComponent(prop.getRef()) : prop.getValue();
                Method setter = findSetter(clazz, prop.getName(), value);
                if (setter != null) {
                    LOG.debug("found setter, attempting to invoke");
                    // invoke setter
                    setter.invoke(instance, new Object[]{value});
                } else {
                    // look for a public instance variable
                    LOG.debug("no setter found. Looking for a public instance variable...");
                    Field field = findPublicField(clazz, prop.getName(), value);
                    if (field != null) {
                        field.set(instance, value);
                    }
                }
            }
        }
    }
    private static void invokeConfigMethods(ObjectDef bean, Object instance, FluxContext context)
            throws InvocationTargetException, IllegalAccessException {

        List<ConfigMethodDef> methodDefs = bean.getConfigMethods();
        if(methodDefs == null || methodDefs.size() == 0){
            return;
        }
        Class clazz = instance.getClass();
        for(ConfigMethodDef methodDef : methodDefs){
            List<Object> args = methodDef.getArgs();
            if (args == null){
                args = new ArrayList<>();
            }
            if(methodDef.hasReferences()){
                args = resolveReferences(args, context);
            }
            String methodName = methodDef.getName();
            Method method = findCompatibleMethod(args, clazz, methodName);
            if(method != null) {
                Object[] methodArgs = getArgsWithListCoercian(args, method.getParameterTypes());
                method.invoke(instance, methodArgs);
            } else {
                String msg = String.format("Unable to find configuration method '%s' in class '%s' with arguments %s.",
                        methodName, clazz.getName(), args);
                throw new IllegalArgumentException(msg);
            }
        }
    }

    // ------------------------------------------------------------------------
    // Type utilities
    // ------------------------------------------------------------------------

    private static TypeInformation resolveTypeInformation(String typeInformation) {
        switch (typeInformation.toLowerCase()) {
            case "string": return BasicTypeInfo.STRING_TYPE_INFO;
            case "boolean": return BasicTypeInfo.BOOLEAN_TYPE_INFO;
            case "byte": return BasicTypeInfo.BYTE_TYPE_INFO;
            case "short": return BasicTypeInfo.SHORT_TYPE_INFO;
            case "int": return BasicTypeInfo.INT_TYPE_INFO;
            case "long": return BasicTypeInfo.LONG_TYPE_INFO;
            case "float": return BasicTypeInfo.FLOAT_TYPE_INFO;
            case "double": return BasicTypeInfo.DOUBLE_TYPE_INFO;
            case "char": return BasicTypeInfo.CHAR_TYPE_INFO;
            case "date": return BasicTypeInfo.DATE_TYPE_INFO;
            case "void": return BasicTypeInfo.VOID_TYPE_INFO;
            default:
                throw new IllegalArgumentException("operator type info is not supported: " + typeInformation);
        }
    }

    // ------------------------------------------------------------------------
    // Field setter and getter utilities
    // ------------------------------------------------------------------------

    private static Field findPublicField(Class clazz, String property, Object arg) throws NoSuchFieldException {
        Field field = clazz.getField(property);
        return field;
    }

    private static Method findSetter(Class clazz, String property, Object arg) {
        String setterName = toSetterName(property);
        Method retval = null;
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (setterName.equals(method.getName())) {
                LOG.debug("Found setter method: " + method.getName());
                retval = method;
            }
        }
        return retval;
    }

    private static String toSetterName(String name) {
        return "set" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
    }

}
