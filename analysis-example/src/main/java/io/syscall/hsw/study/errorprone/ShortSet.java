package io.syscall.hsw.study.errorprone;

import java.util.HashSet;
import java.util.Set;

/**
 * errorprone sample.
 *
 * @see <a href="https://github.com/google/error-prone">error-prone sample</a>
 */
public class ShortSet {

    @SuppressWarnings("CollectionIncompatibleType")
    public static void main(String[] args) {
        Set<Short> s = new HashSet<>();
        for (short i = 0; i < 100; i++) {
            s.add(i);
            s.remove(i - 1);
        }
        System.out.println(s.size());
    }
}
