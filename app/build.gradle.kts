plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("org.jetbrains.kotlin.kapt")
    id("com.github.ben-manes.versions") version "0.52.0"
}

android {
    namespace = "is.hi.hbv601g.verzlunapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "is.hi.hbv601g.verzlunapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    lint {
        abortOnError = false
    }
}

/*kapt {
    javacOptions {
        option("--add-opens", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
        option("--add-opens", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
        option("--add-opens", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED")
        option("--add-opens", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED")
    }
}*/

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    // JSON converter (usually Gson)
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

}
