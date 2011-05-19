This gitrepo contains a small testcase that demonstrates a bug in the Android
dex installer and/or scala 2.9.  I'm happy to update this report with more 
details based on feedback from the community.

For now my workaround is to stay with scala-2.8.1.

# Summary of bug (when generated with scala-2.9.0):

If a generated apk contains the following scala code as a nested class 
(or presumably a top level task) the "adb install 
dexfailure-testcase_2.9.0-0.1.apk" will fail.

<code>
  class ObservableTest extends ArrayBuffer[Int]
</code>

# Expected behavior

Install should not fail.  

# Behavior with scala-2.8.1

It is worth noting that the scala-2.8.1 based APK is substantially smaller than
the scala-2.9.0 version (200k vs 450k)

# Steps to reproduce

* Pull down this repo.  I've included prebuilt working and failing APKs in
the target subdirectory if you don't want to build from source.
* If you do want to build from source: cd into the repo and run 
"sbt reinstall-emulator"
* Note the error message about INSTALL_FAILED_DEXOPT

This failure seems to occur regardless of how much VMmax or RAM is given to the
device.

# Details on failure:

## Host reports:

[error] 1023 KB/s (434216 bytes in 0.414s)
[info] 	pkg: /data/local/tmp/dexfailure-testcase_2.9.0-0.1.apk
[info] Failure [INSTALL_FAILED_DEXOPT]

## logcat on device shows:

