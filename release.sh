#!/bin/bash

set -e

#do release
mvn clean release:clean release:prepare -B
mvn release:perform

