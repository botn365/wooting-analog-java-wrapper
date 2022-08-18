# wooting analog java wrapper
## about
A java wrapper for the wooting analog wrapper.
https://github.com/WootingKb/wooting-analog-sdk

## build
Requires a c++ compiler, cmake version >= 3.20.

gradlew build

Run generateJniHeaders to regen JNI headers if needed.
### cross compile
On linux you can crosscompile for windows and apple.

Windows Requires mingw compiler and add -Pwindows.

Apple requires osxcross and add -Papple.

gradlew build -Papple -Pwindows