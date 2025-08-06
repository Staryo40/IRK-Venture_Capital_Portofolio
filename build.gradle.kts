plugins {
    application
    id("java")
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "irk.staryo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("irk.staryo.Main")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.graphics")
}
