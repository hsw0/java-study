package io.syscall.hsw.study.sampledomain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class PersonIdTest {

    @Test
    fun `create with valid positive value`() {
        val id = PersonId.create(1L)
        assertThat(id.value).isEqualTo(1L)
    }

    @Test
    fun `create with large value`() {
        val id = PersonId.create(Long.MAX_VALUE)
        assertThat(id.value).isEqualTo(Long.MAX_VALUE)
    }

    @Test
    fun `create with zero throws exception`() {
        assertThatThrownBy { PersonId.create(0L) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `create with negative value throws exception`() {
        assertThatThrownBy { PersonId.create(-1L) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `create from string`() {
        val id = PersonId.create("42")
        assertThat(id.value).isEqualTo(42L)
    }

    @Test
    fun `EVERYONE constant has value -1`() {
        assertThat(PersonId.EVERYONE.value).isEqualTo(-1L)
    }

    @Test
    fun `equality works correctly`() {
        val id1 = PersonId.create(1L)
        val id2 = PersonId.create(1L)
        val id3 = PersonId.create(2L)

        assertThat(id1).isEqualTo(id2)
        assertThat(id1).isNotEqualTo(id3)
    }
}
