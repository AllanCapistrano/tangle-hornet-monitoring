#!/bin/bash

# Verifica se foram passados argumentos
if [ $# -eq 0 ]; then
    java -jar bin/tangle-hornet-monitoring-1.0.0-jar-with-dependencies.jar -apt $API_PORT -ridx $READ_INDEX -widx $WRITE_INDEX
else
    java -jar bin/tangle-hornet-monitoring-1.0.0-jar-with-dependencies.jar -apt $API_PORT -ridx $READ_INDEX -widx $WRITE_INDEX $1
fi