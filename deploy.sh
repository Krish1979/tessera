#!/bin/bash

echo "TRAVIS_BRANCH $TRAVIS_BRANCH"
echo "JAVA_HOME $JAVA_HOME"
echo "TRAVIS_EVENT_TYPE $TRAVIS_EVENT_TYPE"
echo "TRAVIS_JOB_NAME $TRAVIS_JOB_NAME"
echo "TRAVIS_PULL_REQUEST $TRAVIS_PULL_REQUEST"
echo "TRAVIS_BUILD_NUMBER $TRAVIS_BUILD_NUMBER"
echo "TRAVIS_BUILD_STAGE_NAME $TRAVIS_BUILD_STAGE_NAME"
echo "TRAVIS_JDK_VERSION $TRAVIS_JDK_VERSION"
echo "JDK_SWITCHER_DEFAULT $JDK_SWITCHER_DEFAULT"

if [[ -z "${GPG_SECRET_KEYS}" ]]; then
    echo "GPG_SECRET_KEYS environment variable not found"
    exit 1
else 
    echo "GPG_SECRET_KEYS environment variable found"
    echo $GPG_SECRET_KEYS | base64 --decode | gpg --import
   
fi

if [[ -z "${GPG_OWNERTRUST}" ]]; then
    echo "GPG_OWNERTRUST environment variable not found"
    exit 1
else
    echo "GPG_OWNERTRUST environment variable found"
    echo $GPG_OWNERTRUST | base64 --decode | gpg --import-ownertrust
fi

echo "Performing release build"
release_version=`mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec`
branch_name="release-${release_version}"
mvn --settings .maven.xml -B release:branch -DbranchName=${branch_name} -DdryRun 
mvn --settings .maven.xml -B -DpushChanges=true release:prepare release:perform -DdryRun 
echo "TODO: The release branch must be manually merged back to master. (This could be automated in the future.)"

exit 0
