subprojects {
    apply<JavaPlugin>()
    apply<ApplicationPlugin>()
    apply(plugin="org.springframework.boot")

    dependencies {
        val implementation by configurations
        val testImplementation by configurations

        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-actuator")

        testImplementation("org.junit:junit-jupiter-api")
    }
}

tasks.register<Exec>("composeBuild") {
    subprojects.forEach {
        dependsOn("${it.name}:build")
    }
    commandLine = listOf("docker-compose", "build")
}