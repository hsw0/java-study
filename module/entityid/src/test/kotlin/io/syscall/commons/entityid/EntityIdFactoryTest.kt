package io.syscall.commons.entityid

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.syscall.commons.entityid.LongEntityIdSupport.factory
import io.syscall.commons.entityid.StringEntityIdSupport.factory
import io.syscall.commons.entityid.test.ISBN
import io.syscall.commons.entityid.test.PersonId
import org.assertj.core.api.Assertions.assertThat
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets
import java.util.Base64
import kotlin.reflect.KClass
import kotlin.test.Test

private val log = KotlinLogging.logger {}

class EntityIdFactoryTest {

    @JvmInline
    internal value class DummyLongId(override val value: Long) : LongEntityId

    @JvmInline
    internal value class DummyStringId(override val value: String) : StringEntityId

    @JvmInline
    private value class Inaccessible(override val value: Long) : LongEntityId

    internal data class AsDataClass(override val value: Long) : LongEntityId

    internal class AsPlainClass(override val value: Long) : LongEntityId

    @JvmInline
    internal value class Failing(override val value: Long) : LongEntityId {
        companion object {
            init {
                throw ExceptionInInitializerError("Something went wrong")
            }
        }
    }

    @Suppress("RedundantNullableReturnType")
    @Test
    fun `Basic LongEntityId factory`() {
        val factory = LongEntityId.factory<DummyLongId>()
        val created: DummyLongId? = factory.create(314159)

        assertThat(created).isInstanceOf(DummyLongId::class.java)
        created?.value shouldBe 314159

        shouldNotThrowAny {
            factory(DummyLongId::class.java).create(111)
        }
    }

    @Suppress("RedundantNullableReturnType")
    @Test
    fun `Basic StringEntityId factory`() {
        val factory = StringEntityId.factory<DummyStringId>()
        val created: DummyStringId? = factory.create("Sample value")

        assertThat(created).isInstanceOf(DummyStringId::class.java)
        created?.value shouldBe "Sample value"

        shouldNotThrowAny {
            factory(DummyStringId::class.java).create("aaa")
        }
    }

    @Test
    fun `Various class types`() {
        shouldNotThrowAny { LongEntityId.factory<AsDataClass>() }
        shouldNotThrowAny { LongEntityId.factory<AsPlainClass>() }
    }

    @Test
    fun `Error handling`() {
        val personIdFactory = LongEntityId.factory<PersonId>()
        shouldThrow<IllegalArgumentException> { personIdFactory.create(Long.MIN_VALUE) }
            .message shouldContain "Failed requirement"
    }

    @Test
    fun `Unsupported case - Class visibility`() {
        shouldThrow<UnsupportedEntityIdImplementationException> { LongEntityId.factory<Inaccessible>() }
    }

    @Test
    fun `Unsupported case - Not a subclass of  EntityId`() {
        shouldThrow<UnsupportedEntityIdImplementationException> {
            @Suppress("UNCHECKED_CAST")
            LongEntityId.factory(Void::class as KClass<DummyLongId>)
        }
    }

    @Test
    fun `Unsupported case - Initialization problem`() {
        shouldThrow<UnsupportedEntityIdImplementationException> { LongEntityId.factory<Failing>() }
    }

    @Test
    fun `Create from string`() {
        assertThat(PersonId.create("ALICE")).isEqualTo(PersonId.ALICE)
        assertThat(PersonId.create("BOB")).isNotEqualTo(PersonId.MALLORY)
        assertThat(PersonId.create("MALLORY")).isNotEqualTo(PersonId.CHARLIE)

        log.info { "\"ALICE\": value=${PersonId.create("ALICE").value} toString=${PersonId.create("ALICE")}" }
        log.info { "\"5234123\": value=${PersonId.create("5234123").value} toString=${PersonId.create("5234123")}" }
    }

    @Test
    fun asString() {
        val alice = PersonId.create("ALICE")
        val randomPerson = PersonId.create("5234123")
        log.info { "\"ALICE\": value=${alice.value} asString=${alice.asString()} toString=${alice}" }
        log.info { "\"5234123\": value=${randomPerson.value} asString=${randomPerson.asString()} toString=${randomPerson}" }
    }

    @Test
    fun test() {
        val personIdFactory = LongEntityId.factory<PersonId>()
        val personId = personIdFactory.create(123456)
        log.info { "personId=$personId, everyone=${PersonId.EVERYONE}" }
        assertThat(personId.value).isEqualTo(123456)

        if (personIdFactory is FromStringEntityIdFactory<*, *>) {
            val pFromStr = personIdFactory.create("12345")
            log.info { "personId=$pFromStr" }
        }

        val isbnFactory = StringEntityId.factory<ISBN>()
        val isbn = isbnFactory.create("978-3-16-148410-0")
        log.info { "isbn=$isbn" }

        val baos = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(baos)
        objectOutputStream.writeObject(personId)

        objectOutputStream.flush()
        objectOutputStream.close()

        val b64Encoder = Base64.getEncoder()
        val toBase64 = { it: ByteArray -> String(b64Encoder.encode(it), StandardCharsets.ISO_8859_1) }

        log.info { "serialized=${toBase64(baos.toByteArray())}" }

        val objectInputStream = ObjectInputStream(ByteArrayInputStream(baos.toByteArray()))
        val deserialized = objectInputStream.readObject()
        log.info { "deserialized=${deserialized}" }

        assertThat(deserialized).describedAs("deserialized").isEqualTo(personId)
    }
}
