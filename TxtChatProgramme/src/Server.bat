echo off
for /r C:\search_folder_root %%a in (*) do if "%%~nxa"=="Server.jar" set jarLoc=%%~dpa
java -jar %jarLoc%Server.jar %*
PAUSE