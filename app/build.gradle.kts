import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val id_token = localProperties.getProperty("ID_TOKEN") ?: ""



android {
    namespace = "com.rodrigoloq.chatapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.rodrigoloq.chatapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String","ID_TOKEN","\"$id_token\"")
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
    packagingOptions{
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/INDEX.LIST")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.volley)
    testImplementation(libs.junit)

    //icons extended
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    //navigation https://developer.android.com/develop/ui/compose/navigation?hl=es-419
    implementation("androidx.navigation:navigation-compose:2.9.7")
    //Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.11.0"))
    // Add the dependency for the Firebase Authentication library
    implementation("com.google.firebase:firebase-auth")
    // Add the dependency for the Realtime Database library
    implementation("com.google.firebase:firebase-database")
    // google sign-in
    implementation("com.google.android.gms:play-services-auth:21.5.1")
    //coil
    implementation("io.coil-kt.coil3:coil-compose:3.4.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.4.0")
    // firebase storage
    implementation("com.google.firebase:firebase-storage")
    // photo zoom
    implementation("net.engawapg.lib:zoomable:2.11.1")
    // messaging
    implementation ("com.google.firebase:firebase-messaging-ktx:24.1.2")
    // messaging auth
    implementation("com.google.auth:google-auth-library-oauth2-http:1.46.0")
    implementation("com.google.api-client:google-api-client:2.9.0")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}