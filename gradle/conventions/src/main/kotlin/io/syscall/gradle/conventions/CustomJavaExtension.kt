package io.syscall.gradle.conventions

abstract class CustomJavaExtension {

    companion object {
        const val LINT_ARG_PREFIX = "-Xlint:"
    }

    private val lintOptions: MutableMap<String, Boolean> = HashMap()

    fun lintOption(key: String, value: Boolean) {
        lintOptions.put(key, value)
    }

    fun buildCompilerArgs(): List<String> {
        return lintOptions.map {
            LINT_ARG_PREFIX + (
                if (it.value) {
                    ""
                } else {
                    "-"
                }
                ) + it.key
        }
    }

    init {
        lintOptions += mapOf(
            "all" to true,
            "try" to false,
            "processing" to false,
        )
    }
}
