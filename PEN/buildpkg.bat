set PEN_VER=PEN_v120p

F not EXIST ％SystemRoot％￥system32￥wbem￥wmic.exe goto WINDOWS_UNKOWN
WMIC OS GET CAPTION | find "Windows 7" > nul
IF not errorlevel 1 GOTO WINDOWS_7
WMIC OS GET CAPTION | find "Windows Vista" > nul
IF not errorlevel 1 GOTO WINDOWS_VISTA
WMIC OS GET CAPTION | find "Windows XP" > nul
IF not errorlevel 1 GOTO WINDOWS_XP
GOTO WINDOWS_UNKOWN

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

mkdir "%PEN_DIR%"
mkdir "%PEN_DIR%\src"

copy "%PEN_WORK%\*.java" "%PEN_DIR%\src\"
copy "%PEN_WORK%\IntVParser.jjt" "%PEN_DIR%\src\"
copy "%PEN_WORK%\Property.ini" "%PEN_DIR%\src\"
copy "%PEN_WORK%\pen.png" "%PEN_DIR%\src\"

mkdir "%PEN_DIR%\src\Locale"
copy "%PEN_WORK%\Locale\*.properties" "%PEN_DIR%\src\Locale\"

mkdir "%PEN_DIR%\\src\ButtonList"
copy "%PEN_WORK%\ButtonList\*.ini" "%PEN_DIR%\src\ButtonList"

cd "%PEN_WORK%\"
xcopy  /Y /S /EXCLUDE:nocopy.txt "%PEN_WORK%\rossi" "%PEN_DIR%\src\rossi\"

cd "%PEN_WORK%\"
xcopy  /Y /S /EXCLUDE:nocopy.txt "%PEN_WORK%\org" "%PEN_DIR%\src\org\"

cd "%PEN_WORK%\sample\xDNCL\"
xcopy  /Y /S /EXCLUDE:nocopy.txt "%PEN_WORK%\sample\xDNCL\sample" "%PEN_DIR%\sample\"

mkdir "%PEN_DIR%\Manual"
copy "%PEN_WORK%\Manual\*.pdf" "%PEN_DIR%\Manual"

mkdir "%PEN_DIR%\ButtonList"
copy "%PEN_WORK%\ButtonList\*.ini" "%PEN_DIR%\ButtonList"

copy "%PEN_WORK%\PEN.url" "%PEN_DIR%\"
copy "%PEN_WORK%\ChangeLog.txt" "%PEN_DIR%\"
copy "%PEN_WORK%\ReadMe.txt" "%PEN_DIR%\"
copy "%PEN_WORK%\*.ini" "%PEN_DIR%\"

cd "%PEN_WORK%"
jar cmf META-INF/MANIFEST.MF PEN.jar -C bin .
move PEN.jar "%PEN_DIR%\"
