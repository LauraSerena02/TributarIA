plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tributaria"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tributaria"
        minSdk = 24
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

    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    //Navigation
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    //Library for Visibility, VisibilityOff

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation ("androidx.compose.material3:material3:1.2.0")

    implementation ("androidx.compose.foundation:foundation:1.4.0")
    implementation ("androidx.compose.foundation:foundation-layout:1.4.0")


    //viewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    //LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation ("androidx.compose.runtime:runtime-livedata:1.4.0")
    implementation ("androidx.compose.material3:material3:1.1.1")
    //Fragment
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    //Activity
    implementation("androidx.activity:activity-compose:1.7.2")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))


// TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    // Add the dependencies for any other desired Firebase products
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")






    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}