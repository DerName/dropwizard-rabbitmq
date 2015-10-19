#!/bin/bash

set -e

mvn -B clean test jacoco:report # coveralls:report
