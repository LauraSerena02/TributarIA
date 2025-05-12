plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
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

        testInstrumentationRunner = "com.example.tributaria.utils.CustomTestRunner"
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

kapt {
    correctErrorTypes = true
}

dependencies {

    //Navigation
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    //Library for Visibility, VisibilityOff

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation ("androidx.compose.material3:material3:1.2.0")

    implementation ("androidx.compose.foundation:foundation:1.4.0")
    implementation ("androidx.compose.foundation:foundation-layout:1.4.0")

    implementation("io.coil-kt:coil-compose:2.2.0")

    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation ("com.google.dagger:hilt-android:2.48")
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.48")

    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    //viewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    //LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation ("androidx.compose.runtime:runtime-livedata:1.4.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")// Para convertir las respuestas a objetos

    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    //Fragment
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    //Activity
    implementation("androidx.activity:activity-compose:1.7.2")

    implementation ("com.google.accompanist:accompanist-swiperefresh:0.27.0")


    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))


// TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    // Add the dependencies for any other desired Firebase products
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.android.gms:play-services-auth")

    // Koin para Android
    implementation("io.insert-koin:koin-android:3.5.3")

    // Koin para Jetpack Compose
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")




    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("io.insert-koin:koin-androidx-compose:3.5.3")

    implementation("com.patrykandpatrick.vico:core:1.13.0")
    implementation("com.patrykandpatrick.vico:compose:1.13.0")
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.0")
    //Generative IA
    implementation("com.google.ai.client.generativeai:generativeai:0.1.2")



    implementation("io.coil-kt:coil-compose:2.4.0")




}