import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * さまざまな描画をおこなうJPanelを拡張したクラスです
 * 
 * @author Ryota Nakamura
 * @author Takeo Yamamoto
 */
public class IntVgOutputWindow extends JPanel{
	private JFrame frame = new JFrame("GraphicWindow");
	
	/**
	 * <code>DEFAULT_DRAW_COLOR</code> のコメント
	 * 描画の初期色
	 */
	public final static Color DEFAULT_DRAW_COLOR = new Color(0, 0, 0);
	
	/**
	 * <code>DEFAULT_BACK_COLOR</code> のコメント
	 * 描画ウィンドウの初期色
	 */
	public final static Color DEFAULT_BACK_COLOR = new Color(255, 255, 255);

	/**
	 * <code>DEFAULT_STROKE</code> のコメント
	 * 図形の輪郭線を描画する属性の初期値
	 */
	public final static BasicStroke DEFAULT_STROKE = new BasicStroke();
	
	/**
	 * <code>DEFAULT_FONT</code> のコメント
	 * テキストを目に見える形に描画するために使用されるフォントの初期値
	 */
	public final static Font DEFAULT_FONT= new Font("Default", Font.PLAIN, 10);
	
	private BufferedImage image;
	private Graphics2D imageGraphics;
	
	private Color Fill_Color;
	private Color Line_Color;
	private Color Text_Color;
	private int lineshape;
	private double linewidth;
	private int dottype;
	private int arrowtype;
	private int arrowedge;
	private int arrowsize;
	private int fonttype;
	private int fontsize;
	private int windowsizeX;
	private int windowsizeY;
	private String font;

	private double xPoint = 0;
	private double yPoint = 0;
	
	private double xRange;
	private double yRange;
	
	private boolean DefaultOriginPoint = false;
	private boolean originPoint = false;
	
	private boolean drawGraph = false;
	
	private boolean repaintFlag = true;
	
	private MainGUI gui = null;
	private ConsoleAppend consoleAppend = null;
	
	/* (非 Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent((Graphics2D) g);
		((Graphics2D) g).drawImage(image, 0, 0, this);
	}
	
	/**
	 * 新しい <code>IntVgOutputWindow</code> オブジェクトを構築します。
	 */
	public IntVgOutputWindow(){
		//setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
	}
	
	/**
	 * 新しい <code>IntVgOutputWindow</code> オブジェクトを構築します。
	 */
	public IntVgOutputWindow(MainGUI gui){
		this.gui = gui;
		consoleAppend = gui.consoleAppend;

		//setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
	}
	
	/**
	 * 描画ィンドウを初期化、生成を行うメソッド
	 * 
	 * @param width
	 * 描画ウィンドウの横幅
	 * @param height
	 * 描画ウィンドウの縦幅
	 */
	public void gOpenWindow(int width, int height){
		setBackground(IntVgOutputWindow.DEFAULT_BACK_COLOR);
		setSize(width, height);
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		imageGraphics = (Graphics2D)image.createGraphics();
		imageGraphics.setFont(IntVgOutputWindow.DEFAULT_FONT);
		imageGraphics.setStroke(IntVgOutputWindow.DEFAULT_STROKE);
		imageGraphics.setColor(IntVgOutputWindow.DEFAULT_DRAW_COLOR);
		
		imageGraphics.setBackground(IntVgOutputWindow.DEFAULT_BACK_COLOR);
		imageGraphics.clearRect(0, 0, width, height);
		
		imageGraphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		imageGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		imageGraphics.setRenderingHint(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE);
		imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		imageGraphics.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		imageGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		imageGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		imageGraphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);
		
		setDefaultParameter();
		windowsizeX	= width;
		windowsizeY 	= height;
		
		originPoint	= DefaultOriginPoint;
		drawGraph = false;
		
		if(originPoint){
			xPoint = 0;
			yPoint = height;
		} else {
			xPoint = 0;
			yPoint = 0;
		}
		
		xRange = width;
		yRange = height;
		
		setRepaintFlag(true);
		
