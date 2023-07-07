package io.syscall.commons.entityid

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlin.test.Test

@JvmInline
private value class DummyId(override val value: Long) : LongEntityId

class WellKnownValueSupportTest {

    private val sut = WellKnownValueSupport(::DummyId)

    @Suppress("RedundantNullableReturnType")
    @Test
    fun `Basic declare`() {
        val name = "1234s"
        val value = 0x1111_2222_3333_4444

        val declared: DummyId? = sut.declare(name, value)

        declared!!.value shouldBe value
        sut[name] shouldBe declared
        sut[declared] shouldBe name
        sut.internIfExists(DummyId(value)) shouldBeSameInstanceAs declared
    }

    @Test
    fun `Undeclared value`() {
        val name = "1234s"
        val value = 0x1111_2222_3333_4444

        sut[name] shouldBe null
        sut[DummyId(value)] shouldBe null
        sut.internIfExists(DummyId(value)) shouldBe DummyId(value)
        sut.internIfExists(DummyId(value)) shouldNotBeSameInstanceAs DummyId(value)
    }

    @Test
    fun `No duplicate names`() {
        sut.declare("a", 1)

        shouldThrow<IllegalArgumentException> { sut.declare("a", 2) }
    }

    @Test
    fun `No duplicate values`() {
        sut.declare("a", 1)

        shouldThrow<IllegalArgumentException> { sut.declare("b", 1) }
    }
}
