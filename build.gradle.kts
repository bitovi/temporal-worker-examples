plugins {
    id("org.springframework.boot") version "3.3.1" apply false
	id("io.spring.dependency-management") version "1.1.5" apply false
	kotlin("plugin.spring") version "1.9.24" apply false
    kotlin("jvm") version "1.9.24" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}