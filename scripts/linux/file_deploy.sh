#!/bin/bash


# 参数校验
if [ ! -n "$1" ]; then
        echo "please have a parameter of new file name with full path, such as /home/dounion/download/dtools.exe"
        exit
fi
echo "待更新的文件：【$1】"


if [ ! -n "$2" ]; then
        echo "please have a parameter of work path,such as /home/dounion/download"
        exit
fi
echo "工作目录路径：【$2】"

if [ ! -n "$3" ]; then
        echo "please have a parameter of old file name,such as dtools.exe"
        exit
fi
echo "操作的文件名：【$3】"



# 备份包
bak_file="$2"/"$3"
if [ -f "$bak_war" ]; then
  dt=$(date +%Y%m%d%H%M)
  mv "$bak_file" "$bak_file"."$dt"
fi



# 复制文件
cp -a "$1" "$2"

