#!/bin/bash

set -e

RELEASE_VERSION=$1

if [ -z "${RELEASE_VERSION}" ]; then
  echo 'Usage: ./release.sh $VERSION'
  exit 1
fi

TAG_NAME="v${RELEASE_VERSION}"

# Check for local modifications
HAS_CHANGES=`git status -s`
if [ "${HAS_CHANGES}" ]; then
  echo "Cannot release with local modfications:"
  echo "${HAS_CHANGES}"
  exit 1
fi

echo "Fetching Code"
git checkout master &> /dev/null
git pull origin master &> /dev/null

echo "Creating Local Release Branch"
git branch -D release &> /dev/null
git checkout -b release &> /dev/null

echo "Setting Version"
echo "${RELEASE_VERSION}" > VERSION
git add VERSION &> /dev/null
git commit -m "${RELEASE_VERSION}" &> /dev/null

echo "Creating tag"
git tag -af "${TAG_NAME}" -m "${TAG_NAME}" origin/master &> /dev/null
git push --force origin "${TAG_NAME}"  &> /dev/null

echo "Building"
./gradlew build upload closeAndPromoteRepository

echo "Cleaning up"
git checkout master &> /dev/null
git branch -d release &> /dev/null

echo "Done"

