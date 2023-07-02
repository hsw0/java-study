package io.syscall.hsw.study.checkerframework;

import java.util.HashSet;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CheckerExample {

    @SuppressWarnings("argument.type.incompatible")
    void ex() {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        Set<@NonNull String> set = new HashSet<>();
        set.add("first");
        set.add(null);
    }
}
