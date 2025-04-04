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
//    implementation("org.apache.commons:commons-csv:1.11.0")
//    implementation(platform("com.fasterxml.jackson:jackson-bom:2.17.2"))
//    implementation("org.controlsfx:controlsfx:11.2.1")
//    implementation("com.google.guava:guava:33.2.1-jre")
//    implementation("de.siegmar:fastcsv:3.2.0")
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
