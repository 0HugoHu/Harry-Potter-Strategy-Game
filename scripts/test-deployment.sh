#!/bin/bash

#annoying thing about docker:
#hard to tell when service inside it is actually up and running..
#have to just try to connect.
x=""
count=0
while [ "$x" == "" ]
do
    if [ "$count" == "20" ]
    then
        echo "Giving up after 20 attempts to connect!"
        exit 1
    fi
    x=`netcat -N -w 1 localhost 1651 < /dev/null`
    let count=count+1
done




nc -N localhost 1651 > testoutput <<EOF
2
99
123456
EOF
cat > expectedoutput <<EOF
Hello, welcome to Drew's Prime Factor Server!
Please enter a number:
2
Please enter a number:
3 3 11
Please enter a number:
2 2 2 2 2 2 3 643
Please enter a number:
EOF

diff testoutput expectedoutput

