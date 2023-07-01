plugins {
    id("conventions.project.kotlin")
    application
}

group = "io.syscall.hsw.study"
version = "1.0-SNAPSHOT"

customJava {
    javaModuleName.set("io.syscall.hsw.study.dummyapp")
}

application {
    mainModule.set("io.syscall.hsw.study.dummyapp")
    mainClass.set("io.syscall.hsw.study.dummyapp.Main")
}



dependencies {
    implementation(project(":dummy-library"))
}
