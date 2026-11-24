plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.omniroute"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aistudio.omniroute.gateway"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(21)
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    debugImplementation(libs.androidx.ui.tooling)
}

tasks.register("incrementVersionCode") {
    description = "Automatically increments the versionCode in app/build.gradle.kts"
    group = "versioning"
    doLast {
        val buildFile = file("build.gradle.kts")
        val pattern = Regex("versionCode\\s*=\\s*(\\d+)")
        val content = buildFile.readText()
        val match = pattern.find(content)
        if (match != null) {
            val current = match.groupValues[1].toInt()
            val updated = content.replaceFirst("versionCode = $current", "versionCode = ${current + 1}")
            buildFile.writeText(updated)
            println("VersionCode incremented from $current to ${current + 1}")
        } else {
            println("No versionCode pattern found in ${buildFile.path}")
        }
    }
}
