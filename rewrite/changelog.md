# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

The subsections are formatted as they need when publishing a release to the Play Store.

## [Unreleased]

<de-DE>
</de-DE>

<en-GB>
</en-GB>

<en-US>
</en-US>

## Unreleased

 * Fixed: play button was wrong right after start
 * Added: basic plan, no offline handling

## [0.9.33] - 2019-09-01

 * Added: smaller screen support
 * Internal: Improved navigation
 * Added: basic wish functionality

## [0.9.32] - 2019-09-01

 * Added: support for changing network
 * UI styling
 * Internal: Switched to navigation component

## [0.9.31] - 2019-08-30

 * Fixed: Binding to service after resuming activity

## [0.9.30] - 2019-08-28

 * Fixed: IllegalStateException when stopping
 * Added: Info about loading show info

## [0.9.29] - ??

(Unknown)

## [0.9.28] - 2019-08-27

 * Fixed: logs were to big for intents
 * Fixed: Non-ending service
 * Fixed: service binding
 * Improved: audio focus handling
 * Added: more debug statements to investigate doze
 * Added: More debug statements to prepare "stream is buffering" UI
 * Cleanup in Streaming service

## [0.9.27] - 2019-08-19 23:38
 * Improved logging
 * Cleanup in Streaming service, fixed non-ending service
 * Fixed service binding
 * More debug statements to investigate doze
 * Improved audio focus handling
 * More debug statements to prepare "stream is buffering" UI

## [0.9.26] - 2019-08-19

Alpha Release 1.

## [0.9.25] - 2019-08-17 18:21

 * Using HyperLog

## [0.9.24] - 2019-08-17 18:11
 * Fixed log locking...

## [0.9.23] - 2019-08-17 18:05

 * Improved logging

## [0.9.22] - 2019-08-17 13:41

 * Increased log file max size to 64k
 * Added MoStreamingService.IsAwakeLogThread

## [0.9.21] - 2019-08-17 12:25

 * Removed app whitelisting again. No difference.
 * Reduced <main></main>x log file size to 8k
 * Now using stopforeground in service
 * Added stopwithtask to service

## [0.9.20] - 2019-08-17 01:48

 * Another attempt on whitelisting request

## [0.9.19] - 2019-08-17 01:29

 * Added: request for battery whitelisting

## [0.9.18] - 2019-08-17 00:37

 * Fixed. Used wrong cast for AIDL binding

## [0.9.17] - 2019-08-17 00:21

 * Moved service to own process; communication via AIDL

## [0.9.16] - 2019-08-16 14:59

 * Added missing permission

## [0.9.15] - 2019-08-16 14:36

 * Acquiring multicastLock too

## [0.9.14] - 2019-08-16 14:15

 * Reduced log file max size to 256K
 * Explicit wakelock

## [0.9.13] - 2019-08-17 13:

 * Using WIFI_MODE_FULL_HIGH_PERF for wifi lock

## [0.9.12] - 2019-08-17 13:42

 * Added more log statements

## [0.9.11] - 2019-08-16 13:21

 * Added log statements and changed notification id

## [0.9.10] - 2019-08-16 13:06

 * Actually starting service as foreground with immediate promotion

## [0.9.9] - 2019-08-16 12:32

 * Starting service as foreground with immediate promotion

## [0.9.8] - 2019-08-16

 * Removed notification sound, fixed priority
 * Added version to log

## [0.9.7] - 2019-08-15

 * Added show info updates
 * Solved todos

## [0.9.6] - 2019-08-15

 * Resolved warnings, fixed play state?

## [0.9.5] - 2019-08-15

 * Added audio focus callbacks
 * Added noisy stream bc receiver

## [0.9.4] - 2019-08-15

 * Added wakelock and wifilock
 * Added error display in ui

## [0.9.3] - 2019-08-12

<de-DE>
Internal Testing Release. Added notification.
</de-DE>

<en-GB>
Internal Testing Release. Added notification.
</en-GB>

<en-US>
Internal Testing Release. Added notification.
</en-US>
