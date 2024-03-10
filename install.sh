#!/bin/bash

set -exu

sbt docker:publishLocal
