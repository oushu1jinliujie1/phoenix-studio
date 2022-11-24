#! /usr/bin/env bash
while getopts s:t:i: opt;
do
case $opt in
s) schema=$OPTARG
   ;;
t) table=$OPTARG
   ;;
i) index=$OPTARG
   ;;
?) echo "$opt is an invalid option"
   ;;
esac
done
echo "hbase org.apache.phoenix.mapreduce.index.IndexTool --schema \"${schema}\" --data-table \"${table}\" --index-table \"${index}\" --output-path /hbase/data/${schema}/${index} --run-foreground"

hbase org.apache.phoenix.mapreduce.index.IndexTool --schema \"${schema}\" --data-table \"${table}\" --index-table \"${index}\" --output-path /hbase/data/${schema}/${index} --run-foreground