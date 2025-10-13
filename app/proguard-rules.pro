// =================================================================================================
// 1. تنظیمات پلاگین (Groovy DSL)
// =================================================================================================
apply plugin: 'com.android.application'
// اگر از کد Kotlin در پروژه جدید استفاده می‌کنید، این خط را فعال کنید:
// apply plugin: 'kotlin-android'
// اگر از Kapt برای Room یا Glide استفاده می‌کنید:
// apply plugin: 'kotlin-kapt'


// =================================================================================================
// 2. متغیرهای نسخه‌ها (با استفاده از بلاک ext) - بدون نیاز به libs.versions.toml
// =================================================================================================
ext {
    // Android SDK
    compileSdk = 35
    targetSdk = 35
    minSdk = 21 // به 21 ارتقا داده شد برای Multidex بهتر و سازگاری با کتابخانه‌های مدرن

    // AndroidX & Google Versions
    androidx_appcompat = "1.6.1"
    androidx_activity = "1.8.2"
    material = "1.11.0"
    multidex = "2.0.1"
    
    // Core Libraries
    room = "2.6.1"
    rxjava3 = "3.1.6"
    rxandroid3 = "3.0.2"
    retrofit = "2.9.0"
    glide = "4.16.0"
    sentry = "7.5.0"
    firebase_messaging = "24.0.0"
    lottie = "6.4.1"
    exoplayer = "2.19.1"
}


// =================================================================================================
// 3. بلاک Android
// =================================================================================================
android {
    compileSdkVersion ext.compileSdk
    namespace 'ntk.android.base'
    
    defaultConfig {
        applicationId "ntk.android.baseactivity"
        minSdkVersion ext.minSdk
        targetSdkVersion ext.targetSdk
        versionCode 1
        versionName "1.0"
        multiDexEnabled true
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // ارتقاء به Java 17
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    signingConfigs {
        release {
            storeFile file('..\\..\\keys\\key.jks')
            storePassword "APPstorePassword"
            keyAlias "APPkeyAlias"
            keyPassword "APPkeyPassword"
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
        debug {
            minifyEnabled false
        }
    }
}


// =================================================================================================
// 4. وابستگی‌ها (Dependencies)
// *توجه: از annotationProcessor استفاده شده است*
// =================================================================================================
dependencies {
    // Core AndroidX
    implementation "androidx.appcompat:appcompat:${androidx_appcompat}"
    implementation "com.google.android.material:material:${material}"
    implementation "androidx.activity:activity:${androidx_activity}"
    implementation "androidx.multidex:multidex:${multidex}"
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    // Architecture Components (Room & RxJava)
    implementation "androidx.room:room-runtime:${room}"
    // اگر Kotlin استفاده می‌کنید، از kapt یا ksp به جای annotationProcessor استفاده کنید.
    annotationProcessor "androidx.room:room-compiler:${room}" 
    implementation "androidx.room:room-rxjava2:${room}" 

    // RxJava 3
    implementation "io.reactivex.rxjava3:rxjava:${rxjava3}"
    implementation "io.reactivex.rxjava3:rxandroid:${rxandroid3}"

    // Networking
    implementation "com.squareup.retrofit2:retrofit:${retrofit}"
    implementation "com.squareup.retrofit2:converter-gson:${retrofit}"

    // Image Loading
    implementation "com.github.bumptech.glide:glide:${glide}"
    annotationProcessor "com.github.bumptech.glide:compiler:${glide}"
    implementation 'com.nostra13.universalimageloader:universal-image-loader:1.9.5' 

    // Utilities & Firebase
    implementation "io.sentry:sentry-android:${sentry}"
    implementation "com.google.firebase:firebase-messaging:${firebase_messaging}"
    implementation 'org.greenrobot:eventbus:3.3.1'
    implementation 'com.github.GrenderG:Toasty:1.5.2'
    implementation "com.airbnb.android:lottie:${lottie}"

    // Other UI Components
    implementation 'com.balysv:material-ripple:1.0.2'
    implementation 'com.flaviofaria:kenburnsview:1.0.7'
    implementation 'me.zhanghai.android.materialratingbar:library:1.3.1'
    implementation 'com.pnikosis:materialish-progress:1.0'

    // Exoplayer & PhotoView
    implementation "com.google.android.exoplayer:exoplayer:${exoplayer}"
    implementation 'com.github.chrisbanes:PhotoView:2.3.0'

    // QR Code
    implementation 'com.github.idindevelop:QRGenerator:1.0.1'
    implementation 'com.google.zxing:core:3.3.2'

    // Java 8 Features (RetroStreams)
    implementation 'net.sourceforge.streamsupport:android-retrostreams:1.7.2'

    // Base API
    api project(path: ':baseApi')
}