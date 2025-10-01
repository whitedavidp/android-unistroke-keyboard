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
* Long press enters cursor mode.

Changes to the original:

1. Made input area taller for use with higher resolution screens by increasing button height dimension.

2. Removed "cursor mode" entirely as it seemed to get triggered accidentally and caused much more trouble than it was worth.

3. Added double-tap of Key button to show pop-up of the cheat sheet.

4. Some additions to the alphabet gestures - especially "y" and "g" to make them more compatible with their Graffiti analogs.

5. Removed various items related to notifications and other stuff that made it difficult to build inside of my old, Eclipse environment.

6. Included the Gesture Builder apk since that is used to modify the raw resources in this app.

7. Added Graffiti Return/Enter stroke.

<img alt="gesture" src="./docs/images/gesture.png" width="400px">
