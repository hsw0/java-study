package io.syscall.gradle.conventions

import org.gradle.api.provider.Provider

inline fun <reified S> Provider<S>.ifPresent(block: (S) -> Unit) {
    if (isPresent) {
        block(get())
    }
}
