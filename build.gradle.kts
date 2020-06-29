buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        val kotlinVersion = project.findProperty("version.kotlin") as String
        val springBootVersion = project.findProperty("version.spring-boot") as String

        classpath(kotlin("gradle-plugin", version="1.3.72"))
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    dependencies {
        val implementation by configurations
        val testImplementation by configurations

        val springBootVersion = project.findProperty("version.spring-boot")
        val springCloudVersion = project.findProperty("version.spring-cloud")

        implementation(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
        implementation(platform("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion"))

        testImplementation(platform("org.junit:junit-bom:5.6.2"))

        constraints {
            add("implementation", "org.springframework.security.oauth:spring-security-oauth2:2.5.0.RELEASE")
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}