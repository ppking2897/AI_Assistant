import java.util.Properties // 這一行請加在最上面！


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)

    // Make sure that you have the Google services Gradle plugin
    id("com.google.gms.google-services")

    // Add the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics")
    kotlin("kapt")
}



val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}
val openWeatherMapApiKey: String = localProperties.getProperty("OPEN_WEATHER_MAP_API_KEY") ?: ""


hilt {
    enableAggregatingTask = false
}

android {
    namespace = "com.bianca.ai_assistant"
    compileSdk = 35

    defaultConfig {
        multiDexEnabled = true
        applicationId = "com.bianca.ai_assistant"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "OPEN_WEATHER_MAP_API_KEY", "\"$openWeatherMapApiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "LOG_DEBUG", "true")
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false

            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }

        release {
            buildConfigField("boolean", "LOG_DEBUG", "false")
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    flavorDimensions+="environment"

    productFlavors {
        create("beta") {
            buildConfigField("String", "ENVIRONMENT_TYPE", "\"beta\"")
            dimension = "environment"
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta"
        }

        create("product") {
            buildConfigField("String", "ENVIRONMENT_TYPE", "\"product\"")
            dimension = "environment"
            applicationIdSuffix = ".product"
            versionNameSuffix = "-product"
        }

    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.appcheck.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)
    androidTestImplementation(libs.androidx.navigation.testing)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    androidTestImplementation(libs.hilt.android.testing)
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.android.compiler)
    kaptAndroidTest(libs.hilt.android.compiler)



    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.coroutines.android)

    //region lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    //    implementation 'androidx.lifecycle:lifecycle-extensions'
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // Saved state module for ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    // alternately - if using Java8, use the following instead of lifecycle-compiler
    implementation(libs.androidx.lifecycle.common.java8)
    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
    implementation(libs.androidx.lifecycle.process)
    // optional - helpers for implementing LifecycleOwner in a Service
    implementation(libs.androidx.lifecycle.service)
    // optional - Test helpers for LiveData
    testImplementation(libs.androidx.core.testing)
    // optional - ReactiveStreams support for LiveData
    implementation(libs.androidx.lifecycle.reactivestreams.ktx)
    implementation(libs.androidx.runtime.livedata)


    // NET
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.gson)

    //log
    implementation(libs.timber)


    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    // optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.15.0")) // 已更新為最新穩定版本
    implementation("com.google.firebase:firebase-ai:16.1.0")
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation("com.google.firebase:firebase-appcheck-playintegrity")

    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.ui.text.google.fonts)

    implementation(libs.coil.compose)
    implementation(libs.coil.gif)


    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work) // Hilt 注入 Worker 用
    kapt(libs.androidx.hilt.compiler)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(kotlin("test"))


    testImplementation(libs.turbine)


    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")

    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("com.kizitonwose.calendar:compose:2.7.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

}