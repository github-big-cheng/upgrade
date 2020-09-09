#!/bin/bash

# 查找进程
pid=$(ps -ef|grep 'upgrade'|grep -v grep|awk '{print $2}')
if [ -n "$pid" ]; then
  echo "upgrade is running, killed $pid"
  kill -9 "$pid"
fi

