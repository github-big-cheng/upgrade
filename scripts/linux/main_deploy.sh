#!/bin/bash

# 参数校验

if [ -z "$1" ]; then
        echo "please have a parameter of new file name with full path, such as /home/dounion/download/dosmart-master.zip"
        exit 1
fi
file_name=$1
if [ ! "${file_name##*.}"x = "zipx"  ]; then
	echo "please upload a zip file"
	exit 2
fi
echo "待更新的文件：【$1】"


if [ -z "$2" ]; then
        echo "please have a parameter of work path, such as /home/dounion/server"
        exit 3
fi
echo "工作目录路径：【$2】"

if [ -z "$3" ]; then
        echo "please have a parameter of old file name, such as dosmart-master"
        exit 4
fi
progress_name="$3"
shell_name="run.sh"
if [ "$progress_name" = "print" ]; then
	progress_name="Jun"
	shell_name="startup.sh"
fi
echo "操作的文件名：【$3】"



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



# 复制新包
cp -a "$1"  "$2"/


# 备份文件夹
bak_dir="$2"/"$3"
if [ -d "$bak_dir" ]; then
  cd "$2"
  dt=$(date +%Y%m%d%H%M)
  tar -cvf "$3.$dt.tar"  "$3"
  # 删除旧包
  rm -rf "$bak_dir"
fi



# 解压
cd "$2"
unzip "$2"/"$3".zip 

sleep 2

# 检查是否解压成功
if [ ! -d "$bak_dir" ]; then
	echo "unzip failed"
	exit 5
fi

#启应用
if [ ! "$progress_name" = "upgrade" ]; then
	echo "nohup sh $bak_dir/$shell_name  &"
nohup sh "$bak_dir"/"$shell_name"  &
fi


