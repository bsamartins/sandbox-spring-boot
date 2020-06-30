dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-client")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClassName = "pt.bsamartins.sandbox.springboot.microservices.testservice.TestServiceApplication"
}