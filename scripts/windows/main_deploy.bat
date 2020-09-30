@echo off

rem 参数1：待更新文件
set p1=%1
if not defined p1 (
    echo "please have a parameter of new file name with full path, such as D:\dosmart\download\dosmart-controller.zip"
    exit 1
)
echo "待更新的文件：【%p1%】"

rem 参数2：工作路径
set p2=%2
if not defined p2 (
    echo "please have a parameter of work path, such as D:\dosmart"
    exit 2
)
echo "工作目录路径：【%p2%】"

rem 参数3：文件名称
set p3=%3
if not defined p3 (
    echo "please have a parameter of old file name, such as dosmart-controller"
    exit 3
)
echo "操作的文件名：【%p3%】"


if "%p3%" == "upgrade" (
	goto dofile
	exit
)
set progress_name=%p3%
if "%p3%" == "dosmart-controller-1.0.0" (
	rem to kill progress
	set progress_name=controller.exe
)
call :killprogress
call :dofile
goto dostart
exit


:dobackup
	rem backup
	set "backup_dir=%p2%\backup\%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%"
	::echo "%backup_dir%"
	
	rem dir not exists create them
	if not exist %backup_dir% mkdir %backup_dir%
	
	rem use winrar to backup
	
	start winrar a %backup_dir%\upgrade %p2%\%p3%
	
	rem use xcopy to
	rem xcopy /e /y %p2%\%p3% %backup_dir%
goto :eof


:dofile
	rem change work dir
	cd %p2%

	if exist %p2%\%p3% goto dobackup

	rem delete origin file
	del /q %p2%\%p3%

	rem unzip with convert
	start winrar x -y %p1% %p2%
	if?errorlevel?1 (
		echo "backup failed..."
		exit
	)

goto :eof


:dostart
	echo %cd%
	if not exist %p2%\%p3% (
		echo "unzip failed..."
		exit
	)
	rem do restart
	if not "%p3%" == "upgrade" (
		call %p2%\%p3%\run.bat
	)
goto :eof



:killprogress
	echo "%progress_name%"
	taskkill /F /IM %progress_name%
goto :eof