		frame.setSize(width + 6, height + 25);
		frame.getContentPane().add(this);
		frame.setVisible(true);
	}
	
	/**
	 * 描画ィンドウを初期化、生成を行うメソッド
	 * 
	 * @param width
	 * 描画ウィンドウの横幅
	 * @param height
	 * 描画ウィンドウの縦幅
	 * @param x
	 * 横の原点座標
	 * @param y
	 * 縦の原点座標
	 */
	public void gOpenWindow(int width, int height, double x, double y){
		gOpenWindow(width, height);
		gSetOrigin(x, y);
	}
	
	/*
	public void gOpenGraphWindow(int width, int height, double x, double y){
		gOpenWindow(width, height, x, y);
		drawGraph(width, height, x, y);
	}
	*/

	/**
	 * グラフウィンドウを開くためのメソッド
	 * 
	 * @param width
	 * グラフウィンドウの横幅
	 * @param height
	 * グラフウィンドウの縦幅
	 * @param x1
	 * 横軸の開始座標
	 * @param x2
	 * 横軸の終了座標
	 * @param y1
	 * 縦軸の開始座標
	 * @param y2
	 * 縦軸の終了座標
	 */
	public void gOpenGraphWindow(int width, int height, double x1, double y1, double x2, double y2){
		gOpenGraphWindow(width, height, x1, y1, x2, y2, true);
	}
	
	/**
	 * グラフウィンドウを開くためのメソッド
	 * 
	 * @param width
	 * グラフウィンドウの横幅
	 * @param height
	 * グラフウィンドウの縦幅
	 * @param x1
	 * 横軸の開始座標
	 * @param x2
	 * 横軸の終了座標
	 * @param y1
	 * 縦軸の開始座標
	 * @param y2
	 * 縦軸の終了座標
	 * @param drawGraph
	 * 座標軸の描画有無
	 */
	public void gOpenGraphWindow(int width, int height, double x1, double y1, double x2, double y2, boolean drawGraph){
		double x, y;

		double xRangel = Math.abs(x1) + Math.abs(x2);
		double yRangel;
		
		if(x1 < x2){
			x = -x1;
		} else {
			x = -x2;
		}
		
		if(y1 > y2){
			y = y1;
			yRangel = Math.abs(y2 - y1);
		} else {
			y = y2;
			yRangel = Math.abs(y1 - y2);
		}
		
		double xx = x * width / xRangel;
		double yy = y * height / yRangel;
		
		gOpenWindow(width, height, xx, yy);
		
		if(drawGraph){
			drawGraph(width, height, xx, yy);
		}
		
		this.drawGraph = drawGraph;
		
		xRange = xRangel;
		yRange = yRangel;
		xPoint = x;
		yPoint = y;
	}
	
	/**
	 * 描画ウィンドウに座標軸を描画するメソッド
	 * 
	 * @param width
	 * 描画ウィンドウの横幅
	 * @param height
	 * 描画ウィンドウの高さ
	 * @param x
	 * 横の原点座標
	 * @param y
	 * 縦の原点座標
	 */
	public void drawGraph(int width, int height, double x, double y){
		int fSize = fontsize;
		
		double xRangeTEMP = this.xRange;
		double yRangeTEMP = this.yRange;
		double xPointTEMP = this.xPoint;
		double yPointTEMP = this.yPoint;
		
		this.xRange = width;
		this.yRange = height;
		this.xPoint = x;
		this.yPoint = y;
		
		gSetArrowDir(1);
		gSetArrowType(2);
		gSetLineColor(200, 200, 200);
		gSetFontSize(15);
		gSetTextColor(200, 200, 200);
		
		gDrawLine(0, y, 0, y - height);
		gDrawLine(width - x, 0, -x, 0);
		
		gDrawText("x", (int) (width - x - 10) , 10);
		gDrawText("y", 12 , (int) (y - 12));

		setDefaultParameter();
		
		gSetArrowDir(arrowedge);
		gSetArrowType(arrowtype);
		gSetFontSize(fSize);
		gSetLineColor(IntVgOutputWindow.DEFAULT_DRAW_COLOR.getRed(), IntVgOutputWindow.DEFAULT_DRAW_COLOR.getGreen(), IntVgOutputWindow.DEFAULT_DRAW_COLOR.getBlue());
		gSetTextColor(IntVgOutputWindow.DEFAULT_DRAW_COLOR.getRed(), IntVgOutputWindow.DEFAULT_DRAW_COLOR.getGreen(), IntVgOutputWindow.DEFAULT_DRAW_COLOR.getBlue());
		
		this.xRange = xRangeTEMP;
		this.yRange = yRangeTEMP;
		this.xPoint = xPointTEMP;
		this.yPoint = yPointTEMP;
	}
	
	/**
	 * 描画Windowを閉じるメソッド
	 */
	public void gCloseWindow(){
		frame.setVisible(false);
		image = null;
		myrepaint();
	}
	
	/**
	 * 描画Windowをクリアするメソッド
	 */
	public void gClearWindow(){
		imageGraphics.clearRect(0,0, windowsizeX, windowsizeY);
		if(drawGraph) {
			double xx = xPoint * windowsizeX / xRange;
			double yy = yPoint * windowsizeY / yRange;
			drawGraph(windowsizeX, windowsizeY, xx, yy);
		}
		myrepaint();
	}
	
	
	/**
	 * 描画Windowsを保存するメソッド
	 * 
	 * @param path
	 * 保存先
	 * @param mode
	 * 保存形式 ( png, jpeg )
	 */
	public void gSaveWindow(String path, String mode){
		File file = new File(path);
		
		try {
			ImageIO.write(image, mode, file);
		} catch (IOException e) {
			exception(e.getLocalizedMessage());
		}
	}
	
	/**
	 * 描画ウィンドウの原点を変更するメソッド
	 * 
	 * @param x
	 * 横の原点座標
	 * @param y
	 * 縦の原点座標
	 */
	public void gSetOrigin(double x, double y){
		originPoint = true;
		
		xPoint = x;
		yPoint = y;
	}
	
	/**
	 * 描画ウィンドウの座標を仮想座標に変更するメソッド
	 * 
	 * @param x1
	 * 横軸の開始座標
	 * @param x2
	 * 横軸の終了座標
	 * @param y1
	 * 縦軸の開始座標
	 * @param y2
	 * 縦軸の終了座標
	 */
	public void gSetMap(double x1, double y1, double x2, double y2){
		double x, y;
		
		if(x1 < x2){
			x = 0 - x1;
		} else {
			x = 0 - x2;
		}
		
		if(y1 > y2){
			y = y1;
			yRange = Math.abs(y2 - y1);
		} else {
			y = y2;
			yRange = Math.abs(y1 - y2);
		}
		
		xRange = Math.abs(x1) + Math.abs(x2);
		
		gSetOrigin(x, y);
	}
	
