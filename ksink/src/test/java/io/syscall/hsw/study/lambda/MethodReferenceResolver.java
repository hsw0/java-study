package io.syscall.hsw.study.lambda;

import io.syscall.hsw.study.lambda.LambdaSerializer.YieldException;
import io.syscall.hsw.study.lambda.SerializableProcedure.Fn2;
import io.syscall.hsw.study.lambda.SerializableProcedure.Fn3;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class MethodReferenceResolver {

    private final Lookup lookup;

    public MethodReferenceResolver(Lookup lookup) {
        this.lookup = lookup;
    }

    public <R, A0, A1> MethodReferenceInfo resolve(Fn2<R, A0, A1> lambda) {
        return doResolve(lambda);
    }

    public <R, A0, A1, A2> MethodReferenceInfo resolve(Fn3<R, A0, A1, A2> lambda) {
        return doResolve(lambda);
    }

    protected MethodReferenceInfo doResolve(SerializableProcedure lambda) {
        SerializedLambda serialized = getSerializedLambda(lambda);

        Class<?> implClass;
        Method implMethod;

        MethodType implMT, instantiatedMT;
        try {
            var implClassName = serialized.getImplClass().replace('/', '.');
            implClass = lookup.findClass(implClassName);
            var cl = implClass.getClassLoader();

            implMT = MethodType.fromMethodDescriptorString(serialized.getImplMethodSignature(), cl);
            instantiatedMT = MethodType.fromMethodDescriptorString(serialized.getInstantiatedMethodType(), cl);
            implMethod = implClass.getDeclaredMethod(serialized.getImplMethodName(), implMT.parameterArray());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }

        RefKind kind = deduceRefKind(serialized, implMethod, instantiatedMT);
        Object receiver = null;
        if (kind == RefKind.BOUND_METHOD && serialized.getCapturedArgCount() == 1) {
            receiver = serialized.getCapturedArg(0);
        }

        return new MethodReferenceInfo(kind, implMethod, receiver);
    }

    static RefKind deduceRefKind(SerializedLambda serialized, Method method, MethodType instantiatedMT) {
        var s = MethodHandleInfo.referenceKindToString(serialized.getImplMethodKind());
        // com.sun.tools.javac.util.Names.lambda
        if (method.isSynthetic() && method.getName().contains("lambda$")) {
            return RefKind.LAMBDA;
        }

        var kind =
                switch (s) {
                    case "invokeStatic" -> RefKind.STATIC_METHOD;
                    case "invokeVirtual", "invokeInterface" -> RefKind.METHOD;
                    default -> throw new IllegalStateException("Unexpected value: " + s);
                };

        if (kind == RefKind.METHOD && method.getParameterCount() == instantiatedMT.parameterCount()) {
            kind = RefKind.BOUND_METHOD;
        }

        return kind;
    }

    private SerializedLambda getSerializedLambda(SerializableProcedure fn) {
        try (var serializer = new LambdaSerializer()) {
            serializer.writeObject(fn);
        } catch (YieldException e) {
            return e.value;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return null;
    }
}
