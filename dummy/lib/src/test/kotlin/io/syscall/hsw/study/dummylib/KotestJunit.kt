package io.syscall.hsw.study.dummylib

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class KotestJunit {

    @Test
    fun `kotest Assertion 학습`() {
        val thrown = shouldThrow<AssertionError> {
            listOf("반동분자").shouldBeEmpty()
        }
        thrown.message
            .shouldContain("Collection should be empty")
            .shouldContain("반동분자")
    }
}
