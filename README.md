# 初学者向けプログラミング学習環境 PEN
PENは初学者向けのプログラミング学習環境です。

PENで動作するプログラム言語は  
 大学入試センターの手順記述言語 DNCL    
に拡張を行った xDNCL言語 を用いています。

## 実行ファイルの生成と実行

	% ant -f build.xml make
	% cd ./pkg
	% java -jar PEN.jar

## 配布パッケージの作成

	% ant -f build.xml build