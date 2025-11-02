package io.syscall.hsw.study.dummylib

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "kind",
    visible = true,
    defaultImpl = FruitType.Other::class,
)
@JsonSubTypes(
    failOnRepeatedNames = true,
    value = [
        JsonSubTypes.Type(FruitType.Apple::class),
        JsonSubTypes.Type(FruitType.Banana::class),
        JsonSubTypes.Type(FruitType.Orange::class),
    ],
)
@JsonPropertyOrder(value = ["kind", "color"])
sealed interface FruitType {
    val kind: String
    val color: String?

    @JsonTypeName(Apple.KIND)
    @JsonIgnoreProperties(ignoreUnknown = true)
    // or: @JsonIgnoreProperties(value = ["kind", "color"], allowGetters = true)
    data object Apple : FruitType {
        const val KIND = "apple"
        override val kind = KIND
        override val color: String = "red"

        @JvmStatic
        @JsonCreator
        fun instance() = this
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeName(Banana.NAME)
    data object Banana : FruitType {
        const val NAME = "banana"
        override val kind = NAME
        override val color: String = "yellow"

        @JvmStatic
        @JsonCreator
        fun instance() = this
    }

    @JsonTypeName(Orange.NAME)
    @JsonIgnoreProperties(ignoreUnknown = true)
    data object Orange : FruitType {
        const val NAME = "orange"
        override val kind = NAME
        override val color: String = "orange"

        @JvmStatic
        @JsonCreator
        fun instance() = this
    }

    data class Other(
        override val kind: String,
        override val color: String?,
        val taste: String?,
    ) : FruitType
}
