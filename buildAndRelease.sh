#!/usr/bin/env bash

mvn -U clean compile test release:prepare release:perform
