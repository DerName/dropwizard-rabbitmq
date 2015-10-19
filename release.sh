#!/bin/bash

set -e

# Ensure on master branch


# Check for local modifications
HAS_CHANGES=`git status -s`
if [ "$HAS_CHANGES" ]; then
  echo "Cannot release with local modfications:"
  echo "$HAS_CHANGES"
  exit 1
fi

## make a release branch to do work in
git checkout master ; git pull ; git branch -D release 
#./gradlew build upload
#./gradlew closeAndPromoteRepository
