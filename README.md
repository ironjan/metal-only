# Metal Only Android App

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e64554aa306945dbbe50e64ad605c37e)](https://www.codacy.com/app/lippertsjan/metal-only?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ironjan/metal-only&amp;utm_campaign=Badge_Grade)

 * LICENSE: [Apache-Lizenz, Version 2.0.](https://github.com/ironjan/metal-only/blob/master/LICENSE.txt)
 * AUTHORS: https://github.com/ironjan/metal-only/graphs/contributors

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
  * [Used Libraries and Software](#used-libraries-and-software)
  * [Project Organization](#project-organization)

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

Currently, the app is mainly written in Java but a migration to kotlin has been 
started in May 2017. New code should be written in kotlin if possible.

### Getting started

 1. [Fork](https://help.github.com/articles/fork-a-repo/) this project.
 2. Import the project into android studio (Check out from Version Control, 
    select github or git)
 3. Create a branch to work on. Then fix bugs, implement features, ...
 4. Push on your fork
 5. [Create a Pull request](https://help.github.com/articles/creating-a-pull-request/) 
    with base branch Codingspezis/metal-only develop
 
See also https://gun.io/blog/how-to-github-fork-branch-and-pull-request/

#### Useful Knowledge

It may be useful to check for dependency updates once in a while. We're using a 
gradle plugin for that: Execute ```./gradlew dependencyUpdates``` to list 
updates. Note: for some reason, the report may include false positives, i.e. 
non-existing updates. 

### Used Libraries and Software

 * [Jackson Databind](http://wiki.fasterxml.com/JacksonHome) (Apache 2.0)
 * [Spring for Android](http://projects.spring.io/spring-android/) (Apache 2.0)
 * [okhttp](https://github.com/square/okhttp) (Apache 2.0)
 * [slf4j-android](https://github.com/twwwt/slf4j) (MIT LICENSE)
 * [Androidannotations (Core & REST Spring)](http://androidannotations.org/) (Apache 2.0)
 * [LazyList](https://www.github.com/thest1/LazyList/) (MIT LICENSE)
 
Build Time dependencise (i.e. not-packaged):

 * [ktlint](https://github.com/shyiko/ktlint) (MIT LICENSE)
 
## Project Organization

The project has been split into multiple modules to enforce encapsulation. See 
[Understanding Onion Architecture](http://blog.thedigitalgroup.com/chetanv/2015/07/06/understanding-onion-architecture/)
for more information. In addition to the onion architecture, we will also 
split out some functionality for easier re-use later, e.g. the json API 
implementation. The current goal is to split the project into the following 
sub-modules:

 * `core` will contain only data models and code related to these models. 
   There should be no dependency on Android. It corresponds to the *domain 
   layer*.
 * `json_lib` will contain the json model and the API implementation. It may 
   enable the re-use of the API implementation in other projects. The service 
   implementatios rely on this library.
 * `services` will hold implementations of our business logic. This layer 
   responds to requests from the UI or system handles. It's a intermediate 
   between the app and the infrastructure layer.
 * `infrastructure` provides specific implementations for the service layer 
   to handle logging and data storage.
 * `app` will contain Android-specific implementations like `Activities` and 
   `Services` that calls on the service layer
