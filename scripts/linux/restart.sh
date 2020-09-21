#!/bin/bash

# 参数校验

if [ -z "$1" ]; then
        echo "please have a parameter of work path, such as /home/dounion/tomcat/tomcat-app-push"
        exit 1
fi
echo "工作目录路径：【$1】"

if [ -z "$2" ]; then
        echo "please have a parameter of app name, such as app"
        exit 2
fi
GREP_NAME=$2
SHELL_PATH="$1"/"$2"
SHELL_NAME="run.sh"
if [ "$2" = "print"  ]; then
	GREP_NAME="Jun"
	SHELL_NAME="start.sh"
fi
if [ "$2" = "app"  ]; then
        GREP_NAME="tomcat-app-push"
fi
echo "应用名称：【$2】"

if [ -z "$3" ]; then
        echo "please have a parameter of app type, such as Tomcat"
        exit 3
fi
if [[ ! "$3" = "Tomcat" ]]&&[[ ! "$3" = "Main"  ]]; then
	echo "only 'Tomcat' or 'Main' is supported"
	exit 4
fi
if [ "$3" = "Tomcat"  ]; then
	CALL_METHOD=""
	SHELL_PATH="$1"/bin
	SHELL_NAME="startup.sh"
fi
echo "应用类型：【$3】"



# 停应用
pid=$(ps -ef|grep "$GREP_NAME"|grep -v grep|grep 'java'|awk '{print $2}')
if [ -n "$pid" ]; then
	for p in $pid
	do
		echo "【$2】 is running, killed $p"
		kill -9 "$p"
	done
fi
sleep 3


#启应用
cd "$SHELL_PATH"
echo "$SHELL_PATH/$SHELL_NAME"
if [ "$3" = "Tomcat" ]; then
	echo "$SHELL_PATH/$SHELL_NAME"
	/"$SHELL_PATH"/"$SHELL_NAME"
fi
if [ "$3" = "Main" ]; then
	echo "sh $SHELL_NAME"
	sh "$SHELL_NAME"
fi
