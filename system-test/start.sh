#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $SCRIPT_DIR

LOG=$(mktemp)
printf "GALOP log file: $LOG\n"

printf "Starting GALOP ...\n"
nohup java -jar ../target/galop-0.6.1.jar ./galop.properties 2>> $LOG >> $LOG &
PID=$!
printf "GALOP started (PID: $PID).\n\n"

mvn clean package
printf "\n"

java -jar target/system_test-0.6.1.jar
RESULT=$?

printf "\nStopping GALOP (PID: $PID) ...\n"
kill $PID
printf "GALOP stopped.\n"

if [ $RESULT -ne 0 ]; then
    printf "\n------------------------------------------------------------------------\n"
    printf " GALOP log file"
    printf "\n------------------------------------------------------------------------\n"
    cat $LOG
fi

exit $RESULT