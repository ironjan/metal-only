# Metal Only Android App

Diese App bringt den Internet Radio Stream von http://metal-only.de/ auf
Android, wo man 24 Stunden am Tag Rock und Metal hören kann. Außerdem enthält 
sie einige Sender-spezifische Funktionen, wie das Senden von Musik-Wünschen 
oder Grüßen.

Die App kann über Google Play [heruntergeladen][11] werden. Probleme und 
Feature-Wünsche können an [lippertsjan+metal-only@gmail.com](mailto:lippertsjan+metal-only@gmail.com) 
gesendet werden.

Ursprünglich wurde die App von [@rbrtslmn](https://github.com/rbrtslmn) und [@michael-busch](https://github.com/michael-busch) 
entwickelt. 

## Fehler melden

Issue Tracker: https://github.com/ironjan/metal-only/issues

Beantworte die folgenden Fragen:

 * Welche Android-Version und welche Version der App wird verwendet?
 * Welche Schritte führst du durch?
 * Was sollte passieren?
 * Was passiert stattdessen?

## Entwickeln

Die metal-only wird mit [gradle](http://tools.android.com/tech-docs/new-build-system/user-guide) 
gebaut. Zum Entwickeln empfiehlt sich [Android Studio](https://developer.android.com/sdk/index.html).

### Getting started

 1. [Fork](https://help.github.com/articles/fork-a-repo/) this project.
 2. Import the project into android studio (Check out from Version Control, 
    select github or git)
 3. Create a branch to work on. Then fix bugs, implement features, ...
 4. Push on your fork
 5. [Create a Pull request](https://help.github.com/articles/creating-a-pull-request/) 
    with base branch Codingspezis/metal-only develop
 
Siehe auch https://gun.io/blog/how-to-github-fork-branch-and-pull-request/

### Abhängigkeiten updaten

Um zu sehen, ob es Updates gibt, kann ```./gradlew dependencyUpdates``` 
benutzt werden. Aber Achtung: zeigt auch "false positives" an. 

## Lizenz

Lizenziert unter der [Apache-Lizenz, Version 2.0.](https://github.com/Codingspezis/metal-only/blob/master/LICENSE.txt)

Verwendete Software
-------------------

* [ActionBarSherlock][3] ([Apache 2.0][6])
* [LazyList][4] ([MIT][7])
* [androidannotations][5] ([Apache 2.0][6])
* [Spring for Android][9] ([Apache 2.0][6])
* [Jackson JSON Processor][10] ([Apache 2.0][6])
 
[3]: https://github.com/JakeWharton/ActionBarSherlock/                "ActionBarSherlock"
[4]: https://www.github.com/thest1/LazyList/                          "LazyList"
[5]: https://github.com/excilys/androidannotations/                   "androidannotations"
[6]: http://www.apache.org/licenses/LICENSE-2.0.htlm                  "Apache 2.0"
[7]: http://opensource.org/licenses/MIT                               "MIT"
[8]: http://www.gnu.org/licenses/lgpl.html                            "LGPL"
[9]: http://projects.spring.io/spring-android/                        "Spring for Android"
[10]: http://wiki.fasterxml.com/JacksonHome                           "Jackson JSON Processor"
[11]: https://play.google.com/store/apps/details?id=com.codingspezis.android.metalonly.player "Metal Only App"
