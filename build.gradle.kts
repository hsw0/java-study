plugins {
    `project-report`
    id("conventions.project.spotless")
}

spotless {
    format("dotfiles") {
        target(".gitignore", ".gitattributes", ".editorconfig")
        indentWithSpaces(2)
        trimTrailingWhitespace()
        endWithNewline()
    }
}
