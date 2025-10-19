# android-unistroke-keyboard

Unistroke Gestural Input Method for General Purpose and Command Line Use.

This provides an alternative to the Graffiti Pro app - which appears to no longer be maintained - and provides extensions useful for command line activities.

<img alt="screenshot" src="./docs/images/screenshot.png" width="400px">
<img alt="screenshot" src="./docs/images/screenshot2.png" width="400px">

* Left pane for alphabet.
* Right pane for number.
* Tap enters special key mode.
* KEY button shows some useful key buttons.
* Long press enters cursor mode. REMOVED

# Please note that for reasons unclear, Microsoft Defender is marking the apk as malware. It is NOT and this is a false-positive. Scanning using multiple products at www.virustotal.com demonstrates that only Defender thinks this is the case. There is a good write-up on this problem here: https://medium.com/@smith_brendan/trojan-script-wacatac-b-ml-when-microsoft-defender-cries-wolf-usually-6fb25816eee6. I apologize for this and have tried, without success, to report the problem to Microsoft. There is nothing more I can do at this time.

Please note that the lion's share of the work here comes from the repo/author from which this has been forked. It is NOT intended to be a source of pull requests for the original.

Also note: Due to my lack of a modern build environment and the use of an older target SDK to avoid file access issues, there is no expectation this will build properly in a "modern" build environment without at least some modifications to those configs.

Changes from the original:

1. Made input area taller for use with higher resolution screens (greater than 1920px in height) by increasing button height dimension from 36 to 60.

2. Removed "cursor mode" entirely as it seemed to get triggered accidentally and caused much more trouble than it was worth.

3. Added double-tap of Key button to show pop-up of the "cheat sheet".

4. Some changes to the alphabet gestures - added to "y", "g", and "5" to make them more compatible with their Graffiti analogs. Changed them for "b" and "e" entirely due to frustrating, consistent mis-recognition.

5. Removed various items related to notifications and other stuff that made it difficult to build inside of my old, Eclipse environment.

6. Included the Gesture Builder apk since that is used to modify the raw resources in this app. (Note: this build of Gesture Builder is my own and differs slightly from the one included in the SDK. It has a test activity and the match prediction it displays
should match that of the keyboard code).

7. Added Graffiti Return/Enter stroke to the common gesture list.

8. Changed package id to avoid collisions as suggested here: https://github.com/tmatz/android-unistroke-keyboard/issues/28#issuecomment-3368561049

9. Added settings panel as launcher icon. Options include:

 * Log some information to the system logcat. You will need to use ADB or a logcat app to view this information.

 * Allow the app to read gestures from files of the appropriate names stored in the root of internal storage. This makes experimenting with gestures easier as no recompiling it required. However, it does require lowering the target SDK and addition of
the READ_EXTERNAL_STORAGE permission. So Android may "warn" you. Results of this are shown in the app's logcat (if optionally enabled).

 * Show gesture recognition results/scores at the bottom of the input area.

 * Show "cheat sheet" excerpts as gesture input backgrounds.

 * Set minimum gesture recognition score. Recognition below this value is considered an error. Usually, a value of 1.5 is considered a good value and is the default. The accepted range is .5 to 3.0. Use after viewing prediction results, if needed. I
personally prefer and error to a mis-recognition so I am currently setting this in the 1.8-2.0 range.

 * Perform extended vibrate on unrecognized gesture in order to make you more aware. If disabled, only a short vibration is issued. This also helps distinguish from the optional short vibration used when entering "special" mode.

10. Slight enlargement of the size of the alphabet gesture area at the expense of the number gesture area.

11. Added icon to replace the default one. Hopefully better but I am no artist :-(

12. Changed position of "special" indicators from top/left to bottom/right as they seem more visible and do not obscure the "cheat sheet" backgrounds (when enabled). Optionally vibrate when entering "special" mode.

13. Added support for the Graffiti Menu gesture to the common gesture list.

14. Added pseudo-support for the Graffiti Shortcut gesture to the special gesture list. As this is a unistroke recognizer, I cannot implement the full Graffiti behavior as these require multi-strokes. As near as I could come was to optionally launch a
settings-specified app upon recognition of the shortcut gesture. This could be a dictionary app, for example. I personally use clipboard text expansion app which is fairly close to the original Graffiti shortcut function.

<img alt="gesture" src="./docs/images/gesture.png" width="400px">
