import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.6"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"

    /**
     * detekt
     *
     * URL
     * - https://github.com/detekt/detekt
     * GradlePlugins(plugins.gradle.org)
     * - https://plugins.gradle.org/plugin/io.gitlab.arturbosch.detekt
     * Main用途
     * - Linter/Formatter
     * Sub用途
     * - 無し
     * 概要
     * KotlinのLinter/Formatter
     */
    id("io.gitlab.arturbosch.detekt") version "1.21.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    /**
     * detektの拡張: detekt-formatting
     *
     * 概要
     * - formattingのルール
     * - 基本はktlintと同じ
     * - format自動適用オプションの autoCorrect が使えるようになる
     */
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
