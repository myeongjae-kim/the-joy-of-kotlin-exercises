#!/usr/bin/env sh

find . -name "*.class" -type f
find . -name "META-INF" -type d

find . -name "*.class" -type f -delete
find . -name "META-INF" -type d | xargs /bin/rm -rf

echo
echo ">> Above files and directories have been deleted. <<"
