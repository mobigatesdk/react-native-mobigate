![](https://wiki.spicymobile.pl/wiki/mobigatesdk/download/Main/WebHome/MobienceSDK_Mobigate.png?width=300&height=300)
# React Native Mobigate SDK plugin for Android
MobigateSDK is a tool for gathering users phone data and events tracking. 

## Overview

[![Library version](https://img.shields.io/badge/npm%20package-1.0.4-brightgreen)](https://www.npmjs.com/package/react-native-mobigate) [![Platforms](https://img.shields.io/badge/platforms-android-lightgrey)](https://developer.android.com/)

  - [Plugin installation](#plugin-installation)
  - [SDK Initialization](#sdk-initialization)
  - [Guides](#guides)

## Plugin installation
```
yarn add react-native-mobigate
```
or
```
npm install react-native-mobigate --save
```
### React Native >= 0.60
Starting from React Native 0.60, [autolinking](https://github.com/react-native-community/cli/blob/master/docs/autolinking.md) makes the installation process simpler

### React Native <= 0.59
**Mostly automatic installation:**
```
react-native link react-native-mobigate
```
**Manual installation:**
<details>
<summary>Manually link the library on Android</summary>

1. Add to: `android/settings.gradle`
```groovy
include ':react-native-mobigate'
project(':react-native-mobigate').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-mobigate/android')
```
2. Add to: `android/app/build.gradle`
```groovy
dependencies {
implementation project(':react-native-mobigate')
}
```
3. Add the MobigatePackage class to your list of exported packages into: `android/app/src/main/.../MainApplication.java`
```java
...
import pl.spicymobile.reactmobigate.MobigatePackage;
   
   
@Override
protected List<ReactPackage> getPackages() {
    return Arrays.<ReactPackage>asList(
        new MainReactPackage(), 
        new MobigatePackage()
    );
}
```

</details>

## SDK Initialization
### Initialize SDK first
```javascript
if (Platform.OS === 'android') {
	Mobigate.init("V0K6jhiIfem6CRWHYZ59Nmj3oFBBKbJsnSsWfR2JNq7ktblOUXwbJoBQTpWnw2uSwW76gpiu2kun50jweTY69B",
        {}, 
	(initSuccess) => {
          console.log(initSuccess)
        }, 
	(initError) => {
          console.log(initError)
        })
    }
```
### Start gather data
```javascript
Mobigate.startSDK((result) => {
            console.log(result)
          }, (errorResult) => {
            console.log(errorResult)
          })
```
## Guides
[Full installation and setup guides can be viewed here.](https://wiki.spicymobile.pl/wiki/mobigatesdk/view/Main/sdkintegration/reactnativeintegration/)
