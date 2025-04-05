plugins {
    application
    id("io.freefair.lombok") version "8.7.1"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenLocal()
    mavenCentral()
}

application {
    mainClass = "com.github.stachu540.CSViewer"
}

javafx {
    version = "22.0.1"
    modules(
        "javafx.controls",
        "javafx.fxml",
        "javafx.swing"
    )
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.12.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testfx:testfx-junit5:4.0.18")
    testImplementation("org.hamcrest:hamcrest:3.0")
}

group = "com.github.stachu540"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_22
    targetCompatibility = JavaVersion.VERSION_22
}

tasks {
    test {
        useJUnitPlatform()
    }
}
