# Atlassian - HipChat Message Analyser Exercise - Android

This project is made as a take-home coding exercise for Atlassian Android Mobile Application Developer Position.

## Exercise Description

We'd like you to complete a take-home coding exercise as a first step.  This exercise is not meant to be tricky or complex; however, it does represent a typical problem faced by the HipChat Engineering team.  Here are a few things to keep in mind as you work through it:

- The position is for an Android Developer, so please build an Android app. 
- There's no time limit; take your time and write quality, production-ready code.  Treat this as if you're a member of the HipChat Engineering team and are solving it as part of your responsibilities there.
- Be thorough and take the opportunity to show the HipChat Engineering team that you've got technical chops.
- Using frameworks and libraries is acceptable, just remember that the idea is to show off your coding abilities.
  
### Now, for the coding exercise

Please write a solution that takes a chat message string and returns a JSON string containing information about its contents. Special content to look for includes:

1. @mentions - A way to mention a user. Always starts with an '@' and ends when hitting a non-word character. (http://help.hipchat.com/knowledgebase/articles/64429-how-do-mentions-work-)
2. Emoticons - For this exercise, you only need to consider 'custom' emoticons which are alphanumeric strings, no longer than 15 characters, contained in parenthesis. You can assume that anything matching this format is an emoticon. (https://www.hipchat.com/emoticons)
3. Links - Any URLs contained in the message, along with the page's title.
 
For example, calling your function with the following inputs should result in the corresponding return values.
Input: "`@chris you around?`"
Return (`string`):
```javascript
{
  "mentions": [
    "chris"
  ]
}
```
 
Input: "`Good morning! (megusta) (coffee)`"
Return (`string`):
```javascript
{
  "emoticons": [
    "megusta",
    "coffee"
  ]
}
```
 
Input: "`Olympics are starting soon; http://www.nbcolympics.com`"
Return (`string`):
```javascript
{
  "links": [
    {
      "url": "http://www.nbcolympics.com",
      "title": "NBC Olympics | 2014 NBC Olympics in Sochi Russia"
    }
  ]
}
```
 
Input: "`@bob @john (success) such a cool feature; https://twitter.com/jdorfman/status/430511497475670016`"
Return (`string`):
```javascript
{
  "mentions": [
    "bob",
    "john"
  ],
  "emoticons": [
    "success"
  ],
  "links": [
    {
      "url": "https://twitter.com/jdorfman/status/430511497475670016",
      "title": "Twitter / jdorfman: nice @littlebigdetail from ..."
    }
  ]
}
```

## Building System

The project building system is currently [Gradle Android][1] and can be easily imported to [Android Studio][2] by importing the project as a Gradle project, and below some more details about the project building process.

### Version

- Release: NaN
- Development: 0.0.1_SNAPSHOT (1)

### Dependencies

- Android SDK 4.2+ (API 17+), with Android Build-tools Rev.22+
- Groovy 2.3.6
- Gradle 2.8
- Android Plugin 1.5.0

Or you can use the `./gradlew` to let the Gradle Wrapper download the dependencies and do all the magic for you.

### Supported API Level

- The minimum SDK Version: 17
- The target SDK Version: 23

### Building Instructions

Below the description of some main tasks could be executed via Gradle:

```sh
~$ ./gradlew assemble # Assembles all variants (buildTypes and productFlavors) of the application
~$ ./gradlew assembleDebug # Assembles all Debug builds of all flavors
~$ ./gradlew assembleRelease # Assembles all Release builds of all flavors
```

## License

    Copyright 2016 Mahmoud Abdurrahman

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: http://tools.android.com/tech-docs/new-build-system/user-guide
[2]: http://developer.android.com/sdk/installing/studio.html