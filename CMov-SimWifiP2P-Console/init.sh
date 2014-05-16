#!/bin/bash

# Forward device ports
ADB="/Applications/Android Studio.app/sdk/platform-tools/adb"

# foreach device
$"$ADB" forward --remove-all
$"$ADB" -s 192.168.56.101:5555 forward tcp:10011 tcp:10001
$"$ADB" -s 192.168.56.101:5555 forward tcp:9011 tcp:9001

$"$ADB" -s 192.168.56.102:5555 forward tcp:10021 tcp:10001
$"$ADB" -s 192.168.56.102:5555 forward tcp:9021 tcp:9001

$"$ADB" -s 192.168.56.103:5555 forward tcp:10031 tcp:10001
$"$ADB" -s 192.168.56.103:5555 forward tcp:9031 tcp:9001

$"$ADB" -s 192.168.56.104:5555 forward tcp:10041 tcp:10001
$"$ADB" -s 192.168.56.104:5555 forward tcp:9041 tcp:9001

$"$ADB" forward --list

# Start SimWifiP2p Console
java pt.utl.ist.cmov.wifidirect.console.Main;
