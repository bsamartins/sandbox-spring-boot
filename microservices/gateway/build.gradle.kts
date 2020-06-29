dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-client")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-zuul")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
}

application {
    mainClassName = "pt.bsamartins.sandbox.springboot.microservices.gateway.GatewayApplication"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }
}