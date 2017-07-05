#!/bin/sh

log=$(mktemp)
echo "GALOP log file: $log"

echo "Starting GALOP ..."
nohup java -jar ../target/galop-0.4.2.jar ./galop.properties 2>> $log >> $log &
pid=$!
echo "GALOP started (PID: $pid).\n"

mvn clean compile exec:exec
result=$?

echo "\nStopping GALOP (PID: $pid) ..."
kill $pid
echo "GALOP stopped."

if [ $result -ne 0 ]; then
    echo "\n------------------------------------------------------------------------"
    echo " GALOP log file"
    echo "------------------------------------------------------------------------\n"
    cat $log
fi

exit $result