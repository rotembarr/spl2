#!/bin/bash
for i in {1..100}
do
    mvn -Dtest=MainTest test
    cp output.json results/output_$i.json
done