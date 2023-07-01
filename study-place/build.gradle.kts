plugins {
    id("conventions.project.kotlin")
}

customJava {
    lintOption("synchronization", true)
}