<pre>
05-19 08:57:56.920: DEBUG/PackageManager(75): Scanning package com.ridemission.dexfail
05-19 08:57:56.925: INFO/PackageManager(75): Package com.ridemission.dexfail codePath changed from /data/app/com.ridemission.dexfail-1.apk to /data/app/com.ridemission.dexfail-2.apk; Retaining data and using new
05-19 08:57:56.925: INFO/PackageManager(75): Unpacking native libraries for /data/app/com.ridemission.dexfail-2.apk
05-19 08:57:57.025: DEBUG/installd(35): DexInv: --- BEGIN '/data/app/com.ridemission.dexfail-2.apk' ---
05-19 08:58:09.505: ERROR/dalvikvm(473): LinearAlloc exceeded capacity (5242880), last=121180
05-19 08:58:09.505: ERROR/dalvikvm(473): VM aborting
05-19 08:58:09.627: INFO/DEBUG(31): *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***
05-19 08:58:09.627: INFO/DEBUG(31): Build fingerprint: 'generic/sdk/generic:2.3.3/GRI34/101070:eng/test-keys'
05-19 08:58:09.627: INFO/DEBUG(31): pid: 473, tid: 473  >>> /system/bin/dexopt <<<
05-19 08:58:09.627: INFO/DEBUG(31): signal 11 (SIGSEGV), code 1 (SEGV_MAPERR), fault addr deadd00d
05-19 08:58:09.627: INFO/DEBUG(31):  r0 fffffec4  r1 deadd00d  r2 00000026  r3 00000000
05-19 08:58:09.627: INFO/DEBUG(31):  r4 800a45c0  r5 004e4a8c  r6 005023ec  r7 0001d95c
05-19 08:58:09.627: INFO/DEBUG(31):  r8 00000000  r9 00000000  10 00000000  fp 00000000
05-19 08:58:09.627: INFO/DEBUG(31):  ip 800a4720  sp bea259a8  lr afd19375  pc 80045a4a  cpsr 20000030
05-19 08:58:09.705: INFO/DEBUG(31):          #00  pc 00045a4a  /system/lib/libdvm.so
05-19 08:58:09.715: INFO/DEBUG(31):          #01  pc 0004dc16  /system/lib/libdvm.so
05-19 08:58:09.715: INFO/DEBUG(31):          #02  pc 00068c88  /system/lib/libdvm.so
05-19 08:58:09.715: INFO/DEBUG(31):          #03  pc 0006945c  /system/lib/libdvm.so
05-19 08:58:09.715: INFO/DEBUG(31):          #04  pc 0006a9ae  /system/lib/libdvm.so
05-19 08:58:09.715: INFO/DEBUG(31):          #05  pc 0006ab7a  /system/lib/libdvm.so
05-19 08:58:09.715: INFO/DEBUG(31):          #06  pc 0005b478  /system/lib/libdvm.so
05-19 08:58:09.725: INFO/DEBUG(31):          #07  pc 0005b6ba  /system/lib/libdvm.so
05-19 08:58:09.725: INFO/DEBUG(31):          #08  pc 0005b948  /system/lib/libdvm.so
05-19 08:58:09.725: INFO/DEBUG(31):          #09  pc 00008c42  /system/bin/dexopt
05-19 08:58:09.725: INFO/DEBUG(31):          #10  pc 00008d3c  /system/bin/dexopt
05-19 08:58:09.725: INFO/DEBUG(31):          #11  pc 00008de0  /system/bin/dexopt
05-19 08:58:09.725: INFO/DEBUG(31):          #12  pc 000091aa  /system/bin/dexopt
05-19 08:58:09.725: INFO/DEBUG(31):          #13  pc 00014db8  /system/lib/libc.so
05-19 08:58:09.725: INFO/DEBUG(31): code around pc:
05-19 08:58:09.725: INFO/DEBUG(31): 80045a28 447a4479 ed0cf7d1 20004c09 ee34f7d1 
05-19 08:58:09.725: INFO/DEBUG(31): 80045a38 447c4808 6bdb5823 d0002b00 49064798 
05-19 08:58:09.725: INFO/DEBUG(31): 80045a48 700a2226 eea0f7d1 000436b7 00045275 
05-19 08:58:09.725: INFO/DEBUG(31): 80045a58 0005eb82 fffffec4 deadd00d b510b40e 
05-19 08:58:09.735: INFO/DEBUG(31): 80045a68 4c0a4b09 447bb083 aa05591b 6b5bca02 
05-19 08:58:09.735: INFO/DEBUG(31): code around lr:
05-19 08:58:09.735: INFO/DEBUG(31): afd19354 b0834a0d 589c447b 26009001 686768a5 
05-19 08:58:09.735: INFO/DEBUG(31): afd19364 220ce008 2b005eab 1c28d003 47889901 
05-19 08:58:09.735: INFO/DEBUG(31): afd19374 35544306 d5f43f01 2c006824 b003d1ee 
05-19 08:58:09.735: INFO/DEBUG(31): afd19384 bdf01c30 000281a8 ffffff88 1c0fb5f0 
05-19 08:58:09.735: INFO/DEBUG(31): afd19394 43551c3d a904b087 1c16ac01 604d9004 
05-19 08:58:09.735: INFO/DEBUG(31): stack:
05-19 08:58:09.735: INFO/DEBUG(31):     bea25968  800a9d9c  
05-19 08:58:09.735: INFO/DEBUG(31):     bea2596c  4003cc38  /dev/ashmem/dalvik-heap (deleted)
05-19 08:58:09.735: INFO/DEBUG(31):     bea25970  afd4270c  /system/lib/libc.so
05-19 08:58:09.735: INFO/DEBUG(31):     bea25974  afd426b8  /system/lib/libc.so
05-19 08:58:09.735: INFO/DEBUG(31):     bea25978  00000000  
05-19 08:58:09.735: INFO/DEBUG(31):     bea2597c  afd19375  /system/lib/libc.so
05-19 08:58:09.735: INFO/DEBUG(31):     bea25980  00000000  
05-19 08:58:09.735: INFO/DEBUG(31):     bea25984  afd183d9  /system/lib/libc.so
05-19 08:58:09.735: INFO/DEBUG(31):     bea25988  000128ac  [heap]
05-19 08:58:09.735: INFO/DEBUG(31):     bea2598c  0005eb82  [heap]
05-19 08:58:09.735: INFO/DEBUG(31):     bea25990  004e4a8c  
05-19 08:58:09.735: INFO/DEBUG(31):     bea25994  005023ec  
05-19 08:58:09.735: INFO/DEBUG(31):     bea25998  0001d95c  [heap]
05-19 08:58:09.735: INFO/DEBUG(31):     bea2599c  afd18437  /system/lib/libc.so
05-19 08:58:09.735: INFO/DEBUG(31):     bea259a0  df002777  
05-19 08:58:09.735: INFO/DEBUG(31):     bea259a4  e3a070ad  
05-19 08:58:09.735: INFO/DEBUG(31): #00 bea259a8  000128a8  [heap]
05-19 08:58:09.735: INFO/DEBUG(31):     bea259ac  8004dc1b  /system/lib/libdvm.so
05-19 08:58:09.735: INFO/DEBUG(31): #01 bea259b0  0001d95c  [heap]
05-19 08:58:09.735: INFO/DEBUG(31):     bea259b4  00000000  
05-19 08:58:09.735: INFO/DEBUG(31):     bea259b8  00000000  
05-19 08:58:09.735: INFO/DEBUG(31):     bea259bc  00014000  [heap]
05-19 08:58:09.735: INFO/DEBUG(31):     bea259c0  000128ac  [heap]
05-19 08:58:09.735: INFO/DEBUG(31):     bea259c4  00012f44  [heap]
05-19 08:58:09.735: INFO/DEBUG(31):     bea259c8  4158eb48  /dev/ashmem/dalvik-LinearAlloc (deleted)
05-19 08:58:09.735: INFO/DEBUG(31):     bea259cc  4003cce0  /dev/ashmem/dalvik-heap (deleted)
05-19 08:58:09.735: INFO/DEBUG(31):     bea259d0  00000000  
05-19 08:58:09.735: INFO/DEBUG(31):     bea259d4  000025e8  
05-19 08:58:09.735: INFO/DEBUG(31):     bea259d8  00000000  
05-19 08:58:09.735: INFO/DEBUG(31):     bea259dc  80068c8d  /system/lib/libdvm.so
05-19 08:58:09.785: WARN/installd(35): DexInv: --- END '/data/app/com.ridemission.dexfail-2.apk' --- status=0x000b, process failed
05-19 08:58:09.785: ERROR/installd(35): dexopt failed on '/data/dalvik-cache/data@app@com.ridemission.dexfail-2.apk@classes.dex' res = 11
05-19 08:58:09.805: WARN/PackageManager(75): Package couldn't be installed in /data/app/com.ridemission.dexfail-2.apk
05-19 08:58:09.805: DEBUG/PackageParser(75): Scanning package: /data/app/com.ridemission.dexfail-1.apk
05-19 08:58:09.825: DEBUG/PackageManager(75): Scanning package com.ridemission.dexfail
05-19 08:58:09.835: INFO/PackageManager(75): Unpacking native libraries for /data/app/com.ridemission.dexfail-1.apk
05-19 08:58:09.875: INFO/ActivityManager(75): Force stopping package com.ridemission.dexfail uid=10035
05-19 08:58:09.885: DEBUG/PackageManager(75):   Activities: com.ridemission.dexfail.MainActivity
</pre>

