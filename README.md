# Metal Only Android App

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e64554aa306945dbbe50e64ad605c37e)](https://www.codacy.com/app/lippertsjan/metal-only?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ironjan/metal-only&amp;utm_campaign=Badge_Grade)

 * LICENSE: [Apache-Lizenz, Version 2.0.](https://github.com/ironjan/metal-only/blob/master/LICENSE.txt)
 * AUTHORS: https://github.com/ironjan/metal-only/graphs/contributors
 * IMAGE RIGHTS: Sll images rights for images under images_sources belong to Metal Only e.V. and are used with permission.
                 See https://www.metal-only.de/impressum.html for more information. 

Diese App bringt den Internet Radio Stream von http://metal-only.de/ auf
Android, wo man 24 Stunden am Tag Rock und Metal hören kann. Außerdem enthält 
sie einige Sender-spezifische Funktionen, wie das Senden von Musik-Wünschen 
oder Grüßen.

Die App kann über Google Play [heruntergeladen](https://play.google.com/store/apps/details?id=com.codingspezis.android.metalonly.player) werden. Probleme und 
Feature-Wünsche können an [lippertsjan+metal-only@gmail.com](mailto:lippertsjan+metal-only@gmail.com) 
gesendet werden.

Ursprünglich wurde die App von [@rbrtslmn](https://github.com/rbrtslmn) und 
[@michael-busch](https://github.com/michael-busch) entwickelt. News gibt es 
primär unter [http://ironjan.de/metal-only](http://ironjan.de/metal-only).


## Table of Contents

  * [Helping](#helping)
    * [Send Feedback and Report Errors](#send-feedback-and-report-errors)
    * [Help with App Development](#help-with-app-development)
      * [Getting Started](#getting-started)
      * [Useful Knowledge](#useful-knowledge)
  * [Used Libraries, Software, Fonts, ...](#used-libraries-and-co)

## Helping

### Send Feedback and Report Errors

The easiest option is to use the feedback functionality in the app. It 
automatically includes the most important information in the email template. 
If it's not possible to use the feedback function, try to answer the following 
questions:

 * Which device do you have and which Android version is running on it?
 * What did you do?
 * What happened and what would you exptected to happen instead?

You can add the issues directly to the issue tracker: 
[https://github.com/ironjan/metal-only/issues](https://github.com/ironjan/metal-only/issues)

### Help with App Development

We're using the [gradle](http://tools.android.com/tech-docs/new-build-system/user-guide)
build system. I strongly recommend [Android Studio](https://developer.android.com/sdk/index.html)
to develop. 

### Getting started

 1. [Fork](https://help.github.com/articles/fork-a-repo/) this project.
 2. Import the project into android studio (Check out from Version Control, 
    select github or git)
 3. Create a branch to work on. Then fix bugs, implement features, ...
 4. Push on your fork
 5. [Create a Pull request](https://help.github.com/articles/creating-a-pull-request/) 
    with base branch Codingspezis/metal-only develop
 
See also https://gun.io/blog/how-to-github-fork-branch-and-pull-request/

#### Additional gradle files

You can create a `gradle.properties`-file in the project root with the following content:

```
metalonly.signing=/some/path/to/a-file-collection
metalonly.variants=/some/path/to/another-file-collection
metalonly.fabric.io=/some/path/to/a-third-file-collection
```

`file-collection` refers to similarly named files residing in the same folder, e.g. `metalonly.signing.gradle`
and `metalonly.signing.keystore`. The property `metalonly.signing` can be re-used to point to the 
key-store etc. Please refer to the build.gradle files which additionals exist. Here are some 
templates for the ones currently used:
 
```
// metalonly.fabric.io
android {
  defaultConfig {
    manifestPlaceholders = [fabric_io_id: "replace-me"]
  }
}
```

```
// metalonly.signing
android {
  signingConfigs {
    release {
      storeFile file(project.property("metalonly.signing")+".keystore")
      storePassword "replace-me"
      keyAlias "replace-me"
      keyPassword "replace-me"
    }
  }

  buildTypes {
    release {
      signingConfig signingConfigs.release
    }
  }
}
```

#  Used Libraries, Software, Fonts, ...

Currently in progress. 

Fonts: [Metal Mania](https://fonts.google.com/specimen/Metal+Mania) 