#!/bin/bash

# 脚本赋权
chmod a+x *.sh

nohup java -jar ./upgrade.jar &
