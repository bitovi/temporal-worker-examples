plugins {
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
	application
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("io.grpc:grpc-netty-shaded:1.43.2")
	implementation("io.netty:netty-handler:4.1.72.Final")
	implementation(kotlin("stdlib"))
	implementation("io.temporal:temporal-sdk:1.13.0")
	implementation("org.springframework.boot:spring-boot-starter-web") {
			exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
	}
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.mockito:mockito-core:4.0.0")
	testImplementation("org.mockito:mockito-inline:4.0.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("io.temporal:temporal-testing:1.13.0")
	testImplementation(kotlin("test"))
	developmentOnly("org.springframework.boot:spring-boot-devtools")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

application {
	mainClass.set("com.example.temporalWorker.AppKt")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
