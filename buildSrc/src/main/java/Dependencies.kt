object Versions {
    const val appCompat = "1.2.0-rc01"
    const val lifecycleVersion = "2.1.0"
    const val coreKtx = "1.3.0"
    const val constraintLayout = "2.0.0-beta7"
    const val materialComponents = "1.2.0-beta01"
    const val androidXTest = "1.2.0"
    const val retrofit = "2.9.0"
    const val retrofitLogger = "4.7.2"
    const val mockitoAndroid = "3.3.3"
    const val picasso = "2.71828"

    const val kotlin = "1.3.72"

    const val junit = "4.13"
    const val androidJunit = "1.1.0"
    const val espressoCore = "3.1.1"

    const val rxJava = "2.2.19"
    const val rxAndroid = "2.1.1"
    const val rxKotlin = "2.4.0"

    const val daggerAndroid = "2.28.1"
    const val gson = "2.8.6"
    const val mockitoKotlin = "2.2.0"
    const val dexOpenerVersion = "2.0.4"
}

object BuildDeps {
    const val AGP = "com.android.tools.build:gradle:4.0.0"
    const val KGP = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object AndroidDeps {
    const val picasso = "com.squareup.picasso:picasso:${Versions.picasso}"
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
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val materialComponents =
        "com.google.android.material:material:${Versions.materialComponents}"
}

object AndroidTestDeps {
    const val androidJunit = "androidx.test.ext:junit:${Versions.androidJunit}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
    const val liveDataHelper = "androidx.arch.core:core-testing:${Versions.lifecycleVersion}"
    const val androidXtestRunner = "androidx.test:runner:${Versions.androidXTest}"
    const val androidXtestRules= "androidx.test:rules:${Versions.androidXTest}"
    const val dexOpener = "com.github.tmurakami:dexopener:${Versions.dexOpenerVersion}"
    const val mockitoAndroid = "org.mockito:mockito-android:${Versions.mockitoAndroid}"
    const val mockitoCore = "org.mockito:mockito-core:${Versions.mockitoAndroid}"
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
    const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoAndroid}"
}