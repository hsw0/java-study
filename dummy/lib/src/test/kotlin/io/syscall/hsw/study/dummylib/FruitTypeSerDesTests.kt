package io.syscall.hsw.study.dummylib

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import tools.jackson.core.StreamReadFeature
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.jacksonMapperBuilder
import tools.jackson.module.kotlin.readValue

private val log = KotlinLogging.logger {}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FruitTypeSerDesTests {

    val customStrawberry: FruitType.Other
        get() =
            FruitType.Other(
                "strawberry",
                "red",
                taste = "sweet",
            )
    val customStrawberryJson = """{"kind":"strawberry","color":"red","taste":"sweet"}"""
    val appleJson = """{"kind":"apple","color":"red"}"""
    val bananaJson = """{"kind":"banana","color":"yellow"}"""
    val orangeJson = """{"kind":"orange","color":"orange"}"""

    val jsonMapper: JsonMapper =
        jacksonMapperBuilder {
            enable(KotlinFeature.SingletonSupport)
        }.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
            .build()

    @Test
    fun testSerialize() {
        jsonMapper.writeValueAsString(FruitType.Apple) shouldBe appleJson
        jsonMapper.writeValueAsString(FruitType.Banana) shouldBe bananaJson
        jsonMapper.writeValueAsString(FruitType.Orange) shouldBe orangeJson
        jsonMapper.writeValueAsString(customStrawberry) shouldBe customStrawberryJson
    }

    @Test
    fun testDeserializeNamed() {
        jsonMapper.readValue<FruitType>(appleJson) shouldBeSameInstanceAs FruitType.Apple
        jsonMapper.readValue<FruitType>(bananaJson) shouldBeSameInstanceAs FruitType.Banana
        jsonMapper.readValue<FruitType>(orangeJson) shouldBeSameInstanceAs FruitType.Orange
    }

    @Test
    fun testDeserializeOther() {
        jsonMapper.readValue<FruitType>(customStrawberryJson) shouldBe customStrawberry
    }
}
