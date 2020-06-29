subprojects {
    apply<JavaPlugin>()
    apply<ApplicationPlugin>()
    apply(plugin="org.springframework.boot")

    dependencies {
        val implementation by configurations
        val testImplementation by configurations

        implementation(platform("org.springframework.cloud:spring-cloud-dependencies:Dalston.SR5"))
        implementation(platform("org.springframework.cloud:spring-cloud-config:1.4.1.RELEASE"))
        implementation(platform("org.springframework.cloud:spring-cloud-netflix:1.4.2.RELEASE"))
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.slf4j:slf4j-api:1.7.18")

        testImplementation("junit:junit:4.12")
    }
}

tasks.register<Exec>("composeBuild") {
    subprojects.forEach {
        dependsOn("${it.name}:build")
    }
    commandLine = listOf("docker-compose", "build")
}