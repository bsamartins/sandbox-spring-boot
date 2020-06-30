dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-client")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-netflix-eureka-server")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClassName = "pt.bsamartins.sandbox.springboot.microservices.registry.RegistryApplication"
}
