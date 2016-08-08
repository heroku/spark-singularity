#!/usr/bin/env bash

# Fit x2 this value into available memory; master + worker
export SPARK_DAEMON_MEMORY="${SPARK_DAEMON_MEMORY-512m}"