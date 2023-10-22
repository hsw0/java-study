package io.syscall.hsw.study.lambda;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.invoke.SerializedLambda;

class LambdaSerializer extends ObjectOutputStream {

    public LambdaSerializer() throws IOException {
        super(NullOutputStream.INSTANCE);
        enableReplaceObject(true);
    }

    @Override
    protected Object replaceObject(Object obj) {
        if (obj instanceof SerializedLambda lambda) {
            throw new YieldException(lambda);
        }
        return obj;
    }

    static class YieldException extends RuntimeException {

        final SerializedLambda value;

        public YieldException(SerializedLambda value) {
            this.value = value;
        }

        public Throwable fillInStackTrace() {
            return this;
        }
    }

    static class NullOutputStream extends OutputStream {

        static final NullOutputStream INSTANCE = new NullOutputStream();

        private NullOutputStream() {
        }

        public void write(int b) {
        }
    }
}
