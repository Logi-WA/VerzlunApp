plugins { 
    id("com.android.application") 
    id("org.jetbrains.kotlin.android") // Ensure Kotlin plugin is applied
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
        jvmTarget = "17" // Ensure Kotlin is using the correct JVM target
    }

    // ✅ Enable ViewBinding & DataBinding
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.3")

    // ✅ Fix Kotlin duplicate class issue: Use only kotlin-stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// ✅ Force Kotlin 1.8.22 to prevent duplicate class conflicts
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
    }
}
