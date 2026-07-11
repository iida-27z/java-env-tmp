plugins {
	java
  jacoco
  alias(libs.plugins.spotless)
	alias(libs.plugins.spring.boot)
}

group = "com.iidadevworks"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

spotless {
  java {
    googleJavaFormat(libs.versions.googleJavaFormat.get())
    endWithNewline()
    trimTrailingWhitespace()
    leadingTabsToSpaces()
    importOrder()
    removeUnusedImports()
    toggleOffOn()
  }
}

repositories {
	mavenCentral()
}

dependencies {
  // Spring Boot BOMの依存関係
	listOf(
		configurations.implementation,
    configurations.annotationProcessor,
    configurations.developmentOnly,
    configurations.testImplementation,
    configurations.testAnnotationProcessor
	).forEach { config ->
		add(config.name, platform(libs.spring.boot.bom))
	}
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-flyway")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("org.flywaydb:flyway-database-postgresql")
	annotationProcessor("org.projectlombok:lombok")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
  testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
  testImplementation("org.springframework.boot:spring-boot-starter-security-test")
  testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testAnnotationProcessor("org.projectlombok:lombok")
	testImplementation("org.testcontainers:testcontainers-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers-postgresql")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// その他の依存関係
  testImplementation(libs.playwright)
}

tasks.withType<Test> {
	useJUnitPlatform()
  dependsOn("spotlessJavaCheck")
  finalizedBy("jacocoTestReport")
  if (project.hasProperty("quick")) {
        exclude("**/*IT.class")
  }
  if(project.hasProperty("skipUnitTest")) {
        exclude("**/*Test.class")
  }
}

tasks.register<JavaExec>("playwright") {
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.microsoft.playwright.CLI")
}
