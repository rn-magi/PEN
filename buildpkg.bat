set PEN_VER=PEN

WMIC OS GET CAPTION | find "Windows 8" > nul
IF not errorlevel 1 GOTO WINDOWS_8
WMIC OS GET CAPTION | find "Windows 7" > nul
IF not errorlevel 1 GOTO WINDOWS_7
WMIC OS GET CAPTION | find "Windows Vista" > nul
IF not errorlevel 1 GOTO WINDOWS_VISTA
WMIC OS GET CAPTION | find "Windows XP" > nul
IF not errorlevel 1 GOTO WINDOWS_XP
GOTO WINDOWS_UNKOWN

:WINDOWS_8
:WINDOWS_7
:WINDOWS_VISTA
set DESKTOP=%USERPROFILE%\Desktop
goto :process
:WINDOWS_XP
set DESKTOP=%USERPROFILE%\デスクトップ
goto :process
:WINDOWS_UNKOWN
goto :eof

:process

set PEN_DIR=%DESKTOP%\%PEN_VER%
set PEN_WORK=%USERPROFILE%\git\PEN
set PLUGIN_DIR=%USERPROFILE%\git\penPluginDev

mkdir "%PEN_DIR%"

xcopy /Y /S "%PEN_WORK%\sample\xDNCL\sample" "%PEN_DIR%\sample\"

mkdir "%PEN_DIR%\Manual"
copy "%PEN_WORK%\Manual\*.pdf" "%PEN_DIR%\Manual"

xcopy /Y /S "%PEN_WORK%\src\ButtonList" "%PEN_DIR%\ButtonList\"

xcopy /Y /S "%PEN_WORK%\lib" "%PEN_DIR%\lib\"
xcopy /Y /S "%PEN_WORK%\lib64" "%PEN_DIR%\lib64\"

xcopy /Y /S "%PLUGIN_DIR%\bin" "%PEN_DIR%\plugin\"

copy "%PEN_WORK%\PEN.url" "%PEN_DIR%\"
copy "%PEN_WORK%\ChangeLog.txt" "%PEN_DIR%\"
copy "%PEN_WORK%\ReadMe.txt" "%PEN_DIR%\"
copy "%PEN_WORK%\src\*.ini" "%PEN_DIR%\"

cd "%PEN_WORK%"
jar cmf src/META-INF/MANIFEST.MF PEN.jar -C bin .
move PEN.jar "%PEN_DIR%\"
