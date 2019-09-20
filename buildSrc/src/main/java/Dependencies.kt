object Versions {
    const val appCompat = "1.1.0-rc01"
    const val lifecycleVersion = "2.0.0"
    const val coreKtx = "1.1.0-rc03"
    const val constraintLayout = "2.0.0-beta2"
    const val materialComponents = "1.0.0"
    const val retrofit = "2.6.1"
    const val retrofitLogger = "4.1.0"

    const val kotlin = "1.3.41"

    const val junit = "4.12"
    const val androidJunit = "1.1.0"
    const val espressoCore = "3.1.1"

    const val rxJava = "2.2.11"
    const val rxAndroid = "2.1.1"
    const val rxKotlin = "2.4.0"

    const val daggerAndroid = "2.24"
    const val gson = "2.8.5"
    const val mockitoKotlin = "2.1.0"
}

object BuildDeps {
    const val AGP = "com.android.tools.build:gradle:3.5.0"
    const val KGP = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object AndroidDeps {
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitRxJavaAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofitLogger = "com.squareup.okhttp3:logging-interceptor:${Versions.retrofitLogger}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.daggerAndroid}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.daggerAndroid}"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifecycleVersion}"
    const val liveData = "androidx.lifecycle:lifecycle-livedata:${Versions.lifecycleVersion}"
    const val lifecycleCommon = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycleVersion}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val materialComponents = "com.google.android.material:material:1.0.0"
}

object AndroidTestDeps {
    const val androidJunit = "androidx.test.ext:junit:${Versions.androidJunit}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
    const val liveDataHelper = "androidx.arch.core:core-testing:${Versions.lifecycleVersion}"
}

object JvmDeps{
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val javaxInject = "javax.inject:javax.inject:1"
    const val dagger = "com.google.dagger:dagger-android:${Versions.daggerAndroid}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.daggerAndroid}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
}

object JvmTestDeps {
    const val junit = "junit:junit:${Versions.junit}"
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
    const val mockitoInline = "org.mockito:mockito-inline:3.0.0"
}