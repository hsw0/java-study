package io.syscall.hsw.study.dummyapp;

import java.util.HashSet;
import java.util.Set;

public class ErrorProneExample {
    /**
     * errorprone sample.
     *
     * @see <a href="https://github.com/google/error-prone">error-prone sample</a>
     */
    @SuppressWarnings("CollectionIncompatibleType")
    public static class ShortSet {
        public static void main(String[] args) {
            Set<Short> s = new HashSet<>();
            for (short i = 0; i < 100; i++) {
                s.add(i);
                s.remove(i - 1);
            }
            System.out.println(s.size());
        }
    }
}
