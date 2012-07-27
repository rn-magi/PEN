if "%ProgramFiles(x86)%XXX"=="XXX" goto x86

start javaw -cp "lib64/RXTXcomm.jar;PEN.jar" -Djava.library.path=lib64 PEN
goto checkdone
 
:x86
start avaw -cp "lib/RXTXcomm.jar;PEN.jar" -Djava.library.path=lib PEN
 
:checkdone