plugins {
    java
    id("org.springframework.boot") version "3.0.0-M1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {

    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:6.0.0.Final")
    annotationProcessor("com.mammb:jpa-fluent-modelgen:0.1.0")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("com.h2database:h2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
