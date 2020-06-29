buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        val kotlinVersion = project.findProperty("version.kotlin") as String
        val springBootVersion = project.findProperty("version.spring-boot") as String

        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

subprojects {
    repositories {
        mavenCentral()
    }
}