//############################（色属性）#######################################
	
	/**
	 * このグラフィックスコンテキストの現在の色を、
	 * 指定された色に設定します。
	 * このグラフィックスコンテキストを使うこれ以降のレンダリング操作は、
	 * ここで指定された色を使用します。
	 * 
	 * @param r
	 * 赤色成分
	 * @param g
	 * 緑色成分
	 * @param b
	 * 青色成分
	 * @see #gDrawOval
	 * @see #gFillOval
	 * @see #gDrawLine
	 * @see #gDrawBox
	 * @see #gFillBox
	 * @see #gDrawArc
	 * @see #gFillArc
	 * @see #gDrawText
	 * @see #gDrawPolygon
	 * @see #gFillPolygon
	 * @see #gDrawPolyline
	 */
	public void gSetFillColor(int r, int g, int b){
		Fill_Color = new Color(r, g, b);
	}
	
	/**
	 * このグラフィックスコンテキストの現在の色を、
	 * 指定された色に設定します。
	 * このグラフィックスコンテキストを使うこれ以降のレンダリング操作は、
	 * ここで指定された色を使用します。
	 * 
	 * @param r
	 * 赤色成分
	 * @param g
	 * 緑色成分
	 * @param b
	 * 青色成分
	 * @see #gDrawOval
	 * @see #gDrawPoint
	 * @see #gFillOval
	 * @see #gDrawLine
	 * @see #gDrawBox
	 * @see #gFillBox
	 * @see #gDrawArc
	 * @see #gFillArc
	 * @see #gDrawText
	 * @see #gDrawPolygon
	 * @see #gFillPolygon
	 * @see #gDrawPolyline
	 */
	public void gSetLineColor(int r, int g, int b){
		Line_Color = new Color(r, g, b);
	}
	
	/**
	 * このグラフィックスコンテキストの現在の色を、
	 * 指定された色に設定します。
	 * このグラフィックスコンテキストを使うこれ以降のレンダリング操作は、
	 * ここで指定された色を使用します。
	 * 
	 * @param r
	 * 赤色成分
	 * @param g
	 * 緑色成分
	 * @param b
	 * 青色成分
	 * @see #gDrawOval
	 * @see #gDrawPoint
	 * @see #gFillOval
	 * @see #gDrawLine
	 * @see #gDrawBox
	 * @see #gFillBox
	 * @see #gDrawArc
	 * @see #gFillArc
	 * @see #gDrawText
	 * @see #gDrawPolygon
	 * @see #gFillPolygon
	 * @see #gDrawPolyline
	 */
	public void gSetTextColor(int r, int g, int b){
		Text_Color = new Color(r, g, b);
	}
	
//########################（フォント属性/文字列描画）#############################
	
	/**
	 * フォントの種類を変更するメソッド。
	 * 
	 * @param fontName
	 * フォントの種類
	 */	
	public void gSetFont(String fontName){
		if( fontName.equals("明朝") ){
			font = "Serif";
		}else if( fontName.equals("ゴシック") ){
			font = "SansSerif";
		}else{
			font = fontName;
		}
		gRehashFont();
	}
	
	/**
	 * フォントのスタイルを更するメソッド。
	 * 
	 * @param type
	 * フォントのスタイル(PLAIN,BOLD,ITALIC)
	 */	
	public void gSetFontType(int type){
		fonttype = type;
		gRehashFont();
	}
	
	/**
	 * フォントのサイズを変更するメソッド。
	 * 
	 * @param size
	 * フォントのサイズ
	 */	
	public void gSetFontSize(int size){
		fontsize = size;
		gRehashFont();
	}
	
	public void gRehashFont(){
		imageGraphics.setFont(new Font(font,fonttype,fontsize));
	}
		
	/**
	 * 文字列を描画するメソッド。
	 * 色は gSetTextColor のメソッドで指定したもの。
	 * このグラフィックスコンテキストの現在のフォントと色を使い、
	 * 指定された文字列によって指定されたテキストを描きます。
	 * 左端の文字のベースラインは、このグラフィックスコンテキストの
	 * 座標系の位置 (x, y) にあります。
	 * 
	 * @param str
	 * @param x
	 * @param y
	 * @see #gSetTextColor
	 * @see #gSetFont
	 * @see #gSetFontType
	 * @see #gSetFontSize
	 */
	public void gDrawText(String str, int x, int y){
		int x2 = getXpoint(x);
		int y2 = getYpoint(y);

		imageGraphics.setColor(Text_Color);
		imageGraphics.drawString(str, x2, y2);
		myrepaint();
	}
	
