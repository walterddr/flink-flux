package org.apache.flink.flux.test.utils.operator;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.operators.ProcessOperator;
import org.apache.flink.util.Collector;

public class BasicPassThroughOperator extends ProcessOperator<String, String> {

    public BasicPassThroughOperator(ProcessFunction<String, String> function) {
        super(function);
    }

    public BasicPassThroughOperator() {
        this(PassThroughFunction.getInstance());
    }

    private static class PassThroughFunction extends ProcessFunction<String, String> {

        static PassThroughFunction INSTANCE = new PassThroughFunction();

        @Override
        public void processElement(String s, Context context, Collector<String> collector) throws Exception {
            collector.collect(s);
        }

        public static PassThroughFunction getInstance() {
            return INSTANCE;
        }
    }
}
