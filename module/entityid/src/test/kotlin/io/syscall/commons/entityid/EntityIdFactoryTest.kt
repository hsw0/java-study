package io.syscall.commons.entityid

import io.github.oshai.kotlinlogging.KotlinLogging
import io.syscall.commons.entityid.LongEntityIdSupport.factory
import io.syscall.commons.entityid.StringEntityIdFactorySupport.factory
import io.syscall.commons.entityid.test.ISBN
import io.syscall.commons.entityid.test.PersonId
import org.assertj.core.api.Assertions.assertThat
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets
import java.util.Base64
import kotlin.test.Test

private val log = KotlinLogging.logger {}

class EntityIdFactoryTest {

    @Test
    fun `Create from string`() {
        assertThat(PersonId.create("ALICE")).isEqualTo(PersonId.ALICE)
        assertThat(PersonId.create("BOB")).isNotEqualTo(PersonId.MALLORY)
        assertThat(PersonId.create("MALLORY")).isNotEqualTo(PersonId.CHARLIE)
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
