import java.util.*

plugins {
    application
    kotlin("jvm") version "1.3.72"
    id("com.google.cloud.tools.jib")
}

group = "Mpeix Backend"
version = "2020.0.0"

application {
    mainClass.set("com.kekmech.schedule.MainKt")
}

repositories {
    mavenCentral()
    jcenter()
}

fun ktor(module: String, version: String? = "_") = "io.ktor:ktor-$module${version?.let { ":$version" } ?: ""}"
fun jooq(module: String = "", version: String? = "_") = "org.jooq:jooq${if(module.isNotBlank()) "-$module" else ""}${version?.let { ":$version" } ?: ""}"

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    implementation(platform(ktor("bom")))
    implementation(ktor("server-netty"))
    implementation(ktor("gson"))
    implementation(ktor("locations"))
    implementation(ktor("metrics-micrometer"))
    implementation(ktor("client-gson"))
    implementation(ktor("client-okhttp"))
    implementation(ktor("network-tls"))

    implementation("ch.qos.logback:logback-classic:_")
    implementation("com.squareup.okhttp3:logging-interceptor:_")

    implementation("io.github.config4k:config4k:_")

    implementation("com.github.ben-manes.caffeine:caffeine:_")

    implementation("org.koin:koin-core:_")
    implementation("org.koin:koin-ktor:_")

    implementation("org.postgresql:postgresql:_")
    implementation("com.zaxxer:HikariCP:_")

    implementation(jooq())
    implementation(jooq("meta"))
    implementation(jooq("codegen"))

    testImplementation("org.junit.jupiter:junit-jupiter:_")
}

fun getImageVersion() = buildString {
    append(version)
    val buildVersion = System.getenv("BUILD_VERSION")
    if(buildVersion != null) {
        append("-")
        append(buildVersion)
    }
}

jib {
    val (imageName, dockerHubLogin, dockerHubPassword) =
        File("credentials").readLines().let { Triple(it[0], it[1], it[2]) }
    to {
        image = imageName
        tags = tags + "latest"
        auth {
            username = dockerHubLogin
            password = dockerHubPassword
        }
        container {
            mainClass = "com.kekmech.schedule.MainKt"
        }
    }
    from {
        image = "gcr.io/distroless/java:11"
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs += "-Xopt-in=io.ktor.locations.KtorExperimentalLocationsAPI"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    test {
        useJUnitPlatform()
    }
}
