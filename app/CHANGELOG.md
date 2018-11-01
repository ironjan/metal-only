# Change Log
All notable changes to this project will be documented in this file.

The format was based on [Keep a Changelog](http://keepachangelog.com/) until 0.6.1.
Since 0.6.1, try to write "prose" changelogs that can be copied to Google Play.

This project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

## [0.6.19-17]
Complete rewrite with new API and merged forgotten changes.

## [0.6.19-16]
Complete rewrite with new API.

## [0.6.19-15]
Rewrite with new API.

## [0.6.19-14]
PlayServices, more tests (incl. no CipherSuites).

## [0.6.19-13]
Added PlayServices and more tests... and minor changes.

## [0.6.19-12]
Added PlayServices and more tests...

## [0.6.19-11]
Debug build with OkHttp, Gson, Reactive and an additional TLS protocol.

## [0.6.19-10]
Debug build with OkHttp, Gson, Reactive.

## [0.6.19-1]
Added Hyperlog (Debug build).

## [0.6.19]
Fixed multiple crashes.

# [0.6.18]
Fixed plan (was broken in beta). Fixed a crash when opening Wish/Greet.

# [0.6.17]
Fixed plan (was broken in beta).

# [0.6.16]
Internal improvements.

# [0.6.15] - Unreleased
Internal improvements.

# [0.6.14]
Fixed multiple crash sources.

# [0.6.13]
Experimental change to fix "application not responding"-errors.

# [0.6.12]
Fixed wishing.

# [0.6.11]
Added Crashlytics/fabric.io to catch more errors. The privacy information on 
http://ironjan.de/metal-only-datenschutz was adapted accordingly. Fixed wishing of favorites. 
Added some tests to prevent reintroduction of bugs.

# [0.6.10]
Fixed crash when opening wishes and cleaned up the code behind it.

# [0.6.9]
Fixed some more crashes and "Application not responding"-errors.

# [0.6.8]
Fixed different crashes.

# [0.6.7]
Fixed crash when opening the time table, time table and song history are displayed correct again.
Improved the performance of the latter greatly.

## [0.6.6-4]

Restructured app-internals and fixed some defects.

## [0.6.6-3]
Rewrote the part of the app responsible for showing current song information.

## [0.6.6-2]

Rewrote the handling of moderator pictures (old pictures should be replaced by new ones now) and 
added a "Clear" button to Wish/Greet. Also, cleaned up the code a lot and fixed wishing a favorite 
song.

Hotfix: adapted App to new Homepage API & fixed a crash.

## [0.6.6-1]
Rewrote the handling of moderator pictures (old pictures should be replaced by new ones now) and 
added a "Clear" button to Wish/Greet. Also, cleaned up the code a lot and fixed wishing a favorite 
song.

Hotfix: adapted App to new Homepage API.

## [0.6.6]
Rewrote the handling of moderator pictures (old pictures should be replaced by new ones now) and 
added a "Clear" button to Wish/Greet. Also, cleaned up the code a lot and fixed wishing a favorite 
song.

## [0.6.5]
Rewrote the handling of moderator pictures (old pictures should be replaced by new ones now) and 
added a "Clear" button to Wish/Greet. Also, cleaned up the code a lot and fixed a crash when 
wishing a favorited song.

## [0.6.4]
Rewrote the handling of moderator pictures (old pictures should be replaced by new ones now) and 
added a "Clear" button to Wish/Greet. Also, cleaned up the code a lot.

## [0.6.3-1]
Fixed a crash that sometimes occurred after opening the show schedule the mod pictures in song 
history. Also, cleaned up the code base. Hotfix: adapt App to new Homepage API

## [0.6.3]
Fixed a crash that sometimes occurred after opening the show schedule the mod pictures in song 
history. Also, cleaned up the code base.

## [0.6.2]
Fixed crash that sometimes occured after opening show schedule. 
Also fixed the mod pictures in song history.

## [0.6.1]
- Mod pictures in Song history fixed

## [0.6.0]
- Minor fixes
- Adapting to new homepage

## [0.5.0]
- Better system for Greetings and Wishes
- Major Code Cleanup
- Made Feedback more accessable
- Fixed some bugs (notification, song history, favorites)

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
- Fixed: Wishes and Greetings work again
- Fixed: Moderator images loaded again
- Fixed: plan works again

## [0.4.17] - 2017-02-03
- Fixed: Crash when starting the app
- Added: CHANGELOG.md in git repository
