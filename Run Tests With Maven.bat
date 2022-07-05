
rem @echo off
del target\*.* /f/s/q
del *.log

REM Mvn test verify -puat
REM Mvn test verify -psit
REM Mvn test verify -pdev
call mvn clean test verify > run.log
call mvn allure:report  > allure.log
call mvn allure:serve  >> allure.log

rem bamboo mvn clean test

call "target\cucumber-html-reports\overview-features.html"
call "target\site\allure-maven-plugin\index.html"

pause