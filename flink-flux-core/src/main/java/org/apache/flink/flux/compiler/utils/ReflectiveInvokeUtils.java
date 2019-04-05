package org.apache.flink.flux.compiler.utils;

import org.apache.flink.flux.compiler.FluxContext;
import org.apache.flink.flux.model.ComponentReferenceDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

final class ReflectiveInvokeUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ReflectiveInvokeUtils.class);

    /**
     * Given a list of constructor arguments, and a target class, attempt to find a suitable constructor.
     * @param args argument list
     * @param target target classs
     * @return constructor method
     * @throws NoSuchMethodException cannot be found
     */
    static Constructor findCompatibleConstructor(List<Object> args, Class target) throws NoSuchMethodException {
        Constructor retval = null;
        int eligibleCount = 0;

        LOG.debug("Target class: {}", target.getName());
        Constructor[] cons = target.getDeclaredConstructors();

        for (Constructor con : cons) {
            Class[] paramClasses = con.getParameterTypes();
            if (paramClasses.length == args.size()) {
                LOG.debug("found constructor with same number of args..");
                boolean invokable = canInvokeWithArgs(args, con.getParameterTypes());
                if (invokable) {
                    retval = con;
                    eligibleCount++;
                }
                LOG.debug("** invokable --> {}", invokable);
            } else {
                LOG.debug("Skipping constructor with wrong number of arguments.");
            }
        }
        if (eligibleCount > 1) {
            LOG.warn("Found multiple invokable constructors for class {}, given arguments {}. Using the last one found.",
                    target, args);
        }
        return retval;
    }

    /**
     * Find referenced objects based on component name.
     *
     * @param args arguments
     * @param context the flux compilation context used to search for reference objects.
     * @return java.lang.Method
     */
    static List<Object> resolveReferences(List<Object> args, FluxContext context) {
        LOG.debug("Checking arguments for references.");
        List<Object> cArgs = new ArrayList<Object>();
        // resolve references
        for (Object arg : args) {
            if (arg instanceof ComponentReferenceDef) {
                cArgs.add(context.getComponent(((ComponentReferenceDef) arg).getId()));
            } else {
                cArgs.add(arg);
            }
        }
        return cArgs;
    }

    /**
     * Find compatible methods for a specific list of arguments and a class reference.
     *
     * @param args arguments
     * @param target target class
     * @param methodName method name
     * @return java.lang.Method
     */
    static Method findCompatibleMethod(List<Object> args, Class target, String methodName){
        Method retval = null;
        int eligibleCount = 0;

        LOG.debug("Target class: {}", target.getName());
        Method[] methods = target.getMethods();

        for (Method method : methods) {
            Class[] paramClasses = method.getParameterTypes();
            if (paramClasses.length == args.size() && method.getName().equals(methodName)) {
                LOG.debug("found constructor with same number of args..");
                boolean invokable = false;
                if (args.size() == 0){
                    // it's a method with zero args
                    invokable = true;
                } else {
                    invokable = canInvokeWithArgs(args, method.getParameterTypes());
                }
                if (invokable) {
                    retval = method;
                    eligibleCount++;
                }
                LOG.debug("** invokable --> {}", invokable);
            } else {
                LOG.debug("Skipping method with wrong number of arguments.");
            }
        }
        if (eligibleCount > 1) {
            LOG.warn("Found multiple invokable methods for class {}, method {}, given arguments {}. " +
                            "Using the last one found.",
                    new Object[]{target, methodName, args});
        }
        return retval;
    }

    /**
     * Determine if the given constructor/method parameter types are compatible given arguments List. Consider if
     * list coercian can make it possible.
     *
     * @param args arguments
     * @param parameterTypes parameter types for setting
     * @return whether can be invoked from
     */
    private static boolean canInvokeWithArgs(List<Object> args, Class[] parameterTypes) {
        if (parameterTypes.length != args.size()) {
            LOG.warn("parameter types were the wrong size");
            return false;
        }

        for (int i = 0; i < args.size(); i++) {
            Object obj = args.get(i);
            Class paramType = parameterTypes[i];
            Class objectType = obj.getClass();
            LOG.debug("Comparing parameter class {} to object class {} to see if assignment is possible.",
                    paramType, objectType);
            if (paramType.equals(objectType)) {
                LOG.debug("Yes, they are the same class.");
            } else if (paramType.isAssignableFrom(objectType)) {
                LOG.debug("Yes, assignment is possible.");
            } else if (isPrimitiveBoolean(paramType) && Boolean.class.isAssignableFrom(objectType)){
                LOG.debug("Yes, assignment is possible.");
            } else if(isPrimitiveNumber(paramType) && Number.class.isAssignableFrom(objectType)){
                LOG.debug("Yes, assignment is possible.");
            } else if(paramType.isEnum() && objectType.equals(String.class)){
                LOG.debug("Yes, will convert a String to enum");
            } else if (paramType.isArray() && List.class.isAssignableFrom(objectType)) {
                // TODO more collection content type checking
                LOG.debug("Assignment is possible if we convert a List to an array.");
                LOG.debug("Array Type: {}, List type: {}", paramType.getComponentType(), ((List) obj).get(0).getClass());
            } else {
                return false;
            }
        }
        return true;
    }

    private static boolean isPrimitiveNumber(Class clazz){
        return clazz.isPrimitive() && !clazz.equals(boolean.class);
    }

    private static boolean isPrimitiveBoolean(Class clazz){
        return clazz.isPrimitive() && clazz.equals(boolean.class);
    }

    /**
     * Given a java.util.List of contructor/method arguments, and a list of parameter types, attempt to convert the
     * list to an java.lang.Object array that can be used to invoke the constructor. If an argument needs
     * to be coerced from a List to an Array, do so.
     *
     * @param args list of arguments
     * @param parameterTypes list of parameter types
     * @return argument object list.
     */
    static Object[] getArgsWithListCoercian(List<Object> args, Class[] parameterTypes) {
//        Class[] parameterTypes = constructor.getParameterTypes();
        if (parameterTypes.length != args.size()) {
            throw new IllegalArgumentException("Contructor parameter count does not egual argument size.");
        }
        Object[] constructorParams = new Object[args.size()];

        // loop through the arguments, if we hit a list that has to be convered to an array,
        // perform the conversion
        for (int i = 0; i < args.size(); i++) {
            Object obj = args.get(i);
            Class paramType = parameterTypes[i];
            Class objectType = obj.getClass();
            LOG.debug("Comparing parameter class {} to object class {} to see if assignment is possible.",
                    paramType, objectType);
            if (paramType.equals(objectType)) {
                LOG.debug("They are the same class.");
                constructorParams[i] = args.get(i);
                continue;
            }
            if (paramType.isAssignableFrom(objectType)) {
                LOG.debug("Assignment is possible.");
                constructorParams[i] = args.get(i);
                continue;
            }
            if (isPrimitiveBoolean(paramType) && Boolean.class.isAssignableFrom(objectType)){
                LOG.debug("Its a primitive boolean.");
                Boolean bool = (Boolean)args.get(i);
                constructorParams[i] = bool.booleanValue();
                continue;
            }
            if(isPrimitiveNumber(paramType) && Number.class.isAssignableFrom(objectType)){
                LOG.debug("Its a primitive number.");
                Number num = (Number)args.get(i);
                if(paramType == Float.TYPE){
                    constructorParams[i] = num.floatValue();
                } else if (paramType == Double.TYPE) {
                    constructorParams[i] = num.doubleValue();
                } else if (paramType == Long.TYPE) {
                    constructorParams[i] = num.longValue();
                } else if (paramType == Integer.TYPE) {
                    constructorParams[i] = num.intValue();
                } else if (paramType == Short.TYPE) {
                    constructorParams[i] = num.shortValue();
                } else if (paramType == Byte.TYPE) {
                    constructorParams[i] = num.byteValue();
                } else {
                    constructorParams[i] = args.get(i);
                }
                continue;
            }

            // enum conversion
            if(paramType.isEnum() && objectType.equals(String.class)){
                LOG.debug("Yes, will convert a String to enum");
                constructorParams[i] = Enum.valueOf(paramType, (String)args.get(i));
                continue;
            }

            // List to array conversion
            if (paramType.isArray() && List.class.isAssignableFrom(objectType)) {
                // TODO more collection content type checking
                LOG.debug("Conversion appears possible...");
                List list = (List) obj;
                LOG.debug("Array Type: {}, List type: {}", paramType.getComponentType(), list.get(0).getClass());

                // create an array of the right type
                Object newArrayObj = Array.newInstance(paramType.getComponentType(), list.size());
                for (int j = 0; j < list.size(); j++) {
                    Array.set(newArrayObj, j, list.get(j));

                }
                constructorParams[i] = newArrayObj;
                LOG.debug("After conversion: {}", constructorParams[i]);
            }
        }
        return constructorParams;
    }
}
