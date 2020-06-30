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

    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
        imageName = "bsamartins-docker-registry.bintray.io/sandbox-spring-boot-microservices/${project.name}"
    }
}