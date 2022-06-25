plugins {
    `java-library`
    `maven-publish`
    signing
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

group = "com.mammb"
version = "0.5.0"

val sonatypeUsername: String? by project
val sonatypePassword: String? by project

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "jpa-fluent-modelgen"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("jpa fluent modelgen")
                description.set("JPA fluent metamodel generator")
                url.set("https://github.com/naotsugu/jpa-fluent-modelgen")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("naotsugu")
                        name.set("Naotsugu Kobayashi")
                        email.set("naotsugukobayashi@gmail.com")
                    }
                }
                scm {
                    connection.set("git@github.com:naotsugu/jpa-fluent-modelgen.git")
                    developerConnection.set("git@github.com:naotsugu/jpa-fluent-modelgen.git")
                    url.set("https://github.com/naotsugu/jpa-fluent-modelgen")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