//###########################（点の種類/点描画）##################################

	/**
	 * 点の種類を変更するメソッド。
	 * 
	 * @param type
	 * t点の種類
	 */	
	public void gSetDotShape(int type){
		dottype = type;
	}
	
	/**
	 * 点を描画するメソッド。
	 * 色は gSetLineColor のメソッドで指定したもの。
	 * 
	 * @param x
	 * 中心点 x の座標です。
	 * @param y
	 * 中心点 y の座標です。
	 * @see #gSetLineColor
	 * @see #gDrawOval
	 * @see #gFillOval
	 */
	public void gDrawPoint(double x, double y){
		double x2 = getXpoint(x);
		double y2 = getYpoint(y);
		
		imageGraphics.setColor(Line_Color);
		switch(dottype){
			case 0:
				imageGraphics.fill(new Ellipse2D.Double(x2, y2, 1, 1));
				break;
			case 1:
				imageGraphics.fill(new Ellipse2D.Double(x2 - 2, y2 - 2, 5, 5));
				break;
			case 2:
				imageGraphics.fill(new Ellipse2D.Double(x2 - 4, y2 - 4, 11, 11));
				break;
			default:
		}
		myrepaint();
	}
	
	/**
	 * 矢じり属性を変更するメソッド
	 * @param size
	 * 矢じりのサイズを指定します。
	 */
	public void gSetArrowSize(int size){
		arrowsize = size;
	}
	
	/**
	 * 矢じり属性を変更するメソッド
	 * @param edge
	 * 両端の矢じりの有無を指定します。[0:なし、1：始点、2:終点、3:両端]
	 */
	public void gSetArrowDir(int edge){
		arrowedge = edge;
	}
	
	/**
	 * 矢じり属性を変更するメソッド
	 * @param type
	 * 矢じりの種類
	 */
	public void gSetArrowType(int type){
		arrowtype = type;
	}
	
	/**
	 * 矢じり属性を変更するメソッド
	 * @param type
	 * 矢じりの種類
	 * @param edge
	 * 両端の矢じりの有無を指定します。[0:なし、1：始点、2:終点、3:両端]
	 */
	public void gSetArrowType(int type, int edge){
		arrowtype = type;
		arrowedge = edge;
	}

	/**
	 * type番目に指定された破線パターンをセットします。
	 * @param type 破線の種類
	 */
	public void gSetLineShape(int type){
		gSetLineShape(type, linewidth);
	}
	
	/**
	 * type番目に指定された破線パターンをセットします。
	 * @param type 破線の種類
	 */
	public void gSetLineShape(int type, double width){
		lineshape = type;		//lineshapeにユーザーが選択したタイプを記憶
		linewidth = width;		//linewidthにユーザーが選択したタイプを記憶

		switch(type){
			case 1:
				float dash1[] = {(float) (4 * width), (float) (2 * width)};
				imageGraphics.setStroke(new BasicStroke((float)width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f, dash1, 25.0f));
				break;
			case 2:
				float dash2[] = {(float) (2 * width), (float) (2 * width)};
				imageGraphics.setStroke(new BasicStroke((float)width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f, dash2, 25.0f));
				break;
			case 3:
				float dash3[] = {(float) (6 * width), (float) (2 * width), (float) (2 * width), (float) (2 * width)};
				imageGraphics.setStroke(new BasicStroke((float)width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f, dash3, 25.0f));
				break;
			case 4:
				float dash4[] = {(float) (12 * width), (float) (3 * width)};
				imageGraphics.setStroke(new BasicStroke((float)width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f, dash4, 25.0f));			
				break;
			default:
				imageGraphics.setStroke(new BasicStroke((float)width));
				break;
		}
	}
	
	/**
	 * 線の太さを変更するメソッドです。
	 * @param width 線の太さ
	 */
	public void gSetLineWidth(double width){
		gSetLineShape(lineshape,width);
	}
	
	public void gSetRotate(double theta){
		imageGraphics.rotate(theta);
	}
	
	public void gSetRotate(double theta, double x, double y){
		imageGraphics.rotate(theta, x, y);
	}
	
	/**
	 * 楕円（淵のみ）を描画するメソッド。
	 * 色は gSetLineColor のメソッドで指定したもの。
	 * 
	 * @param x
	 * 左上隅の x 座標です。
	 * @param y
	 * 左上隅の y 座標です。
	 * @param width
	 * 幅です。
	 * @param height
	 * 高さです。
	 * @see #gSetLineColor
	 * @see #gDrawPoint
	 * @see #gFillOval
	 */
	public void gDrawOval(double x, double y, double width, double height){
		double x2 = getXpoint(x);
		double y2 = getYpoint(y);
		
		imageGraphics.setColor(Line_Color);
		imageGraphics.draw(new Ellipse2D.Double(x2, y2, width, height));
		myrepaint();
	}
	
	/**
	 * 楕円（塗りつぶし）を描画するメソッド。
	 * 色は gSetFillColor のメソッドで指定したもの。
	 * 
	 * @param x
	 * 左上隅の x 座標です。
	 * @param y
	 * 左上隅の y 座標です。
	 * @param width
	 * 幅です。
	 * @param height
	 * 高さです。
	 * @see #gSetFillColor
	 * @see #gDrawPoint
	 * @see #gDrawOval
	 */
	public void gFillOval(double x, double y, double width, double height){
		double x2 = getXpoint(x);
		double y2 = getYpoint(y);
		
		imageGraphics.setColor(Fill_Color);
		imageGraphics.fill(new Ellipse2D.Double(x2, y2, width, height));
		gDrawOval(x, y, width, height);
	}
	
	/**
	 * 円を描画するメソッド。
	 * 色は gSetLineColor のメソッドで指定したもの。
	 * 
	 * @param x
	 * 円の中心の x 座標です。
	 * @param y
	 * 円の中心の y 座標です。
	 * @param r
	 * 半径です。
	 * 
	 * @see #gSetLineColor
	 */
	public void gDrawCircle(double x, double y, double r){
		double xM = getXmagnification();
		double yM = getYmagnification();
		
		if(originPoint) {
			gDrawOval( x - r, y + r, ( 2 * r ) * xM, ( 2 * r ) * yM);
		} else {
			gDrawOval( x - r, y - r, ( 2 * r ) * xM, ( 2 * r ) * yM);
		}
	}
	
	/**
	 * 内部を塗りつぶした円を描画するメソッド。
	 * 色は gSetFillColor のメソッドで指定したもの。
	 * 
	 * @param x
	 * 円の中心の x 座標です。
	 * @param y
	 * 円の中心の y 座標です。
	 * @param r
	 * 半径です。
	 * 
	 * @see #gSetFillColor
	 */
	public void gFillCircle(double x, double y, double r){
		double xM = getXmagnification();
		double yM = getYmagnification();
		
		if(originPoint) {
			gFillOval( x - r, y + r, ( 2 * r ) * xM, ( 2 * r ) * yM);
		} else {
			gFillOval( x - r, y - r, ( 2 * r ) * xM, ( 2 * r ) * yM);
		}
	}
	
	/**
	 * 矩形[四角]（淵のみ）を描画するメソッド。
	 * 色は gSetLineColor のメソッドで指定したもの。
	 * 
	 * @param x
	 * 左上隅の x 座標です。
	 * @param y
	 * 左上隅の y 座標です。
	 * @param width
	 * 幅です。
	 * @param height
	 * 高さです。
	 * @see #gSetLineColor
	 * @see #gFillBox
	 */
	public void gDrawBox(double x, double y, double width, double height){
		double x2 = getXpoint(x);
		double y2 = getYpoint(y);
		
		imageGraphics.setColor(Line_Color);
		imageGraphics.draw(new Rectangle2D.Double(x2, y2, width, height));
		myrepaint();
	}

	/**
	 * 矩形[四角]（塗りつぶし）を描画するメソッド。
	 * 色は gSetFillColor のメソッドで指定したもの。
	 * 
	 * @param x
	 * 左上隅の x 座標です。
	 * @param y
	 * 左上隅の y 座標です。
	 * @param width
	 * 幅です。
	 * @param height
	 * 高さです。
	 * @see #gSetFillColor
	 * @see #gDrawBox
	 */
	public void gFillBox(double x, double y, double width, double height){
		double x2 = getXpoint(x);
		double y2 = getYpoint(y);
		
		imageGraphics.setColor(Fill_Color);
		imageGraphics.fill(new Rectangle2D.Double(x2, y2, width, height));
		gDrawBox(x, y, width, height);
	}
	
	/**
	 * 円弧（淵のみ）を描画するメソッド。
	 * 色は gSetLineColor のメソッドで指定したもの。
	 * 
	 * @param x
	 * 弧の左上隅の x 座標です。
	 * @param y
	 * 弧の左上隅の y 座標です。
	 * @param width
	 * 楕円の幅です (角の大きさは考慮しない)。
	 * @param height
	 * 楕円の高さです (角の大きさは考慮しない)。
	 * @param start
	 * 弧の始角 (度単位) です。
	 * @param extent
	 * 弧の角の大きさ (度単位) です。
	 * @param type
	 * 弧の閉じ方の種類 (OPEN=0、CHORD=1、または PIE=2)
	 * @see #gSetLineColor
	 * @see #gFillArc
	 */
	public void gDrawArc(double x, double y, double width, double height, double start, double extent, int type){
		double x2 = getXpoint(x);
		double y2 = getYpoint(y);
		
		imageGraphics.setColor(Line_Color);
		imageGraphics.draw(new Arc2D.Double(x2, y2, width, height, start, extent, type));
		myrepaint();
	}
	
	/**
	 * 円弧（塗りつぶし）を描画するメソッド。
	 * 色は gSetFillColor のメソッドで指定したもの。
	 * 
	 * @param x
	 * 弧の左上隅の x 座標です。
	 * @param y
	 * 弧の左上隅の y 座標です。
	 * @param width
	 * 楕円の幅です (角の大きさは考慮しない)。
	 * @param height
	 * 楕円の高さです (角の大きさは考慮しない)。
	 * @param start
	 * 弧の始角 (度単位) です。
	 * @param extent
	 * 弧の角の大きさ (度単位) です。
	 * @param type
	 * 弧の閉じ方の種類 (OPEN=0、CHORD=1、または PIE=2)
	 * @see #gSetFillColor
	 * @see #gDrawArc
	 */
	public void gFillArc(double x, double y, double width, double height, double start, double extent, int type){
		double x2 = getXpoint(x);
		double y2 = getYpoint(y);
		
		imageGraphics.setColor(Fill_Color);
		imageGraphics.fill(new Arc2D.Double(x2, y2, width, height, start, extent, type));
		gDrawArc(x, y, width, height, start, extent, type);
	}
	
	/**
	 * x 座標と y 座標の配列で定義された閉じた多角形を描きます。
	 * 座標 (x, y) の各ペアは点を定義します。
	 * 色は gSetLineColor のメソッドで指定したもの。
	 * 
	 * @param xPoints
	 * x 座標の配列
	 * @param yPoints
	 * y 座標の配列
	 * @param nPoints
	 * 点の総数
	 * @see #gSetLineColor
	 * @see #gFillPolygon
	 */
	public void gDrawPolygon(int xPoints[], int yPoints[], int nPoints){
		int xPoints2[] = new int[nPoints];
		int yPoints2[] = new int[nPoints];
	//	System.arraycopy(xPoints, 0, xPoints2, 0, nPoints);
	//	System.arraycopy(yPoints, 0, yPoints2, 0, nPoints);
		for(int i = 0; i < nPoints; i++){
			xPoints2[i] = getXpoint(xPoints[i]);
			yPoints2[i] = getYpoint(yPoints[i]);
		}
		
		imageGraphics.setColor(Line_Color);
		imageGraphics.drawPolygon(xPoints2, yPoints2, nPoints);
		myrepaint();
	}
	
	/**
	 * x 座標と y 座標の配列で定義された閉じた多角形を塗りつぶします。
	 * 座標 (x, y) の各ペアは点を定義します。
	 * 色は gSetFillColor のメソッドで指定したもの。
	 * 
	 * @param xPoints
	 * x 座標の配列
	 * @param yPoints
	 * y 座標の配列
	 * @param nPoints
	 * 点の総数
	 * @see #gSetFillColor
	 * @see #gDrawPolygon
	 */
	public void gFillPolygon(int xPoints[], int yPoints[], int nPoints){
		int xPoints2[] = new int[nPoints];
		int yPoints2[] = new int[nPoints];
	//	System.arraycopy(xPoints, 0, xPoints2, 0, nPoints);
	//	System.arraycopy(yPoints, 0, yPoints2, 0, nPoints);
		for(int i = 0; i < nPoints; i++){
			xPoints2[i] = getXpoint(xPoints[i]);
			yPoints2[i] = getYpoint(yPoints[i]);
		}	
		
		imageGraphics.setColor(Fill_Color);
		imageGraphics.fillPolygon(xPoints2, yPoints2, nPoints);
		gDrawPolygon(xPoints, yPoints, nPoints);
	}

	/**
	 * x 座標と y 座標の配列で定義され連続的につながった直線を描きます。
	 * 座標 (x, y) の各ペアは点を定義します。
	 * 色は gSetLineColor のメソッドで指定したもの。
	 * 
	 * @param xPoints
	 * x 座標の配列
	 * @param yPoints
	 * y 座標の配列
	 * @param nPoints
	 * 点の総数
	 * @see #gSetLineColor
	 * @see #gDrawLine
	 */
	public void gDrawPolyline(int xPoints[], int yPoints[], int nPoints){
		int xPoints2[] = new int[nPoints];
		int yPoints2[] = new int[nPoints];
	//	System.arraycopy(xPoints, 0, xPoints2, 0, nPoints);
	//	System.arraycopy(yPoints, 0, yPoints2, 0, nPoints);
		for(int i = 0; i < nPoints; i++){
			xPoints2[i] = getXpoint(xPoints[i]);
			yPoints2[i] = getYpoint(yPoints[i]);
		}	
		
		imageGraphics.setColor(Line_Color);
		imageGraphics.drawPolyline(xPoints2, yPoints2, nPoints);
		myrepaint();
	} 	
	
	/**
	 * 両端に矢じりを付加可能な破線を描画します。
	 * 色は gSetLineColor のメソッドで指定したもの。
	 * 
	 * @param x1
	 * 始点 x の座標です。
	 * @param y1
	 * 始点 y の座標です。
	 * @param x2
	 * 終点 x の座標です。
	 * @param y2
	 * 終点 y の座標です。
	 * 
	 * @see #gSetLineColor
	 * @see #gDrawPolyline
	 */
	public void gDrawLine(double x1, double y1, double x2, double y2){
		double x1_2 = getXpoint(x1);
		double x2_2 = getXpoint(x2);
		double y1_2 = getYpoint(y1);
		double y2_2 = getYpoint(y2);

		imageGraphics.setColor(Line_Color);
		imageGraphics.draw(new Line2D.Double(x1_2, y1_2, x2_2, y2_2));
		myrepaint();
		
		if(arrowedge > 0){
			gDrawArrow(x1, y1, x2, y2);
		}
	}
	
	public void gDrawLine(double x1, double y1, double x2, double y2, boolean flag){
		if(flag){
			gDrawLine(x1, y1, x2, y2);
		} else {
			int tmp = arrowedge;
			arrowedge = 0;
			gDrawLine(x1, y1, x2, y2);
			arrowedge = tmp;
		}
	}
	
	public void gDrawArrow(double x1, double y1, double x2, double y2){
		double base = arrowsize;	//***矢じりサイズ(数値変更可)***
		double half = base * 7 / 10;
		//矢じり描画基点算出部
		double xl = x2 - x1;
		double yl = y2 - y1;
		double length = Math.sqrt( xl * xl + yl * yl);		//線の長さ	
		double xbase1 = x2 - ( base * xl / length );
		double ybase1 = y2 - ( base * yl / length );
		double xbase2 = x1 + ( base * xl / length );
		double ybase2 = y1 + ( base * yl / length );
		
		//System.out.println("length, 基点座標（x,y）"+length+','+xbase+','+ybase+")");
		
		//終点矢じり用、頂点算出部:(x2,y2)を除く3点
		int pointAx = (int) Math.round( xbase1 + ( half * yl / length ) );
		int pointAy = (int) Math.round( ybase1 - ( half * xl / length ) );
		int pointBx = (int) Math.round( xbase1 - ( half * yl / length ) );
		int pointBy = (int) Math.round( ybase1 + ( half * xl / length ) ); 
		int pointCx = (int) Math.round( x2 - ( half * xl / length ) );
		int pointCy = (int) Math.round( y2 - ( half * yl / length ) );
		
		//始点矢じり用、頂点算出部:(x1,y1)を除く3点
		int pointDx = (int) Math.round( xbase2 -( half * yl / length ) );
		int pointDy = (int) Math.round( ybase2 +( half * xl / length ) );
		int pointEx = (int) Math.round( xbase2 +( half * yl / length ) );
		int pointEy = (int) Math.round( ybase2 -( half * xl / length ) ); 
		int pointFx = (int) Math.round( x1 + ( half * xl / length ) );
		int pointFy = (int) Math.round( y1 + ( half * yl / length ) );
		
		//4点矢じり終点用配列
		int ex4_1[] = {(int) x2, pointAx, pointCx, pointBx};
		int ex4_2[] = {(int) y2, pointAy, pointCy, pointBy};
		
		//4点矢じり始点用配列
		int ex4_3[] = {(int) x1, pointDx, pointFx, pointEx};	
		int ex4_4[] = {(int) y1, pointDy, pointFy, pointEy};
		
		//3点矢じり終点用配列
		int ex3_1[] = {(int) x2, pointAx, pointBx};
		int ex3_2[] = {(int) y2, pointAy, pointBy};
		
		//3点矢じり始点用配列
		int ex3_3[] = {(int) x1, pointDx, pointEx};	
		int ex3_4[] = {(int) y1, pointDy, pointEy};
		
		imageGraphics.setStroke(IntVgOutputWindow.DEFAULT_STROKE);	//矢じり用ペン一時持ち替え
		
		Color Fill_Temp = Fill_Color;	//Polygon仕様に伴うfColor待避
		Fill_Color = Line_Color;	//矢じり内部をLineColorに
		
		switch(arrowtype){
			case 0:
				switch(arrowedge){
					case 0:
						break;
					case 1:		//始点のみ矢じり描画
						gFillPolygon(ex4_3, ex4_4, 4);
						break;
					case 2:		//終点のみ矢じり描画
						gFillPolygon(ex4_1, ex4_2, 4);
						break;
					case 3:		//両端に矢じり描画
						gFillPolygon(ex4_1, ex4_2, 4);
						gFillPolygon(ex4_3, ex4_4, 4);
						break;
					default:
						break;
				}
			
				break;
			case 1:	
				switch(arrowedge){
					case 0:
						break;
					case 1:		//始点のみ矢じり描画
						gFillPolygon(ex3_3, ex3_4, 3);
						break;
					case 2:		//終点のみ矢じり描画
						gFillPolygon(ex3_1, ex3_2, 3);
						break;
					case 3:		//両端に矢じり描画
						gFillPolygon(ex3_1, ex3_2, 3);
						gFillPolygon(ex3_3, ex3_4, 3);
						break;
					default:
						break;
				}
			
				break;
			case 2:
				gSetLineWidth(linewidth);

				switch(arrowedge){
					case 0:
						break;
					case 1:		//始点のみ矢じり描画
						gDrawLine( x1, y1, pointDx, pointDy, false);
						gDrawLine( x1, y1, pointEx, pointEy, false);
						break;
					case 2:		//終点のみ矢じり描画
						gDrawLine( x2, y2, pointAx, pointAy, false);
						gDrawLine( x2, y2, pointBx, pointBy, false);
						break;
					case 3:		//両端に矢じり描画
						gDrawLine( x1, y1, pointDx, pointDy, false);
						gDrawLine( x1, y1, pointEx, pointEy, false);
						gDrawLine( x2, y2, pointAx, pointAy, false);
						gDrawLine( x2, y2, pointBx, pointBy, false);
						break;
					default:
						break;
				}
			
				break;			
			default:
				break;
		}
		
		Fill_Color = Fill_Temp;

		gSetLineShape(lineshape, linewidth);	//ペン持ち直し
	}

	/**
	 * imageを描画するメソッド
	 * 
	 * @param fname
	 * ファイルパスです。
	 * @param x
	 * 左上隅の x 座標です。
	 * @param y
	 * 左上隅の y 座標です。
	 */
	public void gDrawImage(String fname, int x, int y){
		try{
			BufferedImage img = ImageIO.read(new File(fname));
			gDrawImage(fname, x, y, img.getHeight(), img.getWidth());
			
		}catch(Exception e){
			exception(e.getLocalizedMessage());
		}
	}
	
	/**
	 * imageを描画するメソッド
	 * 
	 * @param fname
	 * ファイルパスです。
	 * @param x
	 * 左上隅の x 座標です。
	 * @param y
	 * 左上隅の y 座標です。
	 * @param width
	 * 幅です。
	 * @param height
	 * 高さです。
	 */
	public void gDrawImage(String fname, int x, int y, int width, int height){
		int x2 = getXpoint(x);
		int y2 = getYpoint(y);
		
		try{
			BufferedImage img = ImageIO.read(new File(fname));
			imageGraphics.drawImage(img, x2, y2, width, height, this);
		}catch(Exception e){
			exception(e.getLocalizedMessage());
		}

		myrepaint();
	}
	
	public void setDefaultCloseOperation(int operation){
		frame.setDefaultCloseOperation(operation);
	}
	
	public void setResizable(boolean resizable){
		frame.setResizable(resizable);
	}
	
	public void setRepaintFlag(boolean repaintFlag){
		this.repaintFlag = repaintFlag;
	}
	
	public void enableOriginChange(){
		setOrigin(true);
	}
	
	public void disableOriginChange(){
		setOrigin(false);
	}
	
	public void setOrigin(boolean flag){
		DefaultOriginPoint = flag;
	}
	
	public int getXpoint(int x){
		return new Double(getXpoint((double) x)).intValue();
	}
	
	public double getXpoint(double x){
		return (xPoint + x) * getXmagnification();
	}
	
	public double getXmagnification(){
		return windowsizeX / xRange;
	}
	
	public int getYpoint(int y){
		return new Double(getYpoint((double) y)).intValue();
	}
	
	public double getYpoint(double y){
		if(originPoint) {
			return ((y - yPoint) * getYmagnification() ) * -1;
		} else {
			return y * getYmagnification();
		}
	}
	
	public double getYmagnification(){
		return windowsizeY / yRange;
	}

	public void setDefaultParameter(){
		Fill_Color = IntVgOutputWindow.DEFAULT_DRAW_COLOR;
		Line_Color = IntVgOutputWindow.DEFAULT_DRAW_COLOR;
		Text_Color = IntVgOutputWindow.DEFAULT_DRAW_COLOR;
		
		lineshape	= 0;
		linewidth	= 1.0f;
		dottype		= 0;
		arrowtype	= 0;
		arrowedge	= 0;
		arrowsize	= 10;
		fonttype	= Font.PLAIN;
		fontsize	= 10;
		font		= "Default";
	}
	
	public void myrepaint(){
		if(repaintFlag){
			repaint();
		}
	}
	
	public synchronized void mysleep(long sleep_msec) {
		try{
			wait(sleep_msec);
		}catch(InterruptedException e){ }
	}
	
	public void addKeyListener(KeyListener l){
		frame.addKeyListener(l);
	}
	
	public void addMouseInputListener(MouseInputListener l){
		frame.addMouseListener(l);
		frame.addMouseMotionListener(l);
	}
	
	/**
	 * @param msg
	 */
	private void exception(String msg){
		String str1 = "";
		if(gui == null){
			str1 = "### エラーです\n";
		} else{
			gui.Flags.RunFlag = false;
			str1 = "### " + gui.run_point.getLineCount() + "行目でエラーです\n";
		}
		String str2 = "### " + msg + "\n";
		
		printString(str1);
		printString(str2);
		
		throw new ThreadRunStop();
	}

	private void printString(String str){
		if(consoleAppend == null){
			System.out.print(str);
			System.out.flush();
		} else {
			consoleAppend.appendAll(str);
		}
	}
}
