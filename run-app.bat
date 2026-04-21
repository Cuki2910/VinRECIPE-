@echo off
title VinRECIPE App Launcher

echo [1/2] Đang khởi động cơ sở dữ liệu MariaDB ngầm...
start /B "" ".\mariadb-portable\mariadb-10.11.7-winx64\bin\mysqld.exe" --datadir=..\data >nul 2>&1

echo [2/2] Đang khởi động ứng dụng VinRECIPE (JavaFX)...
call ".\apache-maven-3.9.6\bin\mvn.cmd" javafx:run
