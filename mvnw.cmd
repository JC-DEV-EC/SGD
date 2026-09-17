@echo off
setlocal enabledelayedexpansion

set WRAPPER_JAR=%~dp0\.mvn\wrapper\maven-wrapper.jar
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set MVNW_REPOURL=https://repo.maven.apache.org/maven2
set MAVEN_VERSION=3.9.6

if not exist "%WRAPPER_JAR%" (
    echo Downloading Maven Wrapper...
    powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '%WRAPPER_JAR%'}"
)

if exist "%WRAPPER_JAR%" (
    java -Dmaven.multiModuleProjectDirectory="%~dp0" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
) else (
    echo Maven Wrapper jar not found. Download failed.
    exit /b 1
)
