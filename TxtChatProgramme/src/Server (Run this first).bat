echo off
for /r C:\search_folder_root %%a in (*) do if "%%~nxa"=="Server (Dont Run).jar" set jarLoc=%%~dpa
java -jar %jarLoc%Server.jar %
PAUSE