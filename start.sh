#! /usr/bin/env bash
set -e

basepath=$(cd `dirname $0`; pwd)

noup java -cp ../bin/phoenix-studio-backend-*.jar:../bin/phoenix-client-hbase-*.jar com.oushu.MainApplication