set PEN_DIR=%USERPROFILE%\デスクトップ\PEN_v120
set PEN_WORK=%USERPROFILE%\workspace\PEN

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
