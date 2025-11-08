plugins {
    id("java")
    id ("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("ru.hse.de.vkcli.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}