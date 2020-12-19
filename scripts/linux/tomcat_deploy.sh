#!/bin/bash

# 参数校验

if [ -z "$1" ]; then
        echo "please have a parameter of new file name with full path, such as /home/dounion/download/app.war"
        exit
fi
echo "待更新的文件：【$1】"


if [ -z "$2" ]; then
        echo "please have a parameter of work path, such as /home/dounion/tomcat/tomcat-app"
        exit
fi
echo "工作目录路径：【$2】"

if [ -z "$3" ]; then
        echo "please have a parameter of old file name, such as app.war"
        exit
fi
echo "操作的文件名：【$3】"

GREP_NAME=${3%.*}
if [ "$GREP_NAME" = 'app'  ]; then
	GREP_NAME="tomcat-app-push"
fi
echo "GREP_NAME is $GREP_NAME"


bak_dir=$4
if [ -z "$bak_dir"  ]; then
        bak_dir=/home/dounion/tomcat/backup
fi
bak_dir="$bak_dir"/"$(date +%Y%m%d)"
echo "备份路径：【$bak_dir】"


# 停应用
pid=$(ps -ef|grep "$GREP_NAME"|grep -v grep|grep '/bin/java'|awk '{print $2}')
if [ -n "$pid" ]; then
	for p in $pid
	do
  		echo "tomcat is running, killed $p"
  		kill -9 "$p"
	done
fi
sleep 3



# 备份包
cd "$2"/webapps
bak_war=$3
if [ -f "$bak_war" ]; then
  dt=$(date +%Y%m%d%H%M)

  if [ ! -e "$bak_dir" ]; then
    mkdir -p "$bak_dir"
  fi

  mv "$bak_war" "$bak_dir"/"$bak_war"."$dt"
fi



# 部署
rm -rf "$3"
# 新包移到webapps下
cp -a "$1" "$2"/webapps/

#启应用
/"$2"/bin/startup.sh
