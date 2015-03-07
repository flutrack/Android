##Tips for using the app with Eclipse or Android Studio
1. If you are using Android studio follow [these instructions](https://developer.android.com/sdk/installing/migrate.html), otherwise for Eclipse Select Import -> Existing Android Code Into Workspace. In both cases ensure that you have the latest version of google play services installed. If you do not Select Window > Android SDK Manager. Scroll to the bottom of the package list and Select Extras > Google Play services. The Google Play services SDK is downloaded to your computer and installed in your Android SDK environment at <android-sdk-folder>/extras/google/google_play_services/. **Extra step for Eclipse**: Click File -> Import..., Select Android -> Existing Android Code into Workspace, browse to and Select <android-sdk-folder>/extras/google/google_play_services/libproject/google-play-services_lib.

2. If after importing the project Eclipse or Android Studio do not recognize the APIs that the google play services library provides:
    1. For Eclipse :To add the dependency to Google Play Services into the project go to Project -> Properties -> Android -> Library, Add -> google-play-services_lib.
    2. For Android Studio: Go to File -> Project Structure -> Select 'Project Settings' -> Select 'Dependencies' Tab Click '+' -> 1.Library Dependencies -> Select com.google.android.gms:play-services:+.
        Alternatively you can import the library by modifying the build.gradle file inside your application module directory. Add a new build rule under dependencies for the latest version of play-services. For example:
    ```
dependencies {
    compile 'com.android.support:appcompat-v7:21.0.3'
    compile 'com.google.android.gms:play-services:6.5.87'
}
   ```

In the fluMap and reportSymptoms fragments replace the empty string values (url2, url) with the urls for the get_json.php and submit_flu.php files.

Unless you are using an Android Emulator, you need a device that can run at least **Android 4.0 (Api Level: 14)** in order to use or debug the changes that you have made to the FluTrack app.

