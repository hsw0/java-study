plugins {
    `project-report`
    `build-dashboard`
    alias(libs.plugins.spotless)
}

spotless {
    format("dotfiles") {
        target(".gitignore", ".gitattributes", ".editorconfig")
        indentWithSpaces(2)
        trimTrailingWhitespace()
        endWithNewline()
    }
}
