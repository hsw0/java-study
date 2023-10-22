package io.syscall.hsw.study.lambda;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.invoke.SerializedLambda;

class LambdaSerializer extends ObjectOutputStream {

    public static SerializedLambda apply(SerializableProcedure fn) {
        try (var serializer = new LambdaSerializer()) {
            serializer.writeObject(fn);
        } catch (YieldException e) {
            return e.value;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        throw new IllegalArgumentException("Failed to serialize lambda");
    }

    private LambdaSerializer() throws IOException {
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
