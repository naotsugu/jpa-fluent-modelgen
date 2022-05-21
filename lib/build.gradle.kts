plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("jakarta.persistence:jakarta.persistence-api:3.0.0")
    testAnnotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:6.0.0.Final")
    testAnnotationProcessor(project(":lib", "archives"))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
