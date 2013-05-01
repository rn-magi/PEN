#!/bin/sh

PEN_VER=PEN
PLUGIN_DIR=~/git/penPluginDev

DESKTOP=~/Desktop

PEN_DIR=${DESKTOP}/${PEN_VER}
PEN_WORK=~/git/PEN/

rm -irf ${PEN_DIR}

mkdir ${PEN_DIR}
mkdir ${PEN_DIR}/plugin

cp -R ${PLUGIN_DIR}/ArduinoSketch ${PEN_DIR}/

cp -R ${PEN_WORK}/sample/xDNCL/sample ${PEN_DIR}/
cp -R ${PEN_WORK}/Manual ${PEN_DIR}/
cp -R ${PEN_WORK}/src/ButtonList ${PEN_DIR}/
cp -R ${PEN_WORK}/lib ${PEN_DIR}/
cp -R ${PEN_WORK}/lib64 ${PEN_DIR}/

cp -R ${PLUGIN_DIR}/bin/* ${PEN_DIR}/plugin

cp ${PEN_WORK}/PEN.url ${PEN_DIR}/
cp ${PEN_WORK}/ChangeLog.txt ${PEN_DIR}/
cp ${PEN_WORK}/ReadMe.txt ${PEN_DIR}/
cp ${PEN_WORK}/HowToArduino.txt ${PEN_DIR}/
cp ${PEN_WORK}/src/*.ini ${PEN_DIR}/
cp ${PEN_WORK}/PEN.bat ${PEN_DIR}/
cp ${PEN_WORK}/PEN.sh ${PEN_DIR}/
cp ${PEN_WORK}/PEN64.sh ${PEN_DIR}/
cp ${PEN_WORK}/PEN.command ${PEN_DIR}/

chmod +x ${PEN_DIR}/*.sh
chmod +x ${PEN_DIR}/*.command

cd ${PEN_WORK}
jar cmf src/META-INF/MANIFEST.MF PEN.jar -C bin .
mv PEN.jar ${PEN_DIR}/
