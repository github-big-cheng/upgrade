@echo off

rem ����1���������ļ�
set p1=%1
if not defined p1 (
    echo "please have a parameter of new file name with full path, such as D:\\dosmart\\download\\dosmart-controller.zip"
    exit 1
)
echo "�����µ��ļ�����%p1%��"

rem ����2������·��
set p2=%2
if not defined p2 (
    echo "please have a parameter of work path, such as D:\\dosmart"
    exit 2
)
echo "����Ŀ¼·������%p2%��"

rem ����3���ļ�����
set p3=%3
if not defined p3 (
    echo "please have a parameter of old file name, such as dosmart-controller"
    exit 3
)
echo "�������ļ�������%p3%��"


set progress_name=p3
if "%p3%" == "upgrade" (
	goto dofile
	exit
)
if "%p3%" == "dosmart-controller" (
	rem to kill progress
	set progress_name=controller.exe
)
goto killprogress
goto dofile
goto dostart


:dofile
rem change work dir
cd %p2%
if exist %p2%\\%p3% (
	rem backup
	set backup_dir=%p2%\\backup\\%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%\\
	if not exist %backup_dir% (
	rem dir not exists create them
	mkdir %backup_dir%
)
rem use winrar to backup
start winrar a %backup_dir%upgrade %p2%\\%p3%
rem use xcopy to
rem xcopy /e /y %p2%\\%p3% %backup_dir%
)

rem delete origin file
del /q %p2%\\%p3%
rem unzip with convert
start winrar x -y %p1% %p2%
rem check file
if not exist %p2%\\%p3% (
	echo "unzip failed..."
	exit 4
)


:dostart
rem do restart
if not "%p3%" == "upgrade" (
	call %p2%\\%p3%\\run.bat
)



:killprogress
echo "%progress_name%"
taskkill /F /IM %progress_name%


