#!/usr/bin/env bash
# This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
# If a copy of the MPL was not distributed with this file, You can obtain one at
# https://mozilla.org/MPL/2.0/.
#
#
# Wrapper script to run fakestmp-web.jar that receives the full path to the jar
#

jarFile="$1"

if [ -f "$jarFile" ] ; then
    shift 1

    java -jar "$jarFile" $@

else
  echo "Invalid JAR file $jarFile"
  exit 2
fi
