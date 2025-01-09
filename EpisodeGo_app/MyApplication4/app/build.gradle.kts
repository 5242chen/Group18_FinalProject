import org.gradle.api.JavaVersion

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        resources {
            excludes += "META-INF/*.SF"
            excludes += "META-INF/*.DSA"
            excludes += "META-INF/spring-configuration-metadata.json"
            excludes += "META-INF/spring.factories"
            excludes += "META-INF/*"
            excludes += "META-INF/DEPENDENCIES.txt"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/dependencies.txt"
            excludes += "META-INF/LGPL2.1"
            excludes += "META-INF/spring.schemas"
            excludes += "META-INF/spring.tooling"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/spring.handlers"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        multiDexEnabled = true  // 如果方法超過 65K，也可以啟用這個選項
    }
    kotlinOptions {
        jvmTarget = "17"
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("org.jsoup:jsoup:1.15.4")
    implementation ("org.springframework:spring-web:5.3.29")
    implementation ("org.springframework.boot:spring-boot-starter-web:2.5.5")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0") // 用來將 JSON 轉換為對象
    implementation ("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
}

