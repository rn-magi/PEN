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
set PEN_WORK=%USERPROFILE%\git\PEN\PEN
set PLUGIN_DIR=%USERPROFILE%\git\penPluginDev\penPluginDev

mkdir "%PEN_DIR%"
mkdir "%PEN_DIR%\src"

copy "%PEN_WORK%\*.java" "%PEN_DIR%\src\"
copy "%PEN_WORK%\IntVParser.jjt" "%PEN_DIR%\src\"
copy "%PEN_WORK%\*.ini" "%PEN_DIR%\src\"
copy "%PEN_WORK%\pen.png" "%PEN_DIR%\src\"

mkdir "%PEN_DIR%\src\Locale"
copy "%PEN_WORK%\Locale\*.properties" "%PEN_DIR%\src\Locale\"

mkdir "%PEN_DIR%\src\ButtonList"
copy "%PEN_WORK%\ButtonList\*.ini" "%PEN_DIR%\src\ButtonList"

mkdir "%PEN_DIR%\ArduinoSketch"
copy "%PLUGIN_DIR%\ArduinoSketch\*" "%PEN_DIR%\ArduinoSketch"

mkdir "%PEN_DIR%\src\plugin"
copy "%PLUGIN_DIR%\src\*.java" "%PEN_DIR%\src\plugin"

cd "%PEN_WORK%\"
xcopy /Y /S "%PEN_WORK%\org" "%PEN_DIR%\src\org\"

cd "%PEN_WORK%\sample\xDNCL\"
xcopy /Y /S "%PEN_WORK%\sample\xDNCL\sample" "%PEN_DIR%\sample\"

mkdir "%PEN_DIR%\Manual"
copy "%PEN_WORK%\Manual\*.pdf" "%PEN_DIR%\Manual"

mkdir "%PEN_DIR%\ButtonList"
copy "%PEN_WORK%\ButtonList\*.ini" "%PEN_DIR%\ButtonList"

mkdir "%PEN_DIR%\lib"
copy "%PEN_WORK%\lib\*" "%PEN_DIR%\lib"

mkdir "%PEN_DIR%\lib64"
copy "%PEN_WORK%\lib64\*" "%PEN_DIR%\lib64"

mkdir "%PEN_DIR%\plugin"
copy "%PLUGIN_DIR%\bin\*.class" "%PEN_DIR%\plugin"

copy "%PEN_WORK%\PEN.url" "%PEN_DIR%\"
copy "%PEN_WORK%\ChangeLog.txt" "%PEN_DIR%\"
copy "%PEN_WORK%\ReadMe.txt" "%PEN_DIR%\"
copy "%PEN_WORK%\*.ini" "%PEN_DIR%\"
copy "%PEN_WORK%\PEN.bat" "%PEN_DIR%\"
copy "%PEN_WORK%\PEN.command" "%PEN_DIR%\"
copy "%PEN_WORK%\PEN.sh" "%PEN_DIR%\"
copy "%PEN_WORK%\PEN64.sh" "%PEN_DIR%\"

cd "%PEN_WORK%"
jar cmf META-INF/MANIFEST.MF PEN.jar -C bin .
move PEN.jar "%PEN_DIR%\"
