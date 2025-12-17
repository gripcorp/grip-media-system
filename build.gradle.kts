import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    extra["kotlinVersion"] = "2.1.0"
    extra["springBootVersion"] = "3.4.1"
    extra["kotestVersion"] = "5.9.1"
    System.setProperty("kotlinVersion", project.extra["kotlinVersion"] as String)
    System.setProperty("springBootVersion", project.extra["springBootVersion"] as String)

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${project.extra["kotlinVersion"]}")
        classpath("org.jetbrains.kotlin:kotlin-allopen:${project.extra["kotlinVersion"]}")
        classpath("org.jetbrains.kotlin:kotlin-noarg:${project.extra["kotlinVersion"]}")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${project.extra["springBootVersion"]}")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

plugins {
    val kotlinVersion = System.getProperty("kotlinVersion")
    val springBootVersion = System.getProperty("springBootVersion")
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
}

defaultTasks("clean", "build")

group = "show.grip.mms"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("io.github.oshai:kotlin-logging:7.0.3")

    // AWS SDK
    implementation(platform("software.amazon.awssdk:bom:2.29.50"))
    implementation("software.amazon.awssdk:ivs")
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:secretsmanager")

    // Database
    runtimeOnly("com.mysql:mysql-connector-j")

    // Redis
    implementation("io.lettuce:lettuce-core:6.5.2.RELEASE")

    // Utilities
    implementation("commons-io:commons-io:2.18.0")
    implementation("org.hashids:hashids:1.0.3")
    implementation("joda-time:joda-time:2.13.0")
    implementation("com.google.guava:guava:33.4.0-jre")

    // OpenAPI/Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3")

    // Logging
    implementation("com.sndyuk:logback-more-appenders:1.8.8")
    implementation("org.fluentd:fluent-logger:0.3.4")

    // Configuration Processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("io.kotest:kotest-runner-junit5:${project.extra["kotestVersion"]}")
    testImplementation("io.kotest:kotest-assertions-core-jvm:${project.extra["kotestVersion"]}")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "21"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "21"
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

extra["profile"] = if(!project.hasProperty("profile")) "local" else project.property("profile")
println ("build profile : ${project.extra["profile"]}")

sourceSets {
    main {
        java.srcDirs("src/main/kotlin")

        resources {
            srcDirs ("src/main/resources", "src/main/resources-${project.extra["profile"]}")
            println ("java.srcDirs = ${java.srcDirs}")
            println ("resources.srcDirs = ${resources.srcDirs}")
        }
    }

    test {
        java {
        }

        resources {
        }
    }
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
