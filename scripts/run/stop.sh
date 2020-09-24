#!/bin/bash

# 查找进程
pid=$(ps -ef|grep 'upgrade'|grep java |grep -v grep|awk '{print $2}')
if [ -n "$pid" ]; then
	for p in $pid
	do
		echo "upgrade is running, killed $p"
		kill -9 "$p"		
		sleep 2
	done
fi
