dependencies {
    implementation("org.springframework.security.oauth:spring-security-oauth2")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClassName = "pt.bsamartins.sandbox.springboot.microservices.authentication.AuthenticationApplication"
}