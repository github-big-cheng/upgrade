#!/bin/sh


# 库点代码
CODE=$1
if [ -z "$CODE" ]; then
	echo "please have a parameter of dept code, such as dounion118"
	exit 1
fi
echo "库点代码【$CODE】"

# sql文件压缩包路径
ZIP_FILE=$2
if [ -z "$ZIP_FILE" ]; then
	echo "please have a parameter of sql's zip file with full path, such as /home/dounion/download/upgrade/sql.zip"
	exit 2
fi
if [ ! -f "$ZIP_FILE" ]; then
	# 检查文件是否存在
	echo "file doesn't exists, please check it"
	exit 3
fi
echo "文件压缩包路径【$ZIP_FILE】"

# 用户名
if [ -z "$3" ]; then
	echo "please have a parameter of sql's username, such as dounion"
	exit 4
fi
echo "用户名【$3】"

# 密码
if [ -z "$4" ]; then
        echo "please have a parameter of sql's password"
        exit 5
fi
echo "密码【******】"


# 获取当前是否存在已解压的文件夹
WORK_DIR=${ZIP_FILE%/*}
echo "work dir is $WORK_DIR"
DIR_NAME=${ZIP_FILE##/*}
DIR_NAME=${ZIP_FILE%.*}
echo "dir name is $DIR_NAME"
cd "$WORK_DIR"/
if [ -d "$DIR_NAME" ]; then
	echo "dir [$DIR_NAME]  has exists, do delete operation"
fi


# 解压zip包
unzip -o "$ZIP_FILE"


# 检查是否解压成功
if [ ! -d "$DIR_NAME" ]; then
	echo "unzip failed..."
	exit 6
fi


# loop the sql file in dir
echo "ls $DIR_NAME"
targetDir=$(ls $DIR_NAME|tr " " "\?")
for file in $targetDir
do
	file=$(tr "\?" " " <<<$file)
	echo "file is $file"
	if [ -s "file" ]; then
		continue
	fi
	if [[ ${file:0-4} == '.sql' ]]; then
	
		# set parameters as sql variables && add them to the first line of t.sql
		sed -i "2 i\ set @code='$CODE';" "$DIR_NAME"/"$file"
	
		# execute sql script
		mysql -u"$3" -p"$4" < "$DIR_NAME"/"$file"

		# clear sql script variables
		sed -i "2d" "$DIR_NAME"/"$file"
	fi
done

# 删除解压后的文件夹
rm -rf "$DIR_NAME"
