# Change Log
German translation of CHANGELOG.md - further information there.

The format was based on [Keep a Changelog](http://keepachangelog.com/) until 0.6.1.
Since 0.6.1, try to write "prose" changelogs that can be copied to Google Play.

This project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
Die App nutzt nun Crashlytics/fabric.io zur besseren Fehlererkennung. Die Infos zum Datenschutz auf 
http://ironjan.de/metal-only-datenschutz wurden entsprechend angepasst.

# [0.6.10]
Absturz beim Öffnen des Wunsch-Screens – und Code im Hintergrund aufgeräumt.

# [0.6.9]
Weitere Abstürze und Performance-Probleme (Application not repsonding) behoben.

# [0.6.8]
Einige Abstürze behoben.

## [0.6.7]
Absturz beim Öffnen des Sendeplans behoben, Plan und Song History werden wieder korrekt angezeigt.
Beides wurde im selben Zuge deutlich flotter gemacht.

## [0.6.6-4]

Überarbeitung der internen App-Struktur und einige Fehlerbehebungen.

## [0.6.6-3]
Das System zum Holen von Infos über die aktuelle Show wurde neu geschrieben.

## [0.6.6-2]
App Code zum Verwalten der Moderatoren-Bilder wurde überarbeitet (alte Bilder sollten nun durch 
neue ersetzt werden) und ein "Leeren"-Button wurde zu Wünschen & Grüßen hinzugefügt. 

Zudem wurde der Quellcode aufgeräumt und das Wünschen von Favoriten wurde repariert.

Hotfix: App an neue API angepasst & Absturz behoben.

## [0.6.6-1]
App Code zum Verwalten der Moderatoren-Bilder wurde überarbeitet (alte Bilder sollten nun durch 
neue ersetzt werden) und ein "Leeren"-Button wurde zu Wünschen & Grüßen hinzugefügt. 

Zudem wurde der Quellcode aufgeräumt und das Wünschen von Favoriten wurde repariert.

Hotfix: adapted App to new Homepage API.

## [0.6.6]
App Code zum Verwalten der Moderatoren-Bilder wurde überarbeitet (alte Bilder sollten nun durch 
neue ersetzt werden) und ein "Leeren"-Button wurde zu Wünschen & Grüßen hinzugefügt. 

Zudem wurde der Quellcode aufgeräumt und das Wünschen von Favoriten wurde repariert.

## [0.6.5]
App Code zum Verwalten der Moderatoren-Bilder wurde überarbeitet (alte Bilder sollten nun durch 
neue ersetzt werden) und ein "Leeren"-Button wurde zu Wünschen & Grüßen hinzugefügt. 

Zudem wurde der Quellcode aufgeräumt und ein Absturz beim Wünschen von Favoriten behoben.

## [0.6.4]
App Code zum Verwalten der Moderatoren-Bilder wurde überarbeitet (alte Bilder sollten nun durch 
neue ersetzt werden) und ein "Leeren"-Button wurde zu Wünschen & Grüßen hinzugefügt. Zudem wurde 
der Quellcode aufgeräumt.

## [0.6.3-1]
Fixed a crash that sometimes occurred after opening the show schedule the mod pictures in song 
history. Also, cleaned up the code base.

Hotfix: adapted App to new Homepage API.

## [0.6.3]
Fixed a crash that sometimes occurred after opening the show schedule the mod pictures in song 
history. Also, cleaned up the code base.

## [0.6.2]
Absturz behoben, der manchmal nach Öffnen des Plans aufgetreten ist. Zudem wurden die Mod-Bilder 
in der Song-History gefixt.

## [0.6.1]
Moderatoren-Bilder in der Song-History wurden gefixt. Diese waren beim Update auf die neue 
Homepage kaputt gegangen.

## [0.6.0]
Kleine Fehlerbehebungen & Anpassungen an neue Homepage. 

Bekanntes Problem: Moderatorenbild in Songliste wird nicht angezeigt

## [0.5.0]
Das System für Wünsche und Grüße wurde überarbeitet und die Feedback-Funktion ist nun leichter 
zu finden. Der Code wurde an vielen Stellen aufgeräumt und auch ein paar Bugs wurden an 
verschiedenen Stellen behoben (Benachrichtigung, Song History, Favoriten).

## [0.4.18-*] (Beta)
- (0.4.18-9)
  - Fixed: [Favorites were crashing/not shown](https://github.com/ironjan/metal-only/issues/43)
- (0.4.18-8)
  - Fixed: [Rewrite Greetings/Wishes with OkHttp](https://github.com/ironjan/metal-only/issues/11)
- (0.4.18-7)
  - Fixed: [Song history doesn't display songs](https://github.com/ironjan/metal-only/issues/29)
  - Fixed: [Notification doesn't open app](https://github.com/ironjan/metal-only/issues/34)
- (0.4.18-6)
  - Greetings/Wishes reverted to less maintainable, but probably working version. 
- (0.4.18-4)
  - Greetings/Wishes system rewritten (experimental)
  - Known problem: Song history not displayed
- (0.4.18-3) Major Code Cleanup
- (0.4.18-2) Removed ActionBarSherlock

## [0.4.18] - 2017-02-03
Fixed:
 * Wünsche und Grüße
 * Moderator-Bilder
 * Sendeplan

## [0.4.17] - 2017-02-03
Fixed:

 * Absturz beim Start der App
