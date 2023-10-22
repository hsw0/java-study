package io.syscall.hsw.study.lambda;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.syscall.hsw.study.lambda.SerializableProcedure.Fn2;
import java.lang.invoke.MethodHandles;
import java.util.function.BiFunction;
import java.util.function.IntBinaryOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MethodReferenceTest {

    final Logger log = LoggerFactory.getLogger(getClass());

    final MethodReferenceResolver sut = new MethodReferenceResolver(MethodHandles.lookup());

    static class Adder {

        static int staticAdd(int x, int y) {
            return x + y;
        }

        int add(int x, int y) {
            return x + y;
        }
    }


    @Test
    void resolveLambda1() {
        int z = 1;
        var r = assertDoesNotThrow(() -> sut.resolve((Integer x, Integer y) -> x + y + z));
        log.info("Result: {}", r);
    }

    @Test
    void resolveLambda2() {
        @SuppressWarnings("Convert2MethodRef")
        Fn2<Integer, Integer, Integer> fn = (Integer x, Integer y) -> x + y;
        var r = assertDoesNotThrow(() -> sut.resolve(fn));
        log.info("Result: {}", r);
    }

    @Test
    void resolveLambda2a() {
        @SuppressWarnings("Convert2MethodRef")
        BiFunction<Integer, Integer, Integer> fn = (x, y) -> x + y;
        var r = assertDoesNotThrow(() -> sut.resolve(fn::apply));
        log.info("Result: {}", r);
    }

    @Test
    void resolveStaticMethod() {
        var r = assertDoesNotThrow(() -> sut.resolve(Adder::staticAdd));
        log.info("Result: {}", r);
    }

    @Test
    void resolveStaticMethod2() {
        var r = assertDoesNotThrow(() -> sut.resolve(Integer::sum));
        log.info("Result: {}", r);
    }

    @Test
    void resolveInstanceBound() {
        var adder = new Adder();
        var r = assertDoesNotThrow(() -> sut.resolve(adder::add));
        log.info("Result: {}", r);
    }

    @Test
    void resolveInstanceMethod() {
        var r = assertDoesNotThrow(() -> sut.resolve(Adder::add));
        log.info("Result: {}", r);
    }

    @Test
    void resolveInterface() {
        var r = assertDoesNotThrow(() -> sut.resolve(IntBinaryOperator::applyAsInt));
        log.info("Result: {}", r);
    }
}
