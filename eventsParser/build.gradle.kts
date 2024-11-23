plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    application
}

group = "com.rozoomcool"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-context:6.1.14")


    // Selenium Java API
    implementation("org.seleniumhq.selenium:selenium-java:4.26.0")

    // Chrome Driver для Selenium
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.26.0")
    implementation("org.apache.pdfbox:pdfbox:2.0.29")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // WebDriver Manager для автоматической установки драйвера
    implementation("io.github.bonigarcia:webdrivermanager:5.9.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
//    implementation("org.springframework.kafka:spring-kafka")

    // Apache PDFBox для обработки PDF
    implementation("org.apache.pdfbox:pdfbox:2.0.29")

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.12")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

application {
    mainClass.set("MainKt")
}