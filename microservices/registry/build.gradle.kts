dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-eureka")
    implementation("org.springframework.cloud:spring-cloud-netflix-eureka-server")
    implementation("org.springframework.cloud:spring-cloud-config-client")
}

application {
    mainClassName = "pt.bsamartins.sandbox.springboot.microservices.registry.RegistryApplication"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }
}