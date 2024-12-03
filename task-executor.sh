#!/bin/sh -l
FILE_PATH="/tmp/task"
# use touch /tmp/task to trigger the task
# Check if the file exists
if [ -f "$FILE_PATH" ]; then
    NOW=$( date '+%F_%H:%M:%S' )
    echo ""
    echo "$NOW: $FILE_PATH"

    echo "File exists. Executing code..."
    echo "Checking"

    /root/guard.sh $CHECK_URL >> /root/guard.log 2>&1

    echo "Checking hard"
    /root/guard-hard.sh $CHECK_URL?hard=true >> /root/guard.hard.log 2>&1
    # Add your code here
    # Example: Print the contents of the file

    # Delete the file
    rm "$FILE_PATH"
    echo "File deleted."
fi