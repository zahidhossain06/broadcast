CSE 489 - Assignment 2
Navigation drawer + fragments app (Java, AndroidX / Jetpack)

The drawer (hamburger icon, top left) has four items. Each item shows a fragment.

A. Broadcast Receiver  (BroadcastFragment -> SecondActivity -> ThirdActivity)
   - Spinner with two choices:
       1. Custom broadcast receiver
       2. System battery notification receiver
   - Proceed button opens SecondActivity.
   - Custom: SecondActivity takes a plain text message and passes it to
     ThirdActivity. ThirdActivity registers its own BroadcastReceiver
     (action com.example.cse489a2.CUSTOM_BROADCAST). Pressing
     "Send broadcast" sends the message, and the receiver shows it.
   - Battery: SecondActivity registers a receiver for
     Intent.ACTION_BATTERY_CHANGED and shows the battery percentage.
     ThirdActivity does nothing for this option.

B. Image Scale  (ImageFragment)
   - Downloads an image from the internet on a background thread
     (HttpURLConnection + BitmapFactory).
   - Pinch with two fingers to zoom (ScaleGestureDetector), 0.5x to 5x.

C. Video  (VideoFragment)
   - Plays res/raw/sample_video.mp4 in a VideoView with MediaController.

D. Audio  (AudioFragment)
   - Plays res/raw/sample_audio.mp3 with MediaPlayer. Play / Pause / Stop.

Main files
   app/src/main/java/com/example/cse489a2/   all Java classes
   app/src/main/res/layout/                  screen layouts
   app/src/main/res/menu/drawer_menu.xml     drawer items
   app/src/main/res/raw/                     video and audio files

Build: compileSdk 34, minSdk 24, targetSdk 34, AGP 8.2.1, Gradle 8.2.1, JDK 17.
Permission: INTERNET (for the image).

How to run
   1. Android Studio -> File -> Open -> select this folder (the one that
      contains settings.gradle).
   2. If asked for a Gradle JVM, choose JDK 17. Wait for Gradle sync.
   3. Pick the emulator (Pixel 6a, API 34) at the top, press the green Run button.
