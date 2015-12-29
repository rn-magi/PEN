# 初学者向けプログラミング学習環境 PEN
PENは初学者向けのプログラミング学習環境です。

PENで動作するプログラム言語は  
　センター試験用手順記述標準言語 DNCL  
に拡張を行った xDNCL言語 を用いています。

* 初学者向けプログラミング学習環境 PEN 公式サイト
	* <http://www.media.osaka-cu.ac.jp/PEN/>
* 開発メモWiki
	* <http://pen.moe.hm/>

## 実行ファイルの生成と実行

```
% ant build
% cd ./pkg/run
% java -jar PEN.jar
```

## 実行ファイルの削除

```
% ant clean
```

## 実行ファイルを削除して再生成

```
% ant rebuild
```

## 配布パッケージの作成

```
% ant release
```

`./pkg` に配布用のパッケージが生成されます。

## 開発者について

* **中村 亮太** ( *Ryota Nakamura* ) - 大阪市立大学 大学院創造都市研究科
* **西田 知博** ( *Tomohiro Nishida* ) - 大阪学院大学 情報学部
* **松浦 敏雄** ( *Toshio Matsuura* ) - 大阪市立大学 大学院創造都市研究科