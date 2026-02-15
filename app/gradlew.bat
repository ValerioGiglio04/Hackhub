@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem  Gradle startup script for Windows
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve JAVA_HOME if possible
set JAVA_EXE=java.exe
if not "%JAVA_HOME%" == "" set JAVA_EXE="%JAVA_HOME%\bin\java.exe"

"%JAVA_EXE%" -cp "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*

if "%ERRORLEVEL%" == "0" goto init

:init
@rem End local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" endlocal

:omega
