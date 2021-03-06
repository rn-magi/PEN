import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Vector;

/**
 * IntVParserクラスによって生成した構文木の
 * 各ノードの処理を記述したクラス。
 * xDNCL言語の実行はこのクラスがすべて行っている。
 * 
 * @author Ryota Nakamura
 */
public class IntVConvertArduino implements IntVParserVisitor{
	private int declaration		= 0;
	private boolean flag		= false;
	private boolean NodeDump	= false;
	
	private boolean varFlag		= false;
	private boolean varError	= false;
	
	private int arrayOrigin	= 0;
	private int arrayField	= 0;
	
	private int indentLevel = 1;
	private boolean indentSkip = false;
	
	private PenProperties penPro;
	
	private ArrayList<String> arduinoCode = new ArrayList<String>();
	
	private String openPort = "";
	
	private final String lineSepa = System.getProperty("line.separator");
	private final String fileSepa = System.getProperty("file.separator");
	
	private String inoTempDirPath;
	private String inoTempFilePath;
	private String inoTempFileName;
	
	private boolean uploadFlag = false;
	
	public IntVConvertArduino(PenProperties penPro){
		this.penPro = penPro;

		long unixTime = System.currentTimeMillis();
		
		inoTempDirPath = penPro.getProperty(penPro.PEN_SYSTEM_DIR)
				+ fileSepa + "tmpArduino" + fileSepa + "pen" + unixTime + fileSepa;
		inoTempFileName = "pen" + unixTime + ".ino";
		inoTempFilePath = inoTempDirPath + inoTempFileName;
	}

	/**
	 * 構文木をダンプするためのフラグを true にするメソッド
	 */
	public void NodeDump(){
		NodeDump = true;
	}
	
	/**
	 * @param type
	 * 	0 : 宣言ありモード
	 * 	1 : 宣言なしモード + 警告あり
	 * 	2 : 宣言なしモード
	 */
	public void varType(int type){
		switch(type){
			case 0:
				varFlag	= false;
				varError= false;
				break;
			case 1:
				varFlag	= true;
				varError= true;
				break;
			case 2:
			case 3:
				varFlag	= true;
				varError= false;
				break;
		}
	}

	/**
	 * @param arrayType
	 * 	0 : 0オリジン～添字
	 * 	1 : 0オリジン～添字 - 1
	 * 	2 : 1オリジン～添字
	 */
	public void arrayOrigin(int arrayType){
		switch(arrayType){
			case 0:
				arrayOrigin = 0;
				arrayField  = 0;
				break;
			case 1:
				arrayOrigin = 0;
				arrayField  = 1;
				break;
			case 2:
				arrayOrigin = 1;
				arrayField  = 0;
				break;
		}
	}
	
	/**
	 * 実行時エラーによって呼び出されるメソッド
	 */
	public Object visit(SimpleNode node, Object data) {
		throw new Error(); // It better not come here.
	}
	
	/**
	 * 構文木のルートノード
	 */
	public Object visit(ASTIntVUnit node, Object data) {
		int i, k = node.jjtGetNumChildren();
		
		// メインプログラムの変換
		outPutCodeln("void setup() {");
		for (i = 0; i < k; i++) {
			if(!(node.jjtGetChild(i) instanceof ASTFunction)){
				node.jjtGetChild(i).jjtAccept(this, data);
			}
		}
		outPutCodeln("}");
		outPutCodeln("void loop() { }");

		// 手続き・関数呼び出しの変換
		for (i = 0; i < k; i++) {
			if( node.jjtGetChild(i) instanceof ASTFunction){
				indentLevel = 1;
				node.jjtGetChild(i).jjtAccept(this, data);
			}
		}
		
		writeCode();
		
		if(NodeDump){
			printDebug(node);
		}
		
		if(uploadFlag){
			commandLineArduino();
		}
		
		//deleteTempFiles(inoTempDirPath);
		
		return null;
	}

	// ファイルの確認
	private static boolean checkBeforeWritefile(File file) {
		if (file.exists()) {
			if (file.isFile() && file.canWrite()) {
				return true;
			}
		}

		return false;
	}
	/**
	 * 実行エラー
	 */
	public Object visit(ASTErrorOccur node, Object data) {
		throw new Error(); // It better not come here.
	}

