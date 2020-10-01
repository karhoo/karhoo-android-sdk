fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew install fastlane`

# Available Actions
## Android
### android unit_tests
```
fastlane android unit_tests
```
Runs all the unit tests
### android linting
```
fastlane android linting
```
Runs lint and detekt
### android beta
```
fastlane android beta
```
New Build
### android slack_result
```
fastlane android slack_result
```
Messaging

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
