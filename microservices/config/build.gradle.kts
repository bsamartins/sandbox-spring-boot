dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-server")
}

application {
    mainClassName = "pt.bsamartins.sandbox.springboot.microservices.config.ConfigurationApplication"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }
}