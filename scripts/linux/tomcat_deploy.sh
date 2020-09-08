#!/bin/bash

# 打印参数
echo "待更新的文件：【$1】"
echo "工作目录路径：【$2】"
echo "操作的文件名：【$3】"



# 停应用
pid=$(ps -ef | grep "$2" | grep -v grep | grep '/bin/java' | awk '{print $2}')
if [ -n "$pid" ]; then
  echo "tomcat is running, killed $pid"
  kill -9 "$pid"
fi
sleep 3



# 备份包
bak_war="$2"/webapps/"$3"
if [ -f "$bak_war" ]; then
  dt=$(date +%Y%m%d%H%M)
  bak_dir=/home/dounion/tomcat/backup/"$dt"

  if [ ! -e "$bak_dir" ]; then
    mkdir -p "$bak_dir"
  fi

  mv "$bak_war" "$bak_dir"
fi



# 部署
rm -rf "${bak_war%%.*}"
# 新包移到webapps下
cp -a "$1" "$2"/webapps/

#启应用
/"$2"/bin/startup.sh
