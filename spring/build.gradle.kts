plugins {
    id("java")
}

group = "com.neptune.spring"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    implementation("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    optional("me.clip.placeholderapi:placeholderapi:3.1.8")
    optional("com.sk89q.worldguard:worldguard-core:7.0.2")
    optional("me.luckperms:luckperms-api:5.3.0")
}

tasks {
    processResources {
        inputs.files(project.file("src/main/resources/config.json"))
        outputs.file(project.file("build/generated/resources/config.json"))
    }
}
