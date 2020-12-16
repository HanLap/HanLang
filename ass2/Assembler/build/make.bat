@echo off
rem :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
rem ::             WLA DX make file                            ::
rem :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
rem :: Do not edit anything unless you know what you're doing! ::
rem :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

set WLAPATH=%~dp0..\

rem Cleanup to avoid confusion
if exist main.o del main.o

rem Compile
"%WLAPATH%wla-gb.exe" main.asm

if NOT ["%errorlevel%"]==["0"] (
    pause
    exit /b %errorlevel%
)

rem Make linkfile
echo [objects]>linkfile
echo main.o>>linkfile

rem Link
"%WLAPATH%wlalink.exe" -d -r -v -s linkfile output.gb 2> wlalink-log.txt || (type wlalink-log.txt | findstr /v DISCARD && false)
findstr Bank < wlalink-log.txt 2> nul

if NOT ["%errorlevel%"]==["0"] (
    pause
    exit /b %errorlevel%
)

rem Cleanup to avoid mess
if exist linkfile del linkfile
if exist main.o del main.o
if exist wlalink-log.txt del wlalink-log.txt