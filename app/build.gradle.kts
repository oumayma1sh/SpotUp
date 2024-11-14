plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "tn.esprit.spotup"
    compileSdk = 34

    defaultConfig {
        applicationId = "tn.esprit.spotup"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        resources {
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
            // Add other exclusions if necessary
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // Ajout de Glide
    implementation(libs.glide) // bibliothèque Glide
    // Ajout de CardView via l'alias défini dans libs.versions.toml
    implementation(libs.cardview)
    implementation ("androidx.drawerlayout:drawerlayout:1.2.0")
    // Dépendances JavaMail compatibles avec Android
    // Add only the latest JavaMail API dependency and exclude any older versions
    implementation("com.sun.mail:android-mail:1.6.5") {
        exclude(group = "com.sun.mail", module = "javax.mail")  // Ensure exclusions are handled
    }
    implementation("com.sun.mail:android-activation:1.6.5")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("mysql:mysql-connector-java:8.0.29")

    implementation(libs.material.calendar.view)
    //implementation(libs.androidx.constraintlayout)
    implementation(libs.support.annotations)
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    //implementation(libs.androidx.activity)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    implementation("com.github.dhaval2404:imagepicker:2.1")


    implementation("com.google.android.material:material:1.9.0")
    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")

}