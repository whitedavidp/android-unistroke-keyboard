# android-unistroke-keyboard

![Android CI](https://github.com/tmatz/android-unistroke-keyboard/workflows/Android%20CI/badge.svg)
[![codebeat badge](https://codebeat.co/badges/0ef814df-ee55-41f1-9af7-03c17807479d)](https://codebeat.co/projects/github-com-tmatz-android-unistroke-keyboard-master)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/180a8cae4ef744ee83d9b5103a5c2fd6)](https://app.codacy.com/gh/tmatz/android-unistroke-keyboard/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

Unistroke Handwriting Input Method for Programming

<img alt="screenshot" src="./docs/images/screenshot.png" width="400px">

* Left pane for alphabet.
* Right pane for number.
* Tap enters special key mode.
* KEY button shows some useful key buttons.
* Long press enters cursor mode. REMOVED

Changes to the original:

1. Made input area taller for use with higher resolution screens by increasing button height dimension.

2. Removed "cursor mode" entirely as it seemed to get triggered accidentally and caused much more trouble than it was worth.

3. Added double-tap of Key button to show pop-up of the cheat sheet.

4. Some additions to the alphabet gestures - especially "y" and "g" to make them more compatible with their Graffiti analogs.

5. Removed various items related to notifications and other stuff that made it difficult to build inside of my old, Eclipse environment.

6. Included the Gesture Builder apk since that is used to modify the raw resources in this app. (Note: this build of Gesture Builder is my own and differs from the one included in the SDK. It has a test activity and it is using a 3rd party recognizer in
the test activity called Point Cloud - see http://depts.washington.edu/acelab/proj/dollar/pdollar.html for details - so the quality of reported recognition may not match that of the keyboard code.)

7. Added Graffiti Return/Enter stroke.

8. Changed package id to avoid collisions as suggested here: https://github.com/tmatz/android-unistroke-keyboard/issues/28#issuecomment-3368561049

9. Optionally allow the app to read gestures from files of the appropriate names stored in the root of internal storage. This makes experimenting with gestures easier as no recompiling it required. However, it does require lowering the target SDK. So Android may
"warn" you. Results of this are shown in the app's logcat (if optionally enabled).

10.Added settings panel as launcher icon.

<img alt="gesture" src="./docs/images/gesture.png" width="400px">
