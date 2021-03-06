#!/bin/bash


DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "$DIR"

# 检查是否有残留进程
cd "$DIR"
if [ -z "$1" ]; then
	sh stop.sh
fi

# 脚本赋权
if [ ! -x "$DIR" ]; then
	chmod a+x *.sh
fi

JAVA_OPTS="-Xms6m"
# 启动
nohup java -jar "$JAVA_OPTS" upgrade.jar "$1"  &
