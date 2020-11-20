#!/bin/bash

# 参数校验

if [ -z "$1" ]; then
        echo "please have a parameter of new file name with full path, such as /home/dounion/download/static.zip"
        exit 1
fi
file_name=$1
if [ ! "${file_name##*.}"x = "zipx"  ]; then
	echo "please upload a zip file"
	exit 2
fi
echo "待更新的文件：【$1】"


if [ -z "$2" ]; then
        echo "please have a parameter of work path, such as /home/dounion"
        exit 3
fi
echo "工作目录路径：【$2】"

if [ -z "$3" ]; then
        echo "please have a parameter of old file name, such as static"
        exit 4
fi
echo "操作的文件名：【$3】"



# 复制新包
cp -a "$1"  "$2"/


# 移动到指定目录
cd "$2"


# 备份文件夹
bak_dir="$2"/"$3"
if [ -d "$bak_dir" ]; then
  dt=$(date +%Y%m%d%H%M)
  tar -cvf "$3.$dt.tar"  "$3"
  # 删除旧包
  rm -rf "$bak_dir"
fi



# 解压
unzip "$2"/"$3".zip 

sleep 2


# 删除压缩包
rm -rf "$2"/"$3".zip


# 检查是否解压成功
if [ ! -d "$bak_dir" ]; then
	echo "unzip failed"
	exit 5
fi

