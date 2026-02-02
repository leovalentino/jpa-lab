#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Maven Start Up Batch script
#
# Required ENV vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars
# -----------------
#   M2_HOME - location of maven2's installed home dir
#   MAVEN_OPTS - parameters passed to the Java VM when running Maven
#     e.g. to debug Maven itself, use
#       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
#   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ] ; then

  if [ -f /etc/mavenrc ] ; then
    . /etc/mavenrc
  fi

  if [ -f "$HOME/.mavenrc" ] ; then
    . "$HOME/.mavenrc"
  fi

fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
mingw=false
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  MINGW*) mingw=true;;
  Darwin*) darwin=true
    # Use /usr/libexec/java_home if available, otherwise fall back to /Library/Java/Home
    # See https://developer.apple.com/library/mac/qa/qa1170/_index.html
    if [ -z "$JAVA_HOME" ]; then
      if [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME="`/usr/libexec/java_home`"
      else
        export JAVA_HOME="/Library/Java/Home"
      fi
    fi
    ;;
esac

if [ -z "$JAVA_HOME" ] ; then
  if [ -r /etc/gentoo-release ] ; then
    JAVA_HOME=`java-config --jre-home`
  fi
fi

if [ -z "$JAVA_HOME" ] ; then
  echo "The JAVA_HOME environment variable is not defined correctly" >&2
  echo "This environment variable is needed to run this program" >&2
  echo ""
  exit 1
fi

if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
  # IBM's JDK on AIX uses strange locations for the executables
  JAVACMD="$JAVA_HOME/jre/sh/java"
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is defined to an invalid directory: $JAVA_HOME" >&2
  echo "" >&2
  echo "Please set the JAVA_HOME variable in your environment to match the" >&2
  echo "location of your Java installation" >&2
  exit 1
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$M2_HOME" ] && M2_HOME=`cygpath --unix "$M2_HOME"`
fi

# Traverse up the directory tree to find mvnw
# This is needed because the script can be called from any subdirectory
MVNW_DIR="$0"
while [ -h "$MVNW_DIR" ] ; do
  LS=`ls -ld "$MVNW_DIR"`
  LINK=`expr "$LS" : '.*-> \(.*\)$'`
  if expr "$LINK" : '/.*' > /dev/null; then
    MVNW_DIR="$LINK"
  else
    MVNW_DIR="`dirname "$MVNW_DIR"`/$LINK"
  fi
done
MVNW_DIR="`dirname "$MVNW_DIR"`"
MVNW_POM="$MVNW_DIR/.mvn/wrapper/maven-wrapper.properties"
MVNW_REPOURL=`grep 'wrapperUrl' "$MVNW_POM" 2>/dev/null | cut -d'=' -f2`
MVNW_ARTIFACT=`grep 'wrapperArtifact' "$MVNW_POM" 2>/dev/null | cut -d'=' -f2 | cut -d'-' -f1`
MVNW_VERSION=`grep 'wrapperVersion' "$MVNW_POM" 2>/dev/null | cut -d'=' -f2`
if [ -z "$MVNW_REPOURL" ] || [ -z "$MVNW_ARTIFACT" ] || [ -z "$MVNW_VERSION" ]; then
  echo "Unable to locate the Maven Wrapper properties file: $MVNW_POM" >&2
  exit 1
fi

# Build the download URL
MVNW_JAR="$MVNW_DIR/.mvn/wrapper/maven-wrapper.jar"
MVNW_DOWNLOAD_URL="$MVNW_REPOURL/org/apache/maven/wrapper/maven-wrapper/$MVNW_VERSION/$MVNW_ARTIFACT-$MVNW_VERSION.jar"

# Download the wrapper jar if it doesn't exist
if [ ! -e "$MVNW_JAR" ]; then
  # Download if we have curl
  if command -v curl > /dev/null; then
    curl -s -o "$MVNW_JAR" "$MVNW_DOWNLOAD_URL" 2>/dev/null
    if [ $? -ne 0 ]; then
      echo "Could not download Maven Wrapper JAR from $MVNW_DOWNLOAD_URL" >&2
      exit 1
    fi
  # Otherwise try wget
  elif command -v wget > /dev/null; then
    wget -q -O "$MVNW_JAR" "$MVNW_DOWNLOAD_URL" 2>/dev/null
    if [ $? -ne 0 ]; then
      echo "Could not download Maven Wrapper JAR from $MVNW_DOWNLOAD_URL" >&2
      exit 1
    fi
  else
    echo "Could not find curl or wget; please install one of them to download the Maven Wrapper JAR" >&2
    exit 1
  fi
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
  [ -n "$M2_HOME" ] && M2_HOME=`cygpath --windows "$M2_HOME"`
fi

# Provide a "standardized" way to retrieve the CLI args that will
# work with both Windows and non-Windows executions.
MAVEN_CMD_LINE_ARGS="$MAVEN_CONFIG $@"

WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

exec "$JAVACMD" \
  $MAVEN_OPTS \
  "-Dmaven.multiModuleProjectDirectory=$MVNW_DIR" \
  -classpath "$MVNW_JAR" \
  "$WRAPPER_LAUNCHER" \
  $MAVEN_CMD_LINE_ARGS
