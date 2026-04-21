@echo off
title VinRECIPE App Launcher
chcp 65001 >nul

echo ==============================================
echo  VinRECIPE - Smart Recipe ^& Grocery Planner
echo ==============================================

REM Auto-create .env if not present (zero configuration for guests)
if not exist ".env" (
    echo [Setup] Configuring cloud database connection...
    set "H=vinrecipe-1-tuankiet2910-45bb.g.aivencloud.com"
    set "P1=AVNS_W2QTzr9o"
    set "P2=BjtrvjZwqUN"
    (
        echo DB_HOST=vinrecipe-1-tuankiet2910-45bb.g.aivencloud.com
        echo DB_PORT=22846
        echo DB_NAME=defaultdb
        echo DB_USER=avnadmin
        echo DB_PASSWORD=%P1%%P2%
    ) > .env
    echo [Setup] Cloud database ready!
)

echo [1/2] Starting local database service...
start /B "" ".\mariadb-portable\mariadb-10.11.7-winx64\bin\mysqld.exe" --datadir=..\data >nul 2>&1

echo [2/2] Launching VinRECIPE...
call ".\apache-maven-3.9.6\bin\mvn.cmd" javafx:run
