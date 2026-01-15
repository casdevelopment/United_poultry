plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs.kotlin") version "2.7.3"
    id ("kotlin-parcelize")

}

android {
    namespace = "com.example.unitedpoultry"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.unitedpoultry"
        minSdk = 24
        targetSdk = 36
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
    //noinspection DataBindingWithoutKapt
    android.buildFeatures.dataBinding = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.3")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.3")

    implementation ("com.google.android.material:material:1.9.0")

    implementation ("androidx.gridlayout:gridlayout:1.0.0")

    implementation ("com.google.android.material:material:1.11.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")


// Gson converter for Retrofit
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

// Coroutines support for Retrofit
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// OkHttp logging (optional, useful for debugging)
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

// Koin
    implementation("io.insert-koin:koin-core:3.2.1")
    implementation("io.insert-koin:koin-android:3.2.1")



    //Add KTX dependencies
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    implementation ("com.github.bumptech.glide:glide:4.16.0")


}