	/**
	 * 手続き・関数呼び出しのノードを symTable に格納
	 */
	public Object visit(ASTFunction node, Object data) {
		switch(node.decl) {
			case PROCEDURAL:
				outPutCode("void ");
				break;
			case INT:
				outPutCode("int ");
				break;
			case LONG:
				outPutCode("long ");
				break;
			case DOUBLE:
				outPutCode("double ");
				break;
			case STRING:
				break;
			case BOOLEAN:
				outPutCode("boolean ");
				break;
		}
		
		outPutCode(node.varName);
		outPutCode("(");
		
		int k = node.jjtGetNumChildren();
		
		if(k != 0){
			if(node.jjtGetChild(0) instanceof ASTFunctionVar){
				node.jjtGetChild(0).jjtAccept(this, data);
			}
		}
		outPutCodeln(") {");
		
		if(k != 0){
			if(k == 2){
				node.jjtGetChild(1).jjtAccept(this, data);
			} else if(node.jjtGetChild(0) instanceof ASTBlock){
				node.jjtGetChild(0).jjtAccept(this, data);
			}
		}
		
		outPutCodeln("}");
		return null;
	}

	/**
	 * 仮引数の処理を行う
	 */
	public Object visit(ASTFunctionVar node, Object data) {
		switch(node.decl) {
			case INT:
				outPutCode("int ");
				break;
			case LONG:
				outPutCode("long ");
				break;
			case DOUBLE:
				outPutCode("double ");
				break;
			case STRING:
				break;
			case BOOLEAN:
				outPutCode("boolean ");
				break;
		}
		node.jjtGetChild(0).jjtAccept(this, data);

		// 子ノードの数を取得
		int k = node.jjtGetNumChildren();
		if(k == 2){
			outPutCode(", ");
			node.jjtGetChild(1).jjtAccept(this, data);
		}
		
		return null;
	}

