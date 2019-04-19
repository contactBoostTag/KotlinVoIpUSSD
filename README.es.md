# Librería MANEJO USSD 

[![Platform](https://img.shields.io/badge/platform-android-brightgreen.svg)](https://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/romellfudi/VoIpUSSDSample/blob/master/LICENSE)
[![Bintray](https://img.shields.io/bintray/v/romllz489/maven/ussd-library.svg)](https://bintray.com/romllz489/maven/ussd-library)
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-Void%20USSD%20Library-green.svg?style=flat )]( https://android-arsenal.com/details/1/7151 )
[![Jitpack](https://jitpack.io/v/romellfudi/VoIpUSSD.svg)](https://jitpack.io/#romellfudi/VoIpUSSD)
[![CircleCi](https://img.shields.io/circleci/project/github/romellfudi/VoIpUSSD.svg)](https://circleci.com/gh/romellfudi/VoIpUSSD/tree/master)
[![](https://img.shields.io/badge/language-EN-blue.svg)](./)

### by Romell Dominguez
[![](https://raw.githubusercontent.com/romellfudi/assets/master/favicon.ico)](https://www.romellfudi.com/)

## Objetivo [High Quality](https://raw.githubusercontent.com/romellfudi/VoIpUSSD/Rev04/snapshot/device_recored.gif):

![](snapshot/device_recored.gif#gif)

Para manejar la comunicación ussd, hay que tener presente que la interfaz depende del SO y del fabricante.

## USSD LIBRARY

`latestVersion` is ![](https://img.shields.io/bintray/v/romllz489/maven/ussd-library.svg)

Agregar en tu archivo `build.gradle` del proyecto Android:

```groovy
repositories {
    jcenter()
}
dependencies {
    implementation 'com.romellfudi.ussdlibrary:kotlin-ussd-library:{latestVersion}'
}
```

* Escribir el archivo xml [acá](https://github.com/romellfudi/VoIpUSSD/blob/master/ussd-library/src/main/res/xml/ussd_service.xml) to res/xml folder (if necessary), this config file allow link between App and SO:

```xml
<?xml version="1.0" encoding="utf-8"?>
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    .../>
```

### Application

Agregar las dependencias: CALL_PHONE, READ_PHONE_STATE and SYSTEM_ALERT_WINDOW:

```xml
    <uses-permission android:name="android.permission.CALL_PHONE" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

Agregar el servicio:

```xml
    <service
        android:name="com.romellfudi.ussdlibrary.USSDService"
        android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
        <intent-filter>
            <action android:name="android.accessibilityservice.AccessibilityService" />
        </intent-filter>
        <meta-data
            android:name="android.accessibilityservice"
            android:resource="@xml/ussd_service" />
    </service>
```

# Uso del API:

Primero necesitamos mapear los mensajes de respuesta USSD, para idetificar los de logeo y de error

| KEY MESSAGE | String Messages |
| ------ | ------ |
| KEY_LOGIN | "espere","waiting","loading","esperando",... |
| KEY_ERROR | "problema","problem","error","null",... |

```kotlin
map = HashMap()
map!!["KEY_LOGIN"] = HashSet(Arrays.asList("espere", "waiting", "loading", "esperando"))
map!!["KEY_ERROR"] = HashSet(Arrays.asList("problema", "problem", "error", "null"))
```

Instancia un objeto ussController con su activity

```kotlin
USSDApi ussdApi = USSDController.getInstance(activity!!)
ussdApi.callUSSDOverlayInvoke(phoneNumber, map!!, object : USSDController.CallbackInvoke {
    override fun responseInvoke(message: String) {
        // message has the response string data
        String dataToSend = "data"// <- send "data" into USSD's input text
        ussdApi!!.send("1", object : USSDController.CallbackMessage {
            override fun responseMessage(message: String) {
                // message has the response string data from USSD
            }
        })
    }

    override fun over(message: String) {
        // message has the response string data from USSD or error
        // response no have input text, NOT SEND ANY DATA
    }
})
```

Si requiere un flujo de trabajo, tienes que usar la siguiente estructura:

```kotlin
ussdApi.callUSSDOverlayInvoke(phoneNumber, map!!, object : USSDController.CallbackInvoke {
    override fun responseInvoke(message: String) {
        // first option list - select option 1
        ussdApi!!.send("1", object : USSDController.CallbackMessage {
            override fun responseMessage(message: String) {
                // second option list - select option 1
                ussdApi.send("1",new USSDController.CallbackMessage(){
                    override fun responseMessage(message: String) {
                        ...
                    }
                })
            }
        })
    }

    override fun over(message: String) {
        // message has the response string data from USSD
        // response no have input text, NOT SEND ANY DATA
    }
    ...
})
```

## Static Methods
En caso de uso android >= M, necesitaras verificar los permisos, igualmente los métodos `callUSSDInvoke` y `callUSSDOverlayInvoke` también verifican que esten habilitados;

```kotlin
 // check if accessibility permissions is enable
    USSDController.verifyAccesibilityAccess(Activity)
 // check if overlay permissions is enable
    USSDController.verifyOverLay(Activity)
```

Soporte multi-sim card:

```kotlin
ussdApi.callUSSDOverlayInvoke(phoneNumber, simSlot, map!!, object : USSDController.CallbackInvoke {
    ...
}
```

## Overlay Services Widget (no indispensable)

Un severo problema al manejar este tipo de widget, este no puede ocultarse, redimencionarse, no puede ser puesto en el fondo con un rogressDialog
Pero recientemente a partir del Android O, Google permite la construcción build a nw kind permission dde widget sobrepuestos, mi solución implementada fue este widget llamdo `OverlayShowingService`:
For use need add permissions at AndroidManifest:

```xml
<uses-permission android:name="android.permission.ACTION_MANAGE_OVERLAY_PERMISSION" />
```

La librería cuenta con dos componentes:

### SplashLoadingService

Agregar Broadcast Service:

```xml
<service android:name="com.romellfudi.ussdlibrary.SplashLoadingService"
         android:exported="false" />
```

Invocar como cualquier servicio:

```kotlin
val svc = Intent(activity, OverlayShowingService::class.java)
// show layout
activity.startService(svc)
ussdApi.callUSSDOverlayInvoke(phoneNumber, map!!, object : USSDController.CallbackInvoke {
        ...
        // dismiss layout
        activity.stopService(svc)
        ...
}
```

![](snapshot/device_splash.gif#gif)

## EXTRA: Uso de la línea voip

En esta sección dejo las líneas requeridas para realizar la conexión VOIP-USSD

```kotlin
ussdPhoneNumber = ussdPhoneNumber.replace("#", uri)
val uriPhone = Uri.parse("tel:$ussdPhoneNumber")
context.startActivity(Intent(Intent.ACTION_CALL, uri))
```

Una vez inicializado la llamada el servidor telcom comenzará a enviar las *famosas pantallas **ussd***

![image](snapshot/telcom.png#center)

### License
```
Copyright 2019 Romell D.Z.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

<style>
img[src*='#center'] { 
    width:390px;
    display: block;
    margin: auto;
}
img[src*='#gif'] { 
    width:200px;
    display: block;
    margin: auto;
}
</style>