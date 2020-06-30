dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-server")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClassName = "pt.bsamartins.sandbox.springboot.microservices.config.ConfigurationApplication"
}