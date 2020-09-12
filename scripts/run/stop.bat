
@echo off
title stop upgrade
echo  ━━━━━━━━━━━━
echo  　　　┏┓　　　┏┓
echo  　　┏┛┻━━━┛┻┓
echo  　　┃　　　　　　　┃
echo  　　┃　　　━　　　┃
echo  　　┃　┳┛　┗┳　┃
echo  　　┃　　　　　　　┃
echo  　　┃　　　┻　　　┃
echo  　　┃　　　　　　　┃ 
echo  　　┗━┓　　　┏━┛
echo  　　　　┃　　　┃
echo  　　　　┃　　　┃
echo  　　　　┃　　　┗━━━┓
echo  　　　　┃　　　　　　　┣┓
echo  　　　　┃　　　　　　　┏┛
echo  　　　　┗┓┓┏━┳┓┏┛
echo  　　　　　┃┫┫　┃┫┫
echo  　　　　　┗┻┛　┗┻┛
echo  ━━━━━━━━━━━━
:start1
set /p port=请输入需要关闭的端口号：
setlocal enabledelayedexpansion
set queryResult=0
for /f "delims=  tokens=1" %%a in ('netstat -aon ^| findstr "%port%" ^|findstr "LISTENING"') do (
	set a1=%%a

	set isCanUse=0
	for /f "tokens=2 delims= " %%b in ("!a1!") do (
		set a2=%%b
		for /f "tokens=2 delims=:" %%c in ("!a2!") do (
			set a3=%%c
			if %port%==!a3! (
				set isCanUse=1
			)
		)
		if !isCanUse!==1 (
		
			echo 查询到匹配的进程信息--!a1!
			set queryResult=1

			for /f "tokens=5 delims= " %%d in ("!a1!") do (
				set killPid=%%d
				for /f "tokens=1 delims= " %%e in ('tasklist^|findstr  !killPid!') do (
					set /p isConfirm=确认关闭%%e进程吗^?^(不了解的进程请不要乱关闭^)^(Y^/N^)： 
					set alreadyKilled=0
					if !isConfirm!==y (
						taskkill /f /t /im  %%e
						set alreadyKilled=1
					)
					if !isConfirm!==Y (
						taskkill /f /t /im  %%e
						set alreadyKilled=1
					)
					if !alreadyKilled!==0 (
						if !isConfirm! NEQ Y (
							echo 已取消
							set port=0
							goto start1
						)
						if !isConfirm! NEQ Y (
							echo 已取消
							goto start1
							set port=0
						)
					)
				)
			)
		)
	)
)
if !queryResult!==0 (
		echo 未找到对应进程
	)
goto start1
pause