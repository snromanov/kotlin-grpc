import com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm")
    id("idea")
    id("io.gitlab.arturbosch.detekt").version("1.19.0")
    id("com.adarshr.test-logger") version "3.2.0"
    id("io.qameta.allure") version "2.9.6"
}

subprojects {
    group = "com.lunar"
    version = "0.0.1"
}

val versionOfDetect: String by project
val versionOfKotlin: String by project
val jacksonVersion: String by project
val versionOfLogback: String by project
val allureVersion: String by project
val versionOfCfg4j: String by project
val versionOfKonfig: String by project
val versionOfKotest: String by project
val jaywayVersion: String by project
val kafkaVersion: String by project
val kotlinxVersion: String by project
val kotestDateTimeVersion: String by project
val kotestPropertyArbs: String by project
val versionOfKotlinLogging: String by project
val versionOfGrpcStubKt: String by project
val versionOfGrpcStub: String by project
val versionOfGrpcProtobuf: String by project
val versionOfProtobuf: String by project
val versionOfJavaxAnnotation: String by project
val nettyVersion: String by project
val versionOfJacksonDataType: String by project
val versionOfKotestArbsProperty: String by project
val versionOfFaker: String by project
val vesrionOfKotestJson: String by project

dependencies {
    implementation(project(":proto"))
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$versionOfDetect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$versionOfKotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$versionOfKotlin")
    implementation("com.natpryce:konfig:$versionOfKonfig")
    implementation(platform("io.kotest:kotest-bom:$versionOfKotest"))
    implementation("io.kotest:kotest-assertions-core")
    implementation("io.kotest:kotest-runner-junit5")
    implementation("io.kotest:kotest-property")
    implementation("io.kotest:kotest-framework-datatest")
    implementation("io.kotest:kotest-assertions-json-jvm:$vesrionOfKotestJson")
    implementation("io.kotest.extensions:kotest-property-arbs:$versionOfKotestArbsProperty")
    implementation("ch.qos.logback:logback-classic:$versionOfLogback")
    implementation("org.cfg4j:cfg4j-core:$versionOfCfg4j")
    implementation("io.github.microutils:kotlin-logging-jvm:$versionOfKotlinLogging")
    implementation("io.grpc:grpc-kotlin-stub:$versionOfGrpcStubKt")
    implementation("io.grpc:grpc-protobuf:$versionOfGrpcProtobuf")
    implementation("com.google.protobuf:protobuf-kotlin:$versionOfProtobuf")
    implementation("io.grpc:grpc-stub:$versionOfGrpcStub")
    implementation("javax.annotation:javax.annotation-api:$versionOfJavaxAnnotation")
    implementation("io.grpc:grpc-testing:$versionOfGrpcProtobuf")
    implementation("io.grpc:grpc-netty:$versionOfGrpcStub")
    implementation("io.grpc:grpc-all:$versionOfGrpcStub")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$versionOfJacksonDataType")
    implementation("io.github.serpro69:kotlin-faker:$versionOfFaker")
}

testlogger {
    theme = MOCHA
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = false
    showCauses = true
    slowThreshold = 2000
    showSummary = true
    showSimpleNames = false
    showPassed = true
    showSkipped = true
    showFailed = true
    showStandardStreams = false
    showPassedStandardStreams = true
    showSkippedStandardStreams = true
    showFailedStandardStreams = true
    logLevel = LogLevel.LIFECYCLE
}

detekt {
    autoCorrect = true
    toolVersion = versionOfDetect
    source = files("src/main/kotlin")
}

task<Wrapper>("wrapper") {
    gradleVersion = gradleVersion
    distributionType = DistributionType.ALL
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-java-parameters",
                "-Xjsr305=strict",
                "-progressive",
                "-Xopt-in=kotlin.RequiresOptIn"
            )
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Detekt> {
        this.jvmTarget = JavaVersion.VERSION_11.toString()
    }

    withType<Test> {
        java.sourceSets["test"].java {
            srcDir("src/main/kotlin")
        }
        useJUnitPlatform()
        systemProperty("java.net.preferIPv4Stack", "true")
        systemProperties(System.getProperties().map { it.key.toString() to it.value }.toMap())
        testLogging {
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_OUT,
                TestLogEvent.STANDARD_ERROR
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }
}
