@echo off
title VinRECIPE App Launcher
chcp 65001 >nul

echo ==============================================
echo  VinRECIPE - Smart Recipe ^& Grocery Planner
echo ==============================================

REM Auto-create .env if not present (so guests need zero configuration)
if not exist ".env" (
    echo [Setup] Loading database config...
    call setup-db.bat
)

echo [1/2] Starting local database backup...
start /B "" ".\mariadb-portable\mariadb-10.11.7-winx64\bin\mysqld.exe" --datadir=..\data >nul 2>&1

echo [2/2] Launching VinRECIPE...
call ".\apache-maven-3.9.6\bin\mvn.cmd" javafx:run
