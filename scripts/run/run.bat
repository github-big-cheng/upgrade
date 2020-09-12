@echo off

if "%1" NEQ "" (
	call stop.bat %1
)

%1 mshta vbscript:CreateObject("WScript.Shell").Run("%~s0 ::",0,FALSE)(window.close)&&exit

java -jar ./upgrade.jar