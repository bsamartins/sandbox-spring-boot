dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-client")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-netflix-eureka-server")
}

application {
    mainClassName = "pt.bsamartins.sandbox.springboot.microservices.registry.RegistryApplication"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }
}