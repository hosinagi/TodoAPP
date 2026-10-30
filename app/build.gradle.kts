plugins {
    alias(libs.plugins.android.application)
    //在模块build.gradle中引入序列化插件
    id ("kotlin-parcelize")
}

android {
    namespace = "com.hosinagi.todoapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.hosinagi.todoapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    // Toasty多样式弹窗
    implementation("com.github.GrenderG:Toasty:1.5.2")
    // 图片加载库
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    // lottie动画库
    implementation("com.airbnb.android:lottie:6.4.0")
    // Gson依赖
    implementation("com.google.code.gson:gson:2.12.1")
    // okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // 日志拦截器
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // 下拉刷新
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    //SmartRefreshLayout
    implementation("io.github.scwang90:refresh-layout-kernel:3.0.0-alpha")
    implementation("io.github.scwang90:refresh-header-classics:3.0.0-alpha")
    implementation("io.github.scwang90:refresh-header-radar:3.0.0-alpha")
    // 权限申请PermissionX
    implementation("com.guolindev.permissionx:permissionx:1.8.1")
    // ExoPlayer dependencies
    implementation("androidx.media3:media3-exoplayer:1.7.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.7.1")
    implementation("androidx.media3:media3-ui:1.7.1")
}