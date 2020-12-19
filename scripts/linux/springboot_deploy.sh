#!/bin/bash

# 参数校验

if [ -z "$1" ]; then
        echo "please have a parameter of new file name with full path, such as /home/dounion/download/sso_web.jar"
        exit 1
fi
file_name=$1
if [ ! "${file_name##*.}"x = "jarx"  ]; then
	echo "please upload a jar file"
	exit 2
fi
echo "待更新的文件：【$1】"


if [ -z "$2" ]; then
        echo "please have a parameter of work path, such as /home/dounion/server"
        exit 3
fi
echo "工作目录路径：【$2】"

if [ -z "$3" ]; then
        echo "please have a parameter of old file name, such as sso_web"
        exit 4
fi
progress_name="$3"
shell_name="run.sh"
if [ "$progress_name" = "print" ]; then
	progress_name="Jun"
	shell_name="startup.sh"
fi
echo "操作的文件名：【$3】"


bak_dir=$4
if [ -z "$bak_dir" ]; then
	bak_dir=/home/dounion/backup
fi
bak_dir="$4"/"$(date +%Y%m%d)"
echo "文件备份路径：【$4】"


# 停应用 按应用名称查询进程号
if [ ! "$progress_name" = "upgrade"  ]; then
	echo "progress_name to grep is $progress_name"
	PID=$(ps -ef | grep "$progress_name" | grep -v grep | grep 'java' | awk '{print $2}')	
	echo "PID is $PID"
	if [ -n "$PID" ]; then
		for p in $PID
		do
			kill -9 "$p"
                	echo "killed progress by ps, pid is 【$p】"
			sleep 1
		done
	fi
fi


# 移动目录
cd "$2"


# 备份文件夹
dt=$(date +%Y%m%d%H%M)
cp -a "$3".jar  "$bak_dir"/"$3".jar."$dt"


# 复制新包
cp -a "$1"  "$2"/



#启应用
if [ ! "$progress_name" = "upgrade" ]; then
	cd "$bak_dir"
	echo "nohup sh $shell_name  &"
	nohup sh "$shell_name"  &
fi


