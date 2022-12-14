plugins {
    id("java")
}

group = "io.github.ithotl"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.onarandombox.com/content/groups/public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    annotationProcessor("org.jetbrains:annotations:23.0.0")
    implementation("org.jetbrains:annotations:23.0.0")
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("com.onarandombox.multiverseportals:Multiverse-Portals:4.2.1")
    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:4.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}



tasks.getByName<Test>("test") {
    useJUnitPlatform()
}