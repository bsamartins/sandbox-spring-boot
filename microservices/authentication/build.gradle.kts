dependencies {
    implementation("org.springframework.security.oauth:spring-security-oauth2")
}

application {
    mainClassName = "pt.bsamartins.sandbox.springboot.microservices.authentication.AuthenticationApplication"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }
}