	/**
	 * 変数の宣言
	 */
	public Object visit(ASTVarDecl node, Object data) {
		outPutIndent();
		switch(node.decl) {
			case INT:
				outPutCode("int ");
				break;
			case LONG:
				outPutCode("long ");
				break;
			case DOUBLE:
				outPutCode("double ");
				break;
			case STRING:
				break;
			case BOOLEAN:
				outPutCode("boolean ");
				break;
		}

		// 子ノードの数を取得
		int k = node.jjtGetNumChildren();
		for (int i = 0; i < k; i++) {
			// 子ノードの数が１以上で;を表示
			if (i >= 1) {
				outPutCode(", ");
			}
			
			// jjtGetChildは子ノードそのものを取得する。 jjtAcceptはノードの実行。
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		outPutCodeln(";");
		return null;
	}

	/**
	 * 変数の領域確保
	 */
	public Object visit(ASTDecl node, Object data) {
		outPutCode(node.varName);
		
		// 配列の場合
		int k = node.jjtGetNumChildren();
		for (int i = 0; i < k; i++) {
			if (node.jjtGetChild(i) instanceof ASTArray) {
				outPutCode("[");
				node.jjtGetChild(i).jjtAccept(this, data);
				outPutCode("]");
			}
		}
		return null;
	}
	
	/**
	 * 変数を配列として確保する
	 */
	public Object visit(ASTArray node, Object data) {
		// TODO　配列処理
		int k = node.jjtGetNumChildren();
		for (int i = 0; i < k; i++) {
			if (node.jjtGetChild(i) instanceof ASTArray) {
				outPutCode(",");
				node.jjtGetChild(i).jjtAccept(this, data);
			} else {
				node.jjtGetChild(0).jjtAccept(this, data);
			}
		}
		return null;
	}
	
	/**
	 * 複数の計算式
	 */
	public Object visit(ASTAssignStats node, Object data) {
		// 子ノードの数を取得
		int k = node.jjtGetNumChildren();
		
		for (int i = 0; i < k; i++) {
			// jjtGetChildは子ノードそのものを取得する。 jjtAcceptはノードの実行。
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return null;
	}
	
	/**
	 * 変数 ← 計算式
	 * 計算式のノードを処理し、結果を変数に格納する
	 */
	public Object visit(ASTAssignStat node, Object data) {
		outPutIndent();
		
		// 左辺の子ノードを呼び出す
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCode(" = ");
		// 右辺の子ノードを呼び出す
		// 文字なら”をつける
		if (node.jjtGetChild(1) instanceof ASTStrlit) {
			outPutCode("\"");
			node.jjtGetChild(1).jjtAccept(this, data);
			outPutCode("\"");
		} else {
			node.jjtGetChild(1).jjtAccept(this, data);
		}
		outPutCodeln(";");
		return null;
	}
	
	/**
	 * IF文
	 */
	public Object visit(ASTIfStat node, Object data) {
		// TODO IF文の処理
		outPutIndent();
		indentSkip = false;
		
		if (node.jjtGetNumChildren() >= 2) {
			outPutCode("if ");
			node.jjtGetChild(0).jjtAccept(this, data);
			outPutCodeln(" {");
			indentLevel++;
			node.jjtGetChild(1).jjtAccept(this, data);
			indentLevel--;
			outPutIndent();
			// 子ノードが3以上は改行なしで表示
			if (node.jjtGetNumChildren() >= 3) {
				outPutCode("} ");
			} else {
				outPutCodeln("}");
			}
		}
		
		if (node.jjtGetNumChildren() == 3) {
			// System.out.println(node.jjtGetChild(2));
			outPutCode("else");
			// 子ノードにASTIfStatがきたらASTIfStatの子ノードを表示
			if (node.jjtGetChild(2) instanceof ASTIfStat) {
				indentSkip = true;
				outPutCode(" ");
				node.jjtGetChild(2).jjtAccept(this, data);
			} else if (node.jjtGetNumChildren() == 3) {
				outPutCodeln(" {");
				indentLevel++;
				node.jjtGetChild(2).jjtAccept(this, data);
				indentLevel--;
				outPutIndent();
				outPutCodeln("}");
			}
		}

		return null;
	}

	/**
	 * Do-While文
	 */
	public Object visit(ASTDoWhileStat node, Object data) {
		return null;
	}

	/**
	 * RepeatUntil文
	 */
	public Object visit(ASTRepeatUntil node, Object data) {
		return null;
	}

	/**
	 * For文の増減値を処理
	 */
	public Object visit(ASTForStatAdd node, Object data) {
		// 足し引きの表示
		if (node.op < 0) {
			outPutCode(" - ");
		} else {
			outPutCode(" + ");
		}
		node.jjtGetChild(0).jjtAccept(this, data);
		return null;
	}

	public Object visit(ASTCase node, Object data) {
		return null;
	}

	public Object visit(ASTLabel node, Object data) {
		return null;
	}

	public Object visit(ASTWhileSwitchFor node, Object data) {
		Object r = null;
		
		switch (node.p) {
			case WHILE:
				r = runWhileStat(node, data);
				break;
			case SWITCH:
				r = runSwitch(node, data);
				break;
			case FOR:
				r = runFor(node, data);
				break;
			default:
				return null;
		}
		
		return r;
	}

	/**
	 * while文の実体
	 */
	public Object runWhileStat(SimpleNode node, Object data) {
		outPutIndent();
		outPutCode("while ");
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCodeln(" {");
		indentLevel++;
		node.jjtGetChild(1).jjtAccept(this, data);
		indentLevel--;
		outPutIndent();
		outPutCodeln("}");
		return null;
	}

	/**
	 * switch文の実体
	 */
	public Object runSwitch(SimpleNode node, Object data) {
		return null;
	}

	/**
	 * for文の実体
	 */
	public Object runFor(SimpleNode node, Object data) {
		// op が 1 なら増やしながら
		// op が -1 なら減らしながら
		int op = ((ASTForStatAdd)node.jjtGetChild(3)).op;

		outPutIndent();
		
		outPutCode("for (");
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCode(" = ");
		node.jjtGetChild(1).jjtAccept(this, data);
		outPutCode("; ");
		
		node.jjtGetChild(0).jjtAccept(this, data);
		if(op == 1){
			outPutCode(" <= ");
		}else{
			outPutCode(" >= ");
		}
		node.jjtGetChild(2).jjtAccept(this, data);
		outPutCode("; ");
		
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCode(" = ");
		node.jjtGetChild(0).jjtAccept(this, data);
		node.jjtGetChild(3).jjtAccept(this, data);
		outPutCodeln(") {");
		
		indentLevel++;
		node.jjtGetChild(4).jjtAccept(this, data);
		indentLevel--;
		
		outPutIndent();
		outPutCodeln("}");
		return null;
	}

	/**
	 * 無限ループ
	 */
	public Object visit(ASTInfiniteLoop node, Object data) {
		outPutIndent();
		outPutCodeln("while(true) {");
		
		indentLevel++;
		node.jjtGetChild(0).jjtAccept(this, data);
		indentLevel--;
		
		outPutIndent();
		outPutCodeln("}");
		
		return null;
	}
	
	/**
	 * ないしょ
	 */
	public Object visit(ASTGetStat node, Object data) {
		return null;
	}

	/**
	 * 出力文
	 */
	public Object visit(ASTPutStat node, Object data) {
		outPutIndent();
		outPutCode("// ");
		int k = node.jjtGetNumChildren();
		
		// 子ノードが2以上で文字列と変数を表示
		if (node.jjtGetNumChildren() >= 2) {
			outPutCode("printf(\"");
			for (int i = 0; i < k; i++) {
				if (node.jjtGetChild(i) instanceof ASTStrlit) {
					node.jjtGetChild(i).jjtAccept(this, data);
				} else if (node.jjtGetChild(i) instanceof ASTIdent) {
					outPutCode("　%d ");
				}
			}
			outPutCode("\"");
			
			// 子ノードが変数でその数だけ表示
			for (int j = 0; j < k; j++) {
				if (j >= 0) {
					if (node.jjtGetChild(j) instanceof ASTIdent) {
						outPutCode(", ");
						node.jjtGetChild(j).jjtAccept(this, data);
					}
				}
			}
			outPutCode(")");
		}
		
		// 文字列の表示
		else if (node.jjtGetChild(0) instanceof ASTStrlit) {
			outPutCode("printf(\"");
			node.jjtGetChild(0).jjtAccept(this, data);
			outPutCode("\")");
		}
		
		// 変数の表示
		else if (node.jjtGetChild(0) instanceof ASTIdent) {
			outPutCode("printf(\"　%d \",");
			node.jjtGetChild(0).jjtAccept(this, data);
			outPutCode(")");
		}
		outPutCodeln(";");
		return null;
	}

	/**
	 * 制御構造内のノードを処理
	 */
	public Object visit(ASTBlock node, Object data) {
		int i, k = node.jjtGetNumChildren();
		for (i = 0; i < k; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return null;
	}

	/**
	 * ループ処理を抜ける break文 の処理
	 */
	public Object visit(ASTBreak node, Object data) {
		outPutIndent();
		outPutCodeln("break;");
		return null;
	}
	
	/**
	 * 手続きを抜ける return文 の処理
	 */
	public  Object visit(ASTReturnProcedural node, Object data) {
		outPutIndent();
		outPutCodeln("return;");
		return null;
	}
	
	/**
	 * 比較演算子 "<" の処理
	 */
	public Object visit(ASTLSNode node, Object data) {
		outPutCode("(");
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCode(" < ");
		node.jjtGetChild(1).jjtAccept(this, data);
		outPutCode(")");
		return null;
	}

	/**
	 * 比較演算子 ">" の処理
	 */
	public Object visit(ASTGTNode node, Object data) {
		outPutCode("(");
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCode(" > ");
		node.jjtGetChild(1).jjtAccept(this, data);
		outPutCode(")");
		return null;
	}

	/**
	 * 比較演算子 "<=" の処理
	 */
	public Object visit(ASTLENode node, Object data) {
		outPutCode("(");
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCode(" <= ");
		node.jjtGetChild(1).jjtAccept(this, data);
		outPutCode(")");
		return null;
	}

	/**
	 * 比較演算子 ">=" の処理
	 */
	public Object visit(ASTGENode node, Object data) {
		outPutCode("(");
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCode(" >= ");
		node.jjtGetChild(1).jjtAccept(this, data);
		outPutCode(")");
		return null;
	}

	/**
	 * 比較演算子 "==" の処理
	 */
	public Object visit(ASTEQNode node, Object data) {
		outPutCode("(");
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCode(" == ");
		node.jjtGetChild(1).jjtAccept(this, data);
		outPutCode(")");
		return null;
	}

	/**
	 * 比較演算子 "!=" の処理
	 */
	public Object visit(ASTNTNode node, Object data) {
		outPutCode("(");
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCode(" != ");
		node.jjtGetChild(1).jjtAccept(this, data);
		outPutCode(")");
		return null;
	}

	/**
	 * 論理演算子 "AND" の処理
	 */
	public Object visit(ASTANDNode node, Object data) {
		return null;
	}

	/**
	 * 論理演算子 "OR" の処理
	 */
	public Object visit(ASTORNode node, Object data) {
		return null;
	}

	/**
	 * 論理演算子 "NOT" の処理
	 */
	public Object visit(ASTNOTNode node, Object data) {
		return null;
	}

	/**
	 * 演算子 "+" の処理
	 */
	public Object visit(ASTAddNode node, Object data) {
		arithmeticOutPut(node, data, "+");
		return null;
	}

	/**
	 * 演算子 "-" の処理
	 */
	public Object visit(ASTSubNode node, Object data) {
		arithmeticOutPut(node, data, "-");
		return null;
	}

	/**
	 * 演算子 "*" の処理
	 */
	public Object visit(ASTMulNode node, Object data) {
		arithmeticOutPut(node, data, "*");
		return null;
	}

	/**
	 * 演算子 "/" の処理
	 */
	public Object visit(ASTDivNode node, Object data) {
		arithmeticOutPut(node, data, "/");
		return null;
	}

	/**
	 * 演算子 "%" の処理
	 */
	public Object visit(ASTSurNode node, Object data) {
		arithmeticOutPut(node, data, "%");
		return null;
	}
	
	public Object arithmeticOutPut(SimpleNode node, Object data, String operator){
		boolean literalCheck = node.jjtGetChild(0) instanceof ASTLiteral
								|| node.jjtGetChild(1) instanceof ASTLiteral;
		
		for(int i = 0; i < 2; i++){
			if(i == 1){
				outPutCode(" " + operator + " ");
			}

			boolean nodeCheck;
			if(operator.equals("+") || operator.equals("-")){
				nodeCheck = false;
			} else {
				nodeCheck = node.jjtGetChild(i) instanceof ASTAddNode
									|| node.jjtGetChild(i) instanceof ASTSubNode;
			}
			
			if(!literalCheck || nodeCheck){
				outPutCode("(");
				node.jjtGetChild(i).jjtAccept(this, data);
				outPutCode(")");
			} else {
				node.jjtGetChild(i).jjtAccept(this, data);
			}
		}
		return null;
	}

	/**
	 * "-" 変数の処理
	 */
	public Object visit(ASTMinNode node, Object data) {
		// TODO マイナスの表示
		outPutCode("-");
		node.jjtGetChild(0).jjtAccept(this, data);
		return null;
	}

	/**
	 * 変数名から値を返す
	 */
	public Object visit(ASTIdent node, Object data) {
		if (node.flag) {
			outPutCode(node.varName);
			// 変数名が配列の場合の処理
			int k = node.jjtGetNumChildren();
			for (int i = 0; i < k; i++) {
				if (node.jjtGetChild(i) instanceof ASTArrayNum) {
					outPutCode("[");
					node.jjtGetChild(i).jjtAccept(this, data);
					outPutCode("]");
				}
			}
		} else {
			// 関数呼び出しの場合の処理
			if(checkArduinoFunction(node.varName)){
				Function(node, data, node.varName);
			}
		}
		return null;
	}

	/**
	 * 変数（配列）から値を返す
	 */
	public Object visit(ASTArrayNum node, Object data) {
		// TODO　配列処理
		int k = node.jjtGetNumChildren();
		for (int i = 0; i < k; i++) {
			if (node.jjtGetChild(i) instanceof ASTArrayNum) {
				outPutCode(",");
				node.jjtGetChild(i).jjtAccept(this, data);
			} else {
				node.jjtGetChild(0).jjtAccept(this, data);
			}
		}
		return null;
	}

	/**
	 * 手続き・関数呼び出し
	 */
	public Object visit(ASTFunctionCall node, Object data) {
		// TODO PENで定義されていない独自関数などの処理
		if(node.varName.equals("openPort")){
			if(node.jjtGetChild(0) instanceof ASTStrlit){
				openPort = ((ASTStrlit) node.jjtGetChild(0)).litString;
			}
		} else {
			if(checkArduinoFunction(node.varName)){
				outPutIndent();
				Function(node, data, node.varName);
				outPutCodeln(";");
			}
		}
		return null;
	}

	/**
	 * 関数呼び出しの戻り値
	 */
	public Object visit(ASTReturn node, Object data) {
		outPutIndent();
		outPutCode("return ");
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCodeln(";");
		
		return null;
	}

	/**
	 * input() : 入力文
	 */
	public Object visit(ASTGet node, Object data) {
		return null;
	}

	/**
	 * random() : 0～引数の間のランダムな値を返す
	 */
	public Object visit(ASTRandom node, Object data) {
		return null;
	}

	/**
	 * sin() : 引数の正弦を返す
	 */
	public Object visit(ASTSine node, Object data) {
		return null;
	}

	/**
	 * cos() : 引数の余弦を返す
	 */
	public Object visit(ASTCosine node, Object data) {
		return null;
	}

	/**
	 * tan() : 引数の正接を返す
	 */
	public Object visit(ASTTangent node, Object data) {
		return null;
	}

	/**
	 * sqrt() : 引数の平方根の値を返す
	 */
	public Object visit(ASTSqrt node, Object data) {
		return null;
	}

	/**
	 * log() : 引数の自然対数値 (底は e) を返す
	 */
	public Object visit(ASTLog node, Object data) {
		return null;
	}

	/**
	 * abs() : 引数の絶対値を返す
	 */
	public Object visit(ASTAbs node, Object data) {
		return null;
	}

	/**
	 * ceil() : 引数の小数点以下 切り上げ
	 */
	public Object visit(ASTCeil node, Object data) {
		return null;
	}

	/**
	 * floor() : 引数の小数点以下 切り捨て
	 */
	public Object visit(ASTFloor node, Object data) {
		return null;
	}

	/**
	 * round() : 引数の小数点以下 四捨五入
	 */
	public Object visit(ASTRound node, Object data) {
		return null;
	}

	/**
	 * int() : 引数の型を「整数」に変換
	 */
	public Object visit(ASTInt node, Object data) {
		return null;
	}

	/**
	 * long() : 引数の型を「Long」に変換
	 */
	public Object visit(ASTLong node, Object data) {
		return null;
	}

	/**
	 * length()
	 */
	public Object visit(ASTLength node, Object data) {
		return null;
	}
	
	
	/**
	 * append()
	 */
	public Object visit(ASTAppend node, Object data) {
		return null;
	}

	/**
	 * substring()
	 */
	public Object visit(ASTSubstring node, Object data) {
		return null;
	}

	/**
	 * insert()
	 */
	public Object visit(ASTInsert node, Object data) {
		return null;
	}

	/**
	 * replace()
	 */
	public Object visit(ASTReplace node, Object data) {
		return null;
	}

	/**
	 * extract()
	 */
	public Object visit(ASTExtract node, Object data) {
		return null;
	}
	
	/**
	 * str2int()
	 */
	public Object visit(ASTStr2Int node, Object data) {
		return null;
	}

	/**
	 * int2str()
	 */
	public Object visit(ASTInt2Str node, Object data) {
		return null;
	}

	/**
	 * compare()
	 */
	public Object visit(ASTCompare node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgOpenWindow node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgOpenGraphWindow node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgCloseWindow node, Object data) {
		return null;
	}
	
	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgClearWindow node, Object data) {
		return null;
	}
	
	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSaveWindow node, Object data) {
		return null;
	}
	
	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetOrigin node, Object data) {
		return null;
	}
	
	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetMap node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetFillColor node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetLineColor node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetTextColor node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetFont node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetFontType node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetFontSize node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetDotShape node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetArrowDir node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetArrowType node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetLineShape node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetLineWidth node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawPoint node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawLine node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawText node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawCircle node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgFillCircle node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawOval node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgFillOval node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawBox node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgFillBox node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawArc node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgFillArc node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawPolygon node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgFillPolygon node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawPolyline node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawImage node, Object data) {
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetRepaintFlag node, Object data) {
		return null;
	}
	
	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgRepaint node, Object data) {
		return null;
	}
	
	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_openr node, Object data) {
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_openw node, Object data) {
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_opena node, Object data) {
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_close node, Object data) {
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_getstr node, Object data) {
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_getline node, Object data) {
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_putstr node, Object data) {
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_putline node, Object data) {
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_flush node, Object data) {
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_isfile node, Object data) {
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_rename node, Object data) {
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_remove node, Object data) {
		return null;
	}
	
	/**
	 * sleep() : プログラムの一時停止
	 */
	public Object visit(ASTSleep node, Object data) {
		outPutIndent();
		outPutCode("delay(");
		node.jjtGetChild(0).jjtAccept(this, data);
		outPutCodeln(");");
		return null;
	}

	/**
	 * 整数を表すノードで整数を返す
	 */
	public Object visit(ASTLiteral node, Object data) {
		outPutCode(node.litValue.toString());
		return null;
	}

	/**
	 * 実数を表すノードで実数を返す
	 */
	public Object visit(ASTFloatLiteral node, Object data) {
		outPutCode(node.litValue.toString());
		return null;
	}

	/**
	 * 「真」を返す
	 */
	public Object visit(ASTTrue node, Object data) {
		return null;
	}
	
	/**
	 * 「偽」を返す
	 */
	public Object visit(ASTFalse node, Object data) {
		return null;
	}
	
	/**
	 * 文字列を表すノードで文字列を返す
	 */
	public Object visit(ASTStrlit node, Object data) {
		//TODO　文字列
		outPutCode(node.litString);
		return null;
	}

	/**
	 * EOF
	 */
	public Object visit(ASTEXTRA_STR node, Object data) {
		return null;
	}
	
	/**
	 * 手続き呼び出し、関数呼び出しを実装しているメソッド
	 * 
	 * @param node
	 * 構文木
	 * 
	 * @param data
	 * Objectデータ
	 * 
	 * @param varName
	 * 手続き名、関数名
	 * 
	 * @return
	 * 関数呼び出しの戻り値
	 */
	public Object Function(SimpleNode node, Object data, String varName){
		outPutCode(varName);
		outPutCode("(");
		int k = node.jjtGetNumChildren();
		for (int i = 0; i < k; i++) {
			// 子ノードの数が１以上で;を表示
			if (i >= 1) {
				outPutCode(", ");
			}
			/*
			 * jjtGetChildは子ノードそのものを取得する。
			 * jjtAcceptはノードの実行。
			 */
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		outPutCode(")");
		return null;
	}
	
	/**
	 * callByReferenceを使用したときに変数表示画面を更新するメソッド
	 * 
	 * @param vec
	 * 関数呼び出し内で使用した配列
	 * 
	 * @param var_name
	 * 関数呼び出し時に渡した変数名
	 */
	public void callByReference(Vector vec, String var_name){
	}

	/**
	 * 実行している行のポインタの移動や、
	 * 一行実行、実行停止などを判断するメソッド
	 * 
	 * @param num
	 * 実行している行
	 * 
	 * @param mysleep_flag
	 * 実行速度調節バーの時間分、停止するかしないかのフラグ
	 */
	public synchronized void run_flag(int num, boolean mysleep_flag) {
	}

	/**
	 * プログラムの実行を一時停止するためのメソッド
	 * 
	 * @param sleep_msec
	 * 実行を停止させてい時間 (ミリ秒)
	 */
	public synchronized void mysleep(long sleep_msec) {
		try {
			wait(sleep_msec);
		} catch (InterruptedException e){ }
	}

	/**
	 * 待機中のスレッドを再開するためのメソッド
	 */
	public synchronized void mysleep_stop() {
		notifyAll();
	}

	/**
	 * 実行行ポインタの移動
	 * 
	 * @param run
	 * 実行している行番号
	 */
	public void run_num(int run) {
	}

	/**
	 * コンソール画面からの入力を処理するためのメソッド
	 * 
	 * @return
	 * コンソール画面に入力された文字列
	 */
	public Object input_wait(){
		return null;
	}
	
	/**
	 * 変数宣言なしモードで起動時、変数領域を確保するためのメソッド
	 * 
	 * @param varName
	 * 確保する変数名
	 * 
	 * @param in
	 * 代入する値
	 */
	public void varSecure(String varName, Object in){
	}
	
	/**
	 * 実行停止ボタンを押すことによって呼び出されるメソッド
	 */
	public void runBreak(boolean flag){
	}
	
	public void outPutCode(String code) {
		arduinoCode.add(code);
	}

	public void outPutCodeln(String code) {
		outPutCode(code + "\n");
	}
	
	public void outPutIndent(){
		if(!indentSkip) {
			for(int i = 0; i < indentLevel; i++) {
				outPutCode("\t");
			}
		}
	}
	
	public void writeCode(){
		try {
			new File(inoTempDirPath).mkdirs();
			FileOutputStream file = new FileOutputStream(inoTempFilePath);
			OutputStreamWriter bw = new OutputStreamWriter(file, "UTF-8");
			for (int j = 0; j < arduinoCode.size(); j++) {
				bw.write(arduinoCode.get(j));
				
				// 改行の追加
				if (arduinoCode.get(j).equals(" {")
						|| arduinoCode.get(j).equals(";")
						|| arduinoCode.get(j).equals("}")
						) {
					bw.write(lineSepa);
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void commandLineArduino(){
		try {
			String osName = System.getProperty("os.name");
			String commandLine = "";
			Runtime r = Runtime.getRuntime();
			Process p;
			
			if(osName.indexOf("Windows")>=0){
				commandLine = penPro.getProperty(penPro.Arduino_EXEC_PATH);
			} else if(osName.indexOf("Linux")>=0){
			} else if(osName.indexOf("Mac")>=0){
				commandLine = "/usr/bin/open -W "
						+ penPro.getProperty(penPro.Arduino_EXEC_PATH)
						+ " --args";
			} else {
			}
			
			if(!openPort.equals("")){
				commandLine = commandLine + " --port " + openPort;
			}
			commandLine = commandLine + " --upload " + inoTempFilePath;
			p = r.exec(commandLine);
			int ret = p.waitFor();
			//System.out.println("command: " + commandLine);
			//System.out.println("Retun: " + ret);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	public void printDebug(SimpleNode node){
		System.out.println("-*-*-*- node dump -*-*-*-");
		node.dump("");
		System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-");
		System.out.println();
		System.out.println("-*-* Arduino Program *-*-");
		for (int j = 0; j < arduinoCode.size(); j++) {
			System.out.print(arduinoCode.get(j));
		}
		System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-");
		System.out.println("Port: " + openPort);
	}
	
	public boolean checkArduinoFunction(String varName){
		if(varName.equals("closePort")){
			return false;
		} else if(varName.equals("pinMode")
				|| varName.equals("digitalWrite")
				|| varName.equals("digitalRead")
				|| varName.equals("analogWrite")
				|| varName.equals("analogRead")
				|| varName.equals("sleep")
				|| varName.equals("delay")){
			return true;
		}
		//return false;
		return true;
	}
	
	public void deleteTempFiles(String filePath){
		File temp = new File(filePath);
		
		if(temp.isFile()){
			temp.delete();
			//System.out.println(temp.getPath());
		} else if(temp.isDirectory()){
			File[] files=temp.listFiles();
			for(int i=0; i<files.length; i++){
				deleteTempFiles( files[i].getPath() );
			}
			temp.delete();
			//System.out.println(temp.getPath());
		}
	}
	
	public void setUploadFlag(boolean uploadFlag){
		this.uploadFlag = uploadFlag;
	}
}
