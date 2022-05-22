plugins {
    java
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

repositories {
    mavenCentral()
}

dependencies {

    annotationProcessor("org.hibernate:hibernate-jpamodelgen:5.6.9.Final")
    annotationProcessor("com.mammb:jpa-fluent-modelgen:0.3.0")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("com.h2database:h2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}
