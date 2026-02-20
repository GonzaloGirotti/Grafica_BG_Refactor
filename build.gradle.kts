plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version("8.1.1")
    id("application")
    id("org.hibernate.orm") version "8.0.0.Alpha1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass = "main.Main"
}

repositories {
    mavenCentral()
    google()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.intellij:forms_rt:7.0.3")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    testImplementation(kotlin("test"))
    implementation("org.reflections:reflections:0.10.2")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
    implementation("com.itextpdf:kernel:8.0.2")
    implementation("com.itextpdf:layout:8.0.5")
    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("com.jgoodies:jgoodies-forms:1.6.0")
    implementation("org.swinglabs.swingx:swingx-all:1.6.5-1")
    implementation("org.javatuples:javatuples:1.2")
    implementation("com.google.guava:guava:32.1.2-jre")
    implementation("com.formdev:flatlaf:3.5.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.miglayout:miglayout-swing:5.0")
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
}

tasks.test {
    useJUnitPlatform()
}