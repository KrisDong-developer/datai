@echo off

rem 自动提交批处理文件

set "SCRIPT_DIR=%~dp0"
set "POWERSHELL_SCRIPT=%SCRIPT_DIR%auto-commit.ps1"

rem 执行 PowerShell 脚本
powershell.exe -ExecutionPolicy Bypass -File "%POWERSHELL_SCRIPT"

pause
