import java.awt.Color;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

/**
 * IntVParserクラスによって生成した構文木の
 * 各ノードの処理を記述したクラス。
 * xDNCL言語の実行はこのクラスがすべて行っている。
 * 
 * @author Ryota Nakamura
 */
public class IntVExecuter implements IntVParserVisitor{
	private Hashtable FunctionTable	= new Hashtable();
	private Hashtable symTable		= new Hashtable();
	private Hashtable TableNoTable	= new Hashtable();
	private Vector mainVec			= new Vector();
	private Queue callVar			= new Queue();
	
	private IntVFileIO IO;

	private Stack stacksymTable		= new Stack();
	private Stack stackTableNoTable = new Stack();
	
	private int declaration		= 0;
	private boolean flag		= false;
	private boolean NodeDump	= false;
	
	private boolean varFlag		= false;
	private boolean varError	= false;
	
	private int arrayOrigin	= 0;
	private int arrayField	= 0;
	
	private int tmpAddres;
	
	private String array_name;
	
	private double label;
	
	MainGUI gui;
	
	public IntVExecuter(MainGUI gui){
		this.gui = gui;
		IO = new IntVFileIO(gui);
		if(gui.penPro.containsKey(PenProperties.PEN_SYSTEM_CODE)){
			IO.setCharCode(gui.penPro.getProperty(PenProperties.PEN_SYSTEM_CODE));
		}
		varType(Integer.parseInt(gui.penPro.getProperty(PenProperties.EXECUTER_VAR_DECLARATION)));
		arrayOrigin(Integer.parseInt(gui.penPro.getProperty(PenProperties.EXECUTER_VAR_ORIGIN)));
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
		IO.closeFileAll();
		throw new Error(); // It better not come here.
	}
	
	/**
	 * 構文木のルートノード
	 */
	public Object visit(ASTIntVUnit node, Object data) {
		if(NodeDump){
			//ノードダンプ出力
			System.out.println("-*-*-*- node dump -*-*-*-");
			node.dump("");
			System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-");
		}
		
		// 手続き・関数呼び出しの先読み
		int i, k = node.jjtGetNumChildren();
		for (i = 0; i < k; i++) {
			if( node.jjtGetChild(i) instanceof ASTFunction){
				node.jjtGetChild(i).jjtAccept(this, data);
			}
		}
		
		// プログラムの実行
		for (i = 0; i < k; i++) {
			if( !(node.jjtGetChild(i) instanceof ASTFunction)){
				node.jjtGetChild(i).jjtAccept(this, data);
			}
		}
		IO.closeFileAll();
		return null;
	}
	
	/**
	 * 実行エラー
	 */											// --> <10>
	public Object visit(ASTErrorOccur node, Object data) {
		IO.closeFileAll();
		throw new Error(); // It better not come here.
	}

	/**
	 * 手続き・関数呼び出しのノードを symTable に格納
	 */
	public Object visit(ASTFunction node, Object data) {
		Object[] obj = { node, data };
		symTable.put(node.varName, obj);
		FunctionTable.put(node.varName, obj);
		
		TableNoTable.put(node.varName,new Integer(gui.vt_model.getRowCount()));
		if( node.decl == PenProperties.DECLARATION_PROCEDURAL ){
			String[] stat_data = {"手続き", node.varName ,""};
			gui.vt_model.addRow(stat_data);
		} else if( node.decl == PenProperties.DECLARATION_INT ){
			String[] stat_data = {"整数", node.varName ,""};
			gui.vt_model.addRow(stat_data);
		} else if( node.decl == PenProperties.DECLARATION_LONG ){
			String[] stat_data = {"長整数", node.varName ,""};
			gui.vt_model.addRow(stat_data);
		} else if( node.decl == PenProperties.DECLARATION_DOUBLE ) {
			String[] stat_data = {"実数", node.varName ,""};
			gui.vt_model.addRow(stat_data);
		} else if( node.decl == PenProperties.DECLARATION_STRING ) {
			String[] stat_data = {"文字列", node.varName ,""};
			gui.vt_model.addRow(stat_data);
		} else if( node.decl == PenProperties.DECLARATION_BOOLEAN ) {
			String[] stat_data = {"真偽", node.varName ,""};
			gui.vt_model.addRow(stat_data);
		}
		
		return null;
	}

	/**
	 * 仮引数の処理を行う
	 */
	public Object visit(ASTFunctionVar node, Object data) {
		Object obj = null;
		String var_name = (String) ((ASTDecl) (node.jjtGetChild(0))).varName;

		Object var = callVar.pop();

		
		if( var instanceof Vector){
			FunctionArrayVar( (Vector) var, var_name + "[", node.decl);
			obj = var;
		} else {
			String[] stat_data = {null, var_name ,null};
			TableNoTable.put(var_name,new Integer(gui.vt_model.getRowCount()));
			
			if(node.decl == PenProperties.DECLARATION_INT){
				if(var instanceof Double){
					var = Double.valueOf(var.toString()).longValue();
				}
				obj = Long.valueOf(var.toString()).intValue();
				stat_data[0] = "整数";
			} else if(node.decl == PenProperties.DECLARATION_LONG){
				if(var instanceof Double){
					var = Double.valueOf(var.toString()).longValue();
				}
				obj = Long.valueOf(var.toString());
				stat_data[0] = "長整数";
			} else if(node.decl == PenProperties.DECLARATION_DOUBLE){
				obj = Double.valueOf(var.toString());
				stat_data[0] = "実数";
			} else if(node.decl == PenProperties.DECLARATION_STRING){
				obj = String.valueOf(var.toString());
				stat_data[0] = "文字列";
			} else if(node.decl == PenProperties.DECLARATION_BOOLEAN){
				obj = Boolean.valueOf(var.toString());
				stat_data[0] = "真偽";
			}
			stat_data[2] = obj.toString();
			gui.vt_model.addRow(stat_data);
		}
		symTable.put(var_name,obj);
		
		if( node.jjtGetNumChildren() > 1) { node.jjtGetChild(1).jjtAccept(this, data); }
		
		return null;
	}
	
	public void FunctionArrayVar(Vector vec, String var_name, int decl){
		for(int i = 0; i < vec.size(); i++){
			Object ver = vec.get(i);
			
			if( ver instanceof Vector){
				FunctionArrayVar( (Vector) ver, var_name + i + ",", decl);
			} else if(ver != null) {
				String name = var_name + i + "]";
				String[] stat_data = {null, name ,null};
				TableNoTable.put(name,new Integer(gui.vt_model.getRowCount()));

				if( (decl == PenProperties.DECLARATION_INT		&& ver instanceof Integer) || 
					(decl == PenProperties.DECLARATION_LONG		&& ver instanceof Long) || 
					(decl == PenProperties.DECLARATION_DOUBLE	&& ver instanceof Double) ||
					(decl == PenProperties.DECLARATION_STRING	&& ver instanceof String) ||
					(decl == PenProperties.DECLARATION_BOOLEAN	&& ver instanceof Boolean)
				) {
					stat_data[0] = "参照";
				} else {
					gui.consoleAppend.appendAll("### "  + gui.run_point.getLineCount() + "行目:引数のタイプ不整合\n");
					runBreak(true);
				}
				stat_data[2] = ver.toString();
				gui.vt_model.addRow(stat_data);
			}
		}
	}

	/**
	 * 変数の宣言
	 */
	public Object visit(ASTVarDecl node, Object data) {
		declaration = node.decl;
		run_flag(node.line_num1, true);
		int i, k = node.jjtGetNumChildren();
		for (i = 0; i < k; i++){
			run_flag(node.line_num1, false);
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return null;
	}

	/**
	 * 変数の領域確保
	 */
	public Object visit(ASTDecl node, Object data) {
		int k = node.jjtGetNumChildren();
		
		if (symTable.containsKey(node.varName)) {
			gui.consoleAppend.appendAll("### "  + gui.run_point.getLineCount() + "行目の \""+ node.varName + "\" は既に変数として宣言されています\n");
			runBreak(true);
		} else if(k > 0){
			array_name = node.varName + "[";
			Vector vec = (Vector) node.jjtGetChild(0).jjtAccept(this, data);
			symTable.put(node.varName, vec);
		}else{
			Object obj = null;
			String[] stat_data = {null, node.varName ,null};

			TableNoTable.put(node.varName,new Integer(gui.vt_model.getRowCount()));

			if( declaration == PenProperties.DECLARATION_INT ){
				obj = new Integer(0);
				stat_data[0] = "整数";
				stat_data[2] = "0";
			} else if( declaration == PenProperties.DECLARATION_LONG ){
				obj = new Long(0);
				stat_data[0] = "長整数";
				stat_data[2] = "0";
			} else if( declaration == PenProperties.DECLARATION_DOUBLE ){
				obj = new Double(0.0);
				stat_data[0] = "実数";
				stat_data[2] = "0.0";
			} else if( declaration == PenProperties.DECLARATION_STRING ){
				obj = new String();
				stat_data[0] = "文字列";
				stat_data[2] = "";
			} else if( declaration == PenProperties.DECLARATION_BOOLEAN ){
				obj = new Boolean(false);
				stat_data[0] = "真偽";
				stat_data[2] = "false";
			}
			symTable.put(node.varName, obj);
			gui.vt_model.addRow(stat_data);
		}
		return node.varName;
	}
	
	/**
	 * 変数を配列として確保する
	 */
	public Object visit(ASTArray node, Object data) {
		String var,array;
		int k = node.jjtGetNumChildren();
		array = array_name;
		Integer i = (Integer)node.jjtGetChild(0).jjtAccept(this, data);
		
		Vector vec = new Vector();
		
		if(k > 1){
			for( int j = 0; j <= i.intValue(); j++){
				if((arrayOrigin == 1 && j == 0) || (arrayField == 1 && j == i.intValue())) {
					vec.add(null);
				} else {
					array_name = array + String.valueOf(j) + ",";
					vec.add(node.jjtGetChild(1).jjtAccept(this, data));
				}
			}
			return vec;
		}else{
			for( int j = 0; j <= i.intValue(); j++){
				if((arrayOrigin == 1 && j == 0) || (arrayField == 1 && j == i.intValue())) {
					vec.add(null);
				} else {
					String x = "";
					String y = "";
					var = array + String.valueOf(j) + "]";
					if( declaration == PenProperties.DECLARATION_INT ) {
						x = "整数";
						y = "0";
						vec.add(new Integer(0));
					} else if( declaration == PenProperties.DECLARATION_LONG ) {
						x = "長整数";
						y = "0";
						vec.add(new Long(0));
					} else if( declaration == PenProperties.DECLARATION_DOUBLE ) {
						x = "実数";
						y = "0.0";
						vec.add(new Double(0));
					} else if( declaration == PenProperties.DECLARATION_STRING ) {
						x = "文字列";
						y = "";
						vec.add(new String());
					} else if( declaration == PenProperties.DECLARATION_BOOLEAN ) {
						x = "真偽";
						y = "true";
						vec.add(new Boolean(true));
					}
					
					TableNoTable.put(var,new Integer(gui.vt_model.getRowCount()));
					String[] stat_data = {x, var, y};
					gui.vt_model.addRow(stat_data);
				}
			}
			return vec;
		}
	}
	
	/**
	 * 複数の計算式
	 */
	public Object visit(ASTAssignStats node, Object data) {
		run_flag(node.line_num1, true);
		int i, k = node.jjtGetNumChildren();
		for (i = 0; i < k; i++){
			run_flag(node.line_num1, false);
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return null;
	}
	
	/**
	 * 変数 ← 計算式
	 * 計算式のノードを処理し、結果を変数に格納する
	 */
	public Object visit(ASTAssignStat node, Object data) {
		//node.jjtGetChild(0).jjtAccept(this, data);
		Object i = null;
		Object input = new Object();

		String varName = ((ASTIdent)(node.jjtGetChild(0))).varName;
		if(symTable.containsKey(varName) || varFlag) {
			if(node.InputFlag) {
				input = input_wait();
			} else {
				input = node.jjtGetChild(1).jjtAccept(this, data);
			}
		} else {
			if(stacksymTable.size() > 0 && ((Hashtable) stacksymTable.get(0)).containsKey(varName)){
				i = ((Hashtable) stacksymTable.get(0)).get(varName);
			} else {
				gui.consoleAppend.appendAll("### "  + gui.run_point.getLineCount()
						+ "行目で宣言されていない変数 "+ varName +" が使用されました\n");
				runBreak(true);
			}
		}
		
		if(symTable.containsKey(varName) || i != null) {
			Object obj = new Object();
			if(i == null){
				i = symTable.get(varName);
			}
			
			if(i instanceof Integer){
				if(input instanceof Double){
					input = Double.valueOf(input.toString()).longValue();
				}
				obj = Long.valueOf(input.toString()).intValue();
			} else if(i instanceof Long){
				if(input instanceof Double){
					input = Double.valueOf(input.toString()).longValue();
				}
				obj = Long.valueOf(input.toString());
			} else if(i instanceof Double){
				obj = Double.valueOf(input.toString());
			} else if(i instanceof String){
				obj = String.valueOf(input.toString());
			} else if(i instanceof Boolean){
				obj = Boolean.valueOf(input.toString());
			}
			
			if(i instanceof Vector){
				String tmpname = varName;
				array_name = varName + "[";
				Object tmp = node.jjtGetChild(0).jjtAccept(this, data);
				varName = array_name + "]";
				
				if(tmp instanceof Vector){
					setArrayVar( (Vector) input, tmpname + "[");
					symTable.put(tmpname,(Vector) input);
					return null;
				} else if(tmp instanceof Integer){
					if(input instanceof Double){
						input = Double.valueOf(input.toString()).longValue();
					}
					obj = Long.valueOf(input.toString()).intValue();
				} else if(tmp instanceof Long){
					if(input instanceof Double){
						input = Double.valueOf(input.toString()).longValue();
					}
					obj = Long.valueOf(input.toString());
				} else if(tmp instanceof Double){
					obj = Double.valueOf(input.toString());
				} else if(tmp instanceof String){
					obj = String.valueOf(input.toString());
				} else if(tmp instanceof Boolean){
					obj = Boolean.valueOf(input.toString());
				}
				mainVec.set(tmpAddres, obj);
			} else {
				symTable.put(varName,obj);
			}

			Integer TableNo = (Integer)TableNoTable.get(varName);
			gui.vt_model.setValueAt(obj,TableNo.intValue(),2);
		} else if(varFlag) {
			if(node.jjtGetChild(0).jjtGetNumChildren() > 0){
				//TODO array
			//	String tmpname = varName;
			//	array_name = varName + "[";
			//	Vector vec = (Vector) node.jjtGetChild(0).jjtAccept(this, data);
			//	setArrayVar( vec, tmpname + "[");
			//	symTable.put(varName, vec);
				gui.consoleAppend.appendAll("### "  + gui.run_point.getLineCount()
						+ "行目: 配列名 "+ varName +" を使用するには変数宣言が必要です\n");
				runBreak(true);
			} else {
				varSecure(varName, input);
			}
		}
		return null;
	}
	
	public void setArrayVar(Vector vec, String var_name){
		for(int i = 0; i < vec.size(); i++){
			Object ver = vec.get(i);
			if( ver instanceof Vector){
				setArrayVar( (Vector) ver, var_name + i + ",");
			} else {
				String name = var_name + i + "]";
				Integer TableNo = (Integer)TableNoTable.get(name);
				gui.vt_model.setValueAt(ver.toString(),TableNo.intValue(),2);
			}
		}
	}
	
	/**
	 * IF文
	 */
	public Object visit(ASTIfStat node, Object data) {
		Boolean b;
		
		try {
			b = (Boolean)node.jjtGetChild(0).jjtAccept(this, data);
		} catch(Exception e){
			throw new ConditionFormatException(node.line_num1);
		}
		
		run_flag(node.line_num1, true);
		if(b.booleanValue()) {
			Object var = node.jjtGetChild(1).jjtAccept(this, data);
			if(var instanceof ASTBreak || var instanceof ASTReturn) {
				return var;
			}
			if(node.jjtGetNumChildren() == 3) {
				run_flag(node.line_num2, true);
			}
		}else if (node.jjtGetNumChildren() == 3) {
			run_flag(node.line_num2, true);
			Object var = node.jjtGetChild(2).jjtAccept(this, data);
			if(var instanceof ASTBreak || var instanceof ASTReturn) {
				return var;
			}
		}
		if (node.line_num3 != 0) {
			run_flag(node.line_num3, true);
		}
	
		return null;
	}

	/**
	 * Do-While文
	 */
	public Object visit(ASTDoWhileStat node, Object data) {
		Boolean b;
		do {
			run_flag(node.line_num1, true);
			Object var = node.jjtGetChild(0).jjtAccept(this, data);
			if(var instanceof ASTBreak){
				run_flag(node.line_num2, true);
				break;
			} else if(var instanceof ASTReturn) {
				return var;
			}
			
			try {
				b = (Boolean)node.jjtGetChild(1).jjtAccept(this, data);
			} catch(Exception e){
				throw new ConditionFormatException(node.line_num1);
			}
			
			run_flag(node.line_num2, true);
			if (!b.booleanValue()) {
				run_flag(node.line_num2, true);
				break;
			}
		} while (true);

		return null;
	}

	/**
	 * RepeatUntil文
	 */
	public Object visit(ASTRepeatUntil node, Object data) {
		Boolean b;
		do {
			run_flag(node.line_num1, true);
			Object var = node.jjtGetChild(0).jjtAccept(this, data);
			if(var instanceof ASTBreak){
				run_flag(node.line_num2, true);
				break;
			} else if(var instanceof ASTReturn) {
				return var;
			}
			
			try {
				b = (Boolean)node.jjtGetChild(1).jjtAccept(this, data);
			} catch(Exception e){
				throw new ConditionFormatException(node.line_num1);
			}
			
			run_flag(node.line_num2, true);
			if (b.booleanValue()) {
				run_flag(node.line_num2, true);
				break;
			}
		} while (true);
		return null;
	}

	/**
	 * For文の増減値を処理
	 */
	public Object visit(ASTForStatAdd node, Object data) {
		if(node.jjtGetNumChildren() == 0){
			return new Integer(node.op);
		}else{
			Object obj = node.jjtGetChild(0).jjtAccept(this, data);
			if( obj instanceof Integer ){
				return new Integer(((Integer) obj).intValue() * node.op);
			} else if( obj instanceof Long ){
				return new Long(((Long) obj).longValue() * node.op);
			} else if( obj instanceof Double ){
				return new Double(((Double) obj).doubleValue() * node.op);
			} else {
				return null;
			}
		}
	}

	public Object visit(ASTCase node, Object data) {
		int i, k = node.jjtGetNumChildren();
		
		for (i = 0; i < k; i++){
			if( node.jjtGetChild(i) instanceof ASTLabel ){
				Object obj = node.jjtGetChild(i).jjtAccept(this, data);
				
				if(obj == null || Double.valueOf(obj.toString()).doubleValue() == label){
					for(i++; i < k; i++){
						if( node.jjtGetChild(i) instanceof ASTLabel ){
							break;
						}
						node.jjtGetChild(i).jjtAccept(this, data);
					}
					break;
				}
			}
		}
		return null;
	}

	public Object visit(ASTLabel node, Object data) {
		int i = node.jjtGetNumChildren();
		
		run_flag(node.line_num1, true);
		
		if(i > 0){
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			return null;
		}
	}

	public Object visit(ASTWhileSwitchFor node, Object data) {
		if (node.p.equals(node.wh)) {
			return runWhileStat(node, data);
		} else if(node.p.equals(node.sw)){
			return runSwitch(node, data);
		} else if(node.p.equals(node.fr)){
			return runFor(node, data);
		}
		return null;
	}

	/**
	 * while文の実体
	 */
	public Object runWhileStat(SimpleNode node, Object data) {
		Boolean b;
		do {
			try {
				b = (Boolean)node.jjtGetChild(0).jjtAccept(this, data);
			} catch(Exception e){
				throw new ConditionFormatException(node.line_num1);
			}
			run_flag(node.line_num1, true);
			if (b.booleanValue()) {
				Object var = node.jjtGetChild(1).jjtAccept(this, data);
				if(var instanceof ASTBreak){
					run_flag(node.line_num2, true);
					break;
				} else if(var instanceof ASTReturn) {
					return var;
				}
			}else{
				run_flag(node.line_num2, true);
				break;
			}
			
		} while (true);
		return null;
	}

	/**
	 * switch文の実体
	 */
	public Object runSwitch(SimpleNode node, Object data) {
		double i = label;
		label = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();

		run_flag(node.line_num1, true);

		node.jjtGetChild(1).jjtAccept(this, data);
		label = i;

		run_flag(node.line_num2, true);
		
		return null;
	}

	/**
	 * for文の実体
	 */
	public Object runFor(SimpleNode node, Object data) {
		Integer TableNo;
		Object in, tmp, obj = null, step, var, in_var;
		int op;
		
		String varName = ((ASTIdent)(node.jjtGetChild(0))).varName;
		
		run_flag(node.line_num1, true);
		
		in = node.jjtGetChild(1).jjtAccept(this, data);
		
		if(symTable.containsKey(varName)) {
			tmp = symTable.get(varName);
			if( tmp instanceof Integer ){
				symTable.put(varName, new Integer(Long.valueOf(in.toString()).intValue()));
			} else if( tmp instanceof Long ){
				symTable.put(varName, Long.valueOf(in.toString()));
			} else if( tmp instanceof Double ){
				symTable.put(varName, Double.valueOf(in.toString()));
			} else if( tmp instanceof Vector){
				String tmpname = varName;
				array_name = varName + "[";
				Object tmp2 = node.jjtGetChild(0).jjtAccept(this, data);
				varName = array_name + "]";
				
				if( tmp2 instanceof Integer){
					obj = new Integer(Long.valueOf(in.toString()).intValue());
				} else if( tmp2 instanceof Long){
					obj = Long.valueOf(in.toString());
				} else if( tmp2 instanceof Double){
					obj = Double.valueOf(in.toString());
				}
				mainVec.set(tmpAddres, obj);
			}
			
			TableNo = (Integer)TableNoTable.get(varName);
			gui.vt_model.setValueAt(in.toString(),TableNo.intValue(),2);
		} else if(varFlag){
			varSecure(varName, in);
		} else {
			gui.consoleAppend.appendAll("### "  + gui.run_point.getLineCount()
					+ "行目で宣言されていない変数 "+ varName +" が使用されました\n");
			runBreak(true);
		}
		
		
		do {
			varName = ((ASTIdent)(node.jjtGetChild(0))).varName;
			Double x = new Double(node.jjtGetChild(0).jjtAccept(this, data).toString());
			Double y = new Double(node.jjtGetChild(2).jjtAccept(this, data).toString());
			op = ((ASTForStatAdd)(node.jjtGetChild(3))).op;

			if( op > 0 && (x.compareTo(y) <= 0) || op < 0 && (x.compareTo(y) >= 0)){
				obj = node.jjtGetChild(4).jjtAccept(this, data);
				if(obj instanceof ASTBreak){
					run_flag(node.line_num2, true);
					break;
				} else if(obj instanceof ASTReturn) {
					return obj;
				}
				run_flag(node.line_num1, true);
				
				step = node.jjtGetChild(3).jjtAccept(this, data);
				var  = node.jjtGetChild(0).jjtAccept(this, data);
				
				if(var instanceof Double || step instanceof Double){
					double a = Double.valueOf(var.toString()).doubleValue();
					double b = Double.valueOf(step.toString()).doubleValue();
					in_var = new Double(a + b);
					
					if(var instanceof Integer){
						in_var = new Integer(((Double) in_var).intValue());
					}
				} else if(var instanceof Long || step instanceof Long){
					long a = Long.valueOf(var.toString()).longValue();
					long b = Long.valueOf(step.toString()).longValue();
					in_var = new Long(a + b);
				} else {
					int a = Integer.valueOf(var.toString()).intValue();
					int b = Integer.valueOf(step.toString()).intValue();
					in_var = new Integer(a + b);
				}

				if(symTable.get(varName) instanceof Vector){
					String tmpname = varName;
					array_name = varName + "[";
					Object tmp2 = node.jjtGetChild(0).jjtAccept(this, data);
					varName = array_name + "]";
					mainVec.set(tmpAddres, in_var);
				} else {
					symTable.put(varName, in_var);
				}

				TableNo = (Integer)TableNoTable.get(varName);
				gui.vt_model.setValueAt(in_var.toString(),TableNo.intValue(),2);
			} else {
				run_flag(node.line_num2, true);
				break;
			}
		}while (true);
		return null;
	}
	
	/**
	 * 無限ループ
	 */
	public Object visit(ASTInfiniteLoop node, Object data) {
		while(true){
			run_flag(node.line_num1, true);
			Object var = node.jjtGetChild(0).jjtAccept(this, data);
			if( var instanceof ASTBreak ) {
				break;
			} else if( var instanceof ASTReturn) {
				return var;
			}
		}
		run_flag(node.line_num2, true);
		return null;
	}

	/**
	 * ないしょ
	 */
	public Object visit(ASTGetStat node, Object data) {
		node.jjtGetChild(0).jjtAccept(this, data);

		Object input = input_wait();
		Object obj = new Object();
		if(symTable.get(((ASTIdent)(node.jjtGetChild(0))).varName) instanceof Integer){
			obj = new Integer(Long.valueOf(input.toString()).intValue());
		} else if(symTable.get(((ASTIdent)(node.jjtGetChild(0))).varName) instanceof Long){
			obj = Long.valueOf(input.toString());
		} else if(symTable.get(((ASTIdent)(node.jjtGetChild(0))).varName) instanceof Double){
			obj = Double.valueOf(input.toString());
		} else if(symTable.get(((ASTIdent)(node.jjtGetChild(0))).varName) instanceof String){
			obj = String.valueOf(input.toString());
		} else if(symTable.get(((ASTIdent)(node.jjtGetChild(0))).varName) instanceof Boolean){
			obj = Boolean.valueOf(input.toString());
		}
		Integer TableNo = (Integer)TableNoTable.get(((ASTIdent)(node.jjtGetChild(0))).varName);
		gui.vt_model.setValueAt(obj,TableNo.intValue(),2);
		return null;
	}

	/**
	 * 出力文
	 */
	public Object visit(ASTPutStat node, Object data) {
		run_flag(node.line_num1, true);
		String str = new String();
		int i, k = node.jjtGetNumChildren();
		for (i = 0; i < k; i++) {
			str += node.jjtGetChild(i).jjtAccept(this, data).toString();
		}
		gui.consoleAppend.appendAll(str + node.n);
		return null;
	}

	/**
	 * 制御構造内のノードを処理
	 */
	public Object visit(ASTBlock node, Object data) {
		int i, k = node.jjtGetNumChildren();

		for (i = 0; i < k; i++){
			Object var = node.jjtGetChild(i).jjtAccept(this, data);
			if(var instanceof ASTBreak || var instanceof ASTReturn) {
				return var;
			}
		}
		return null;
	}

	/**
	 * ループ処理を抜ける break文 の処理
	 */
	public  Object visit(ASTBreak node, Object data) {
		run_flag(node.line_num1, true);
		return node;
	}
	
	/**
	 * 比較演算子 "<" の処理
	 */
	public Object visit(ASTLSNode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);
		
		int ans;
		
		if(i instanceof Double || j instanceof Double){
			Double x = Double.valueOf(i.toString());
			Double y = Double.valueOf(j.toString());

			ans = x.compareTo(y);
		} else {
			Long x = Long.valueOf(i.toString());
			Long y = Long.valueOf(j.toString());
			
			ans = x.compareTo(y);
		}
		
		if(ans < 0){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 比較演算子 ">" の処理
	 */
	public Object visit(ASTGTNode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);
		
		int ans;
		
		if(i instanceof Double || j instanceof Double){
			Double x = Double.valueOf(i.toString());
			Double y = Double.valueOf(j.toString());

			ans = x.compareTo(y);
		} else {
			Long x = Long.valueOf(i.toString());
			Long y = Long.valueOf(j.toString());
			
			ans = x.compareTo(y);
		}
		
		if(ans > 0){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 比較演算子 "<=" の処理
	 */
	public Object visit(ASTLENode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);

		int ans;
		
		if(i instanceof Double || j instanceof Double){
			Double x = Double.valueOf(i.toString());
			Double y = Double.valueOf(j.toString());

			ans = x.compareTo(y);
		} else {
			Long x = Long.valueOf(i.toString());
			Long y = Long.valueOf(j.toString());
			
			ans = x.compareTo(y);
		}
		
		if(ans <= 0){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 比較演算子 ">=" の処理
	 */
	public Object visit(ASTGENode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);

		int ans;
		
		if(i instanceof Double || j instanceof Double){
			Double x = Double.valueOf(i.toString());
			Double y = Double.valueOf(j.toString());

			ans = x.compareTo(y);
		} else {
			Long x = Long.valueOf(i.toString());
			Long y = Long.valueOf(j.toString());
			
			ans = x.compareTo(y);
		}
		
		if(ans >= 0){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 比較演算子 "==" の処理
	 */
	public Object visit(ASTEQNode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);
		
		if(i instanceof Boolean && j instanceof Boolean){
			return new Boolean(((Boolean)i).booleanValue() == ((Boolean)j).booleanValue());
		} else {
			return new Boolean(i.toString().equals(j.toString()));
		}
	}

	/**
	 * 比較演算子 "!=" の処理
	 */
	public Object visit(ASTNTNode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);
		
		if(i instanceof Boolean && j instanceof Boolean){
			return new Boolean(((Boolean) i).booleanValue() != ((Boolean) j).booleanValue());
		} else {
			return new Boolean(!i.toString().equals(j.toString()));
		}
	}

	/**
	 * 論理演算子 "AND" の処理
	 */
	public Object visit(ASTANDNode node, Object data) {
		Boolean i = (Boolean)node.jjtGetChild(0).jjtAccept(this, data);
		Boolean j = (Boolean)node.jjtGetChild(1).jjtAccept(this, data);

		return new Boolean(i.booleanValue() && j.booleanValue());
	}

	/**
	 * 論理演算子 "OR" の処理
	 */
	public Object visit(ASTORNode node, Object data) {
		Boolean i = (Boolean)node.jjtGetChild(0).jjtAccept(this, data);
		Boolean j = (Boolean)node.jjtGetChild(1).jjtAccept(this, data);
				
		return new Boolean(i.booleanValue() || j.booleanValue());
	}

	/**
	 * 論理演算子 "NOT" の処理
	 */
	public Object visit(ASTNOTNode node, Object data) {
		Boolean i = (Boolean)node.jjtGetChild(0).jjtAccept(this, data);
		
		if(i.booleanValue()){
			return new Boolean(false);
		}else{
			return new Boolean(true);		   
		}
	}

	/**
	 * 演算子 "+" の処理
	 */
	public Object visit(ASTAddNode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);
		
		if(i instanceof String || j instanceof String){
			return new String(i.toString() + j.toString());
		} else if(i instanceof Double || j instanceof Double){
			return new Double(
				Double.valueOf(i.toString()).doubleValue()
				+ Double.valueOf(j.toString()).doubleValue()
			);
		} else if(i instanceof Long || j instanceof Long){
			return new Long(
				Long.valueOf(i.toString()).longValue()
				+ Long.valueOf(j.toString()).longValue()
			);
		} else {
			return new Integer(((Integer) i).intValue() + ((Integer) j).intValue());
		}
	}

	/**
	 * 演算子 "-" の処理
	 */
	public Object visit(ASTSubNode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);
		
		if(i instanceof Double || j instanceof Double){
			return new Double(
				Double.valueOf(i.toString()).doubleValue()
				- Double.valueOf(j.toString()).doubleValue()
			);
		} else if(i instanceof Long || j instanceof Long){
			return new Long(
				Long.valueOf(i.toString()).longValue()
				- Long.valueOf(j.toString()).longValue()
			);
		} else {
			return new Integer(((Integer) i).intValue() - ((Integer) j).intValue());
		}
	}

	/**
	 * 演算子 "*" の処理
	 */
	public Object visit(ASTMulNode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);
		
		if(i instanceof Double || j instanceof Double){
			return new Double(
				Double.valueOf(i.toString()).doubleValue()
				* Double.valueOf(j.toString()).doubleValue()
			);
		} else if(i instanceof Long || j instanceof Long){
			return new Long(
				Long.valueOf(i.toString()).longValue()
				* Long.valueOf(j.toString()).longValue()
			);
		} else {
			return new Integer(((Integer) i).intValue() * ((Integer) j).intValue());
		}
	}

	/**
	 * 演算子 "/" の処理
	 */
	public Object visit(ASTDivNode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);

		if((i instanceof Integer || i instanceof Long) && (j instanceof Integer || j instanceof Long)
				|| (node.DivFlag && gui.penPro.getProperty(PenProperties.EXECUTER_DIV_MODE).equals("1"))){
			return new Long(Long.valueOf(i.toString()) / Long.valueOf(j.toString()));
		} else {
			return new Double(
				Double.valueOf(i.toString()).doubleValue()
				/ Double.valueOf(j.toString()).doubleValue()
			);
		}
	}	

	/**
	 * 演算子 "%" の処理
	 */
	public Object visit(ASTSurNode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);

		if(i instanceof Double || j instanceof Double){
			return new Double(
				Double.valueOf(i.toString()).doubleValue()
				% Double.valueOf(j.toString()).doubleValue()
			);
		} else if(i instanceof Long || j instanceof Long){
			return new Long(
				Long.valueOf(i.toString()).longValue()
				% Long.valueOf(j.toString()).longValue()
			);
		} else {
			return new Integer(((Integer) i).intValue() % ((Integer) j).intValue());
		}
	}

	/**
	 * "-" 変数の処理
	 */
	public Object visit(ASTMinNode node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		if(i instanceof Integer){
			return new Integer(-((Integer) i).intValue());
		}else if(i instanceof Long){
			return new Long(-Long.valueOf(i.toString()).longValue());
		} else {
			return new Double(-Double.valueOf(i.toString()).doubleValue());
		}
	}

	/**
	 * 変数名から値を返す
	 */
	public Object visit(ASTIdent node, Object data) {
		if( node.flag ) {
			int k = node.jjtGetNumChildren();
			if(k > 0){
				mainVec = (Vector) symTable.get(node.varName);
				return node.jjtGetChild(0).jjtAccept(this, data);
			} else 	if( !symTable.containsKey(node.varName)){
				if(stacksymTable.size() > 0 && ((Hashtable) stacksymTable.get(0)).containsKey(node.varName)){
					return ((Hashtable) stacksymTable.get(0)).get(node.varName);
				}
				if(varFlag){
					gui.consoleAppend.appendAll("### "  + gui.run_point.getLineCount() + "行目の変数 "+ node.varName + " は未定義です\n");
				} else {
					gui.consoleAppend.appendAll("### "  + gui.run_point.getLineCount() + "行目の変数 "+ node.varName + " は宣言されていません\n");
				}
			} else {
				return symTable.get(node.varName);
			}
		} else {
			return Function(node, data, node.varName);
		}
		return null;
	}

	/**
	 * 変数（配列）から値を返す
	 */
	public Object visit(ASTArrayNum node, Object data) {
		int j;
		int k = node.jjtGetNumChildren();

		String name = array_name;
		Vector localVec = mainVec;
		tmpAddres = ((Integer) node.jjtGetChild(0).jjtAccept(this, data)).intValue();
		
		if(k > 1 ) {
			mainVec = (Vector) localVec.get(tmpAddres);
			array_name = name + String.valueOf(tmpAddres) + ",";
			return node.jjtGetChild(1).jjtAccept(this, data);
		} else {
			mainVec = localVec;
			array_name = name + String.valueOf(tmpAddres);
			
			Object t = localVec.get(tmpAddres);
	
			if( t == null){
				gui.consoleAppend.appendAll("### 配列の範囲を超えた数が指定されました\n");
				runBreak(true);
				return null;
			} else {
				return t;
			}
		}
	}

	/**
	 * 手続き・関数呼び出し
	 */
	public Object visit(ASTFunctionCall node, Object data) {
		return Function(node, data, node.varName);
	}

	/**
	 * 関数呼び出しの戻り値
	 */
	public Object visit(ASTReturn node, Object data) {
		run_flag(node.line_num1, true);
		node.returnValue = node.jjtGetChild(0).jjtAccept(this, data);
		return node;
	}

	/**
	 * input() : 入力文
	 */
	public Object visit(ASTGet node, Object data) {
		return input_wait();
	}

	/**
	 * random() : 0～引数の間のランダムな値を返す
	 */
	public Object visit(ASTRandom node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		long j = Long.valueOf(i.toString()).longValue();
		double random = Math.random() * (j + 1);
		return new Long((long)random);
	}

	/**
	 * sin() : 引数の正弦を返す
	 */
	public Object visit(ASTSine node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		return Math.sin(Double.valueOf(i.toString()));
	}

	/**
	 * cos() : 引数の余弦を返す
	 */
	public Object visit(ASTCosine node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		return Math.cos(Double.valueOf(i.toString()));
	}

	/**
	 * tan() : 引数の正接を返す
	 */
	public Object visit(ASTTangent node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		return Math.tan(Double.valueOf(i.toString()));
	}

	/**
	 * sqrt() : 引数の平方根の値を返す
	 */
	public Object visit(ASTSqrt node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		return Math.sqrt(Double.valueOf(i.toString()));
	}

	/**
	 * log() : 引数の自然対数値 (底は e) を返す
	 */
	public Object visit(ASTLog node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		double j = Double.valueOf(i.toString()).doubleValue();
		double log = Math.log(j);
		return new Double(log);
	}

	/**
	 * abs() : 引数の絶対値を返す
	 */
	public Object visit(ASTAbs node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		if(i instanceof Integer){
			int j = Long.valueOf(i.toString()).intValue();
			int abs = Math.abs(j);
			return new Integer(abs);
		} else if(i instanceof Long){
			long j = Long.valueOf(i.toString()).longValue();
			long abs = Math.abs(j);
			return new Long(abs);
		} else {
			double j = Double.valueOf(i.toString()).doubleValue();
			double abs = Math.abs(j);
			return new Double(abs);
		}
	}

	/**
	 * ceil() : 引数の小数点以下 切り上げ
	 */
	public Object visit(ASTCeil node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		return Math.ceil(Double.valueOf(i.toString()));
	}

	/**
	 * floor() : 引数の小数点以下 切り捨て
	 */
	public Object visit(ASTFloor node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		return Math.floor(Double.valueOf(i.toString()));
	}

	/**
	 * round() : 引数の小数点以下 四捨五入
	 */
	public Object visit(ASTRound node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		return Math.round(Double.valueOf(i.toString()));
	}

	/**
	 * int() : 引数の型を「整数」に変換
	 */
	public Object visit(ASTInt node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		int j = Double.valueOf(i.toString()).intValue();
		return new Integer(j);
	}

	/**
	 * long() : 引数の型を「Long」に変換
	 */
	public Object visit(ASTLong node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		long j = Long.parseLong(i.toString());
		return new Long(j);
	}

	/**
	 * length()
	 */
	public Object visit(ASTLength node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		return new Integer(i.toString().length());
	}
	
	
	/**
	 * append()
	 */
	public Object visit(ASTAppend node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);
		return new String(i.toString() + j.toString());
	}	


	/**
	 * substring()
	 */
	public Object visit(ASTSubstring node, Object data) {
		int k = node.jjtGetNumChildren();
		String substring;
		
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		int x = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).intValue();
		if( k >= 3){
			int y = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).intValue();
			substring = i.toString().substring(x,x+y);
		} else {
			substring = i.toString().substring(x);
		}
		return new String(substring);
	}

	/**
	 * insert()
	 */
	public Object visit(ASTInsert node, Object data) {
		String str1	= (String) node.jjtGetChild(0).jjtAccept(this, data);
		Integer n	= (Integer) node.jjtGetChild(1).jjtAccept(this, data);
		String str2	= (String) node.jjtGetChild(2).jjtAccept(this, data);
		
		String in = str1.substring(0,n.intValue()) + str2 + str1.substring(n.intValue());
		return new String(in);
	}

	/**
	 * replace()
	 */
	public Object visit(ASTReplace node, Object data) {
		String str1	= (String) node.jjtGetChild(0).jjtAccept(this, data);
		Integer n	= (Integer) node.jjtGetChild(1).jjtAccept(this, data);
		Integer m	= (Integer) node.jjtGetChild(2).jjtAccept(this, data);
		String str2	= (String) node.jjtGetChild(3).jjtAccept(this, data);
		
		String in = str1.substring(0,n.intValue()) + str2 + str1.substring(n.intValue() + m.intValue());
		return new String(in);
	}

	/**
	 * extract()
	 */
	public Object visit(ASTExtract node, Object data) {
		String[] ex;
		String str1	= (String) node.jjtGetChild(0).jjtAccept(this, data);
		String str2	= (String) node.jjtGetChild(1).jjtAccept(this, data);
		Integer n	= (Integer) node.jjtGetChild(2).jjtAccept(this, data);
		
		ex = str1.split(str2);

		if( ex.length <= n.intValue() ){
			int c = -2;
			return new String(new Character((char) c).toString());
		} else {
			return new String(ex[n.intValue()]);
		}
	}
	
	/**
	 * str2int()
	 */
	public Object visit(ASTStr2Int node, Object data) {
		String str = (String) node.jjtGetChild(0).jjtAccept(this, data);
		
		return new Integer((int) str.charAt(0));
	}

	/**
	 * int2str()
	 */
	public Object visit(ASTInt2Str node, Object data) {
		Integer n = (Integer)node.jjtGetChild(0).jjtAccept(this, data);
		
		return new String(Character.toString((char) n.intValue()));
	}

	/**
	 * compare()
	 */
	public Object visit(ASTCompare node, Object data) {
		String str1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
		String str2 = (String) node.jjtGetChild(1).jjtAccept(this, data);
		
		return new Integer(str1.compareTo(str2));
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgOpenWindow node, Object data) {
		int w = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();
		int h = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).intValue();

		run_flag(node.line_num1, true);
		
		if( node.jjtGetNumChildren() < 3 ){
			gui.gDrawWindow.gOpenWindow(w, h);
		} else {
			double x = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();
			double y = Double.valueOf(node.jjtGetChild(3).jjtAccept(this, data).toString()).doubleValue();
			gui.gDrawWindow.gOpenWindow(w, h, x, y);
		}
		
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgOpenGraphWindow node, Object data) {
		int w = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();
		int h = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).intValue();
		double x1 = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();
		double y1 = Double.valueOf(node.jjtGetChild(3).jjtAccept(this, data).toString()).doubleValue();
		double x2 = Double.valueOf(node.jjtGetChild(4).jjtAccept(this, data).toString()).doubleValue();
		double y2 = Double.valueOf(node.jjtGetChild(5).jjtAccept(this, data).toString()).doubleValue();
		
		run_flag(node.line_num1, true);

		if( node.jjtGetNumChildren() < 7 ){
			gui.gDrawWindow.gOpenGraphWindow(w, h, x1, y1, x2, y2);
		} else {
			boolean g  = Boolean.valueOf(node.jjtGetChild(6).jjtAccept(this, data).toString()).booleanValue();
			gui.gDrawWindow.gOpenGraphWindow(w, h, x1, y1, x2, y2, g);
		}
		
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgCloseWindow node, Object data) {
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gCloseWindow();
		return null;
	}
	
	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgClearWindow node, Object data) {
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gClearWindow();
		return null;
	}
	
	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSaveWindow node, Object data) {
		String file = node.jjtGetChild(0).jjtAccept(this, data).toString();
		String mode = node.jjtGetChild(1).jjtAccept(this, data).toString();

		run_flag(node.line_num1, true);
		gui.gDrawWindow.gSaveWindow(file, mode);
		return null;
	}
	
	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetOrigin node, Object data) {
	
		run_flag(node.line_num1, true);
		
		double x = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
		gui.gDrawWindow.gSetOrigin(x, y);
		
		return null;
	}
	
	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetMap node, Object data) {
	
		run_flag(node.line_num1, true);
		
		double x1 = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y1 = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
		double x2 = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();
		double y2 = Double.valueOf(node.jjtGetChild(3).jjtAccept(this, data).toString()).doubleValue();
		gui.gDrawWindow.gSetMap(x1, y1, x2, y2);
		
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetFillColor node, Object data) {
		int r = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();
		int g = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).intValue();
		int b = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).intValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gSetFillColor(r, g, b);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetLineColor node, Object data) {
		int r = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();
		int g = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).intValue();
		int b = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).intValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gSetLineColor(r, g, b);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetTextColor node, Object data) {
		int r = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();
		int g = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).intValue();
		int b = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).intValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gSetTextColor(r, g, b);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetFont node, Object data) {
		String str = node.jjtGetChild(0).jjtAccept(this, data).toString();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gSetFont(str);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetFontType node, Object data) {
		int type = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gSetFontType(type);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetFontSize node, Object data) {
		int size = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gSetFontSize(size);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetDotShape node, Object data) {
		int type = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gSetDotShape(type);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetArrowDir node, Object data) {
		int edge = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gSetArrowDir(edge);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetArrowType node, Object data) {
		int type = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();

		run_flag(node.line_num1, true);
		
		if( node.jjtGetNumChildren() < 2 ){
			gui.gDrawWindow.gSetArrowType(type);
		} else {
			int edge = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).intValue();
			gui.gDrawWindow.gSetArrowType(type, edge);
		}
		
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetLineShape node, Object data) {
		int type = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();

		run_flag(node.line_num1, true);
		
		if( node.jjtGetNumChildren() < 2 ){
			gui.gDrawWindow.gSetLineShape(type);
		} else {
			double width = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
			gui.gDrawWindow.gSetLineShape(type, width);
		}
		
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetLineWidth node, Object data) {
		double width = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gSetLineWidth(width);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawPoint node, Object data) {
		double x = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();

		run_flag(node.line_num1, true);
		gui.gDrawWindow.gDrawPoint(x, y);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawLine node, Object data) {
		double x1 = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y1 = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
		double x2 = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();
		double y2 = Double.valueOf(node.jjtGetChild(3).jjtAccept(this, data).toString()).doubleValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gDrawLine(x1, y1, x2, y2);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawText node, Object data) {
		String str = node.jjtGetChild(0).jjtAccept(this, data).toString();
		int x = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).intValue();
		int y = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).intValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gDrawText(str, x, y);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawCircle node, Object data) {
		double x = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
		double w = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();

		run_flag(node.line_num1, true);
		gui.gDrawWindow.gDrawCircle(x, y, w);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgFillCircle node, Object data) {
		double x = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
		double w = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();

		run_flag(node.line_num1, true);
		gui.gDrawWindow.gFillCircle(x, y, w);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawOval node, Object data) {
		double x = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
		double w = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();
		double h = Double.valueOf(node.jjtGetChild(3).jjtAccept(this, data).toString()).doubleValue();

		run_flag(node.line_num1, true);
		gui.gDrawWindow.gDrawOval(x, y, w, h);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgFillOval node, Object data) {
		double x = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
		double w = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();
		double h = Double.valueOf(node.jjtGetChild(3).jjtAccept(this, data).toString()).doubleValue();

		run_flag(node.line_num1, true);
		gui.gDrawWindow.gFillOval(x, y, w, h);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawBox node, Object data) {
		double x = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
		double w = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();
		double h = Double.valueOf(node.jjtGetChild(3).jjtAccept(this, data).toString()).doubleValue();

		run_flag(node.line_num1, true);
		gui.gDrawWindow.gDrawBox(x, y, w, h);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgFillBox node, Object data) {
		double x = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
		double w = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();
		double h = Double.valueOf(node.jjtGetChild(3).jjtAccept(this, data).toString()).doubleValue();

		run_flag(node.line_num1, true);
		gui.gDrawWindow.gFillBox(x, y, w, h);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawArc node, Object data) {
		double x = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
		double w = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();
		double h = Double.valueOf(node.jjtGetChild(3).jjtAccept(this, data).toString()).doubleValue();
		double start  = Double.valueOf(node.jjtGetChild(4).jjtAccept(this, data).toString()).doubleValue();
		double extent = Double.valueOf(node.jjtGetChild(5).jjtAccept(this, data).toString()).doubleValue();
		int type   = Double.valueOf(node.jjtGetChild(6).jjtAccept(this, data).toString()).intValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gDrawArc(x, y, w, h, start, extent, type);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgFillArc node, Object data) {
		double x = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).doubleValue();
		double y = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).doubleValue();
		double w = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).doubleValue();
		double h = Double.valueOf(node.jjtGetChild(3).jjtAccept(this, data).toString()).doubleValue();
		double start  = Double.valueOf(node.jjtGetChild(4).jjtAccept(this, data).toString()).doubleValue();
		double extent = Double.valueOf(node.jjtGetChild(5).jjtAccept(this, data).toString()).doubleValue();
		int type   = Double.valueOf(node.jjtGetChild(6).jjtAccept(this, data).toString()).intValue();
		
		run_flag(node.line_num1, true);
		gui.gDrawWindow.gFillArc(x, y, w, h, start, extent, type);
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawPolygon node, Object data) {
		Vector x = (Vector) node.jjtGetChild(0).jjtAccept(this, data);
		Vector y = (Vector) node.jjtGetChild(1).jjtAccept(this, data);
		int p = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).intValue();
		
		int[] xPoints = new int[x.size()];
		int[] yPoints = new int[y.size()];

		for( int i = 0; i < x.size(); i++){
			xPoints[i] = Double.valueOf(x.get(i).toString()).intValue();
		}
		for( int i = 0; i < y.size(); i++){
			yPoints[i] = Double.valueOf(y.get(i).toString()).intValue();
		}
		
		run_flag(node.line_num1, true);
		
		gui.gDrawWindow.gDrawPolygon(xPoints, yPoints, p);
		
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgFillPolygon node, Object data) {
		Vector x = (Vector) node.jjtGetChild(0).jjtAccept(this, data);
		Vector y = (Vector) node.jjtGetChild(1).jjtAccept(this, data);
		int p = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).intValue();
		
		int[] xPoints = new int[x.size()];
		int[] yPoints = new int[y.size()];

		for( int i = 0; i < x.size(); i++){
			xPoints[i] = Double.valueOf(x.get(i).toString()).intValue();
		}
		for( int i = 0; i < y.size(); i++){
			yPoints[i] = Double.valueOf(y.get(i).toString()).intValue();
		}
		
		run_flag(node.line_num1, true);
		
		gui.gDrawWindow.gFillPolygon(xPoints, yPoints, p);
		
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawPolyline node, Object data) {
		Vector x = (Vector) node.jjtGetChild(0).jjtAccept(this, data);
		Vector y = (Vector) node.jjtGetChild(1).jjtAccept(this, data);
		int p = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).intValue();
		
		int[] xPoints = new int[x.size()];
		int[] yPoints = new int[y.size()];

		for( int i = 0; i < x.size(); i++){
			xPoints[i] = Double.valueOf(x.get(i).toString()).intValue();
		}
		for( int i = 0; i < y.size(); i++){
			yPoints[i] = Double.valueOf(y.get(i).toString()).intValue();
		}
		
		run_flag(node.line_num1, true);
		
		gui.gDrawWindow.gDrawPolyline(xPoints, yPoints, p);
		
		return null;
	}

	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgDrawImage node, Object data) {
		String fname = node.jjtGetChild(0).jjtAccept(this, data).toString();
		int x = Double.valueOf(node.jjtGetChild(1).jjtAccept(this, data).toString()).intValue();
		int y = Double.valueOf(node.jjtGetChild(2).jjtAccept(this, data).toString()).intValue();
		
		run_flag(node.line_num1, true);
		
		if( node.jjtGetNumChildren() < 4 ){
			gui.gDrawWindow.gDrawImage(fname, x, y);
		} else {
			int w = Double.valueOf(node.jjtGetChild(3).jjtAccept(this, data).toString()).intValue();
			int h = Double.valueOf(node.jjtGetChild(4).jjtAccept(this, data).toString()).intValue();
			gui.gDrawWindow.gDrawImage(fname, x, y, w, h);
		}
		
		return null;
	}
	
	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgSetRepaintFlag node, Object data) {
		boolean flag = Boolean.parseBoolean(node.jjtGetChild(0).jjtAccept(this, data).toString());

		run_flag(node.line_num1, true);
		
		gui.gDrawWindow.setRepaintFlag(flag);
		
		return null;
	}
	
	/**
	 * IntVgOutputWindowクラス参照
	 * @see IntVgOutputWindow
	 */
	public Object visit(ASTgRepaint node, Object data) {
		run_flag(node.line_num1, true);
		
		gui.gDrawWindow.repaint();
		
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_openr node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Integer ID = IO.openRead(i.toString());
		
		return ID;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_openw node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Integer ID = IO.openWrite(i.toString());
		
		return ID;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_opena node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Integer ID = IO.openAppend(i.toString());
		
		return ID;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_close node, Object data) {
		Integer ID = (Integer) node.jjtGetChild(0).jjtAccept(this, data);
		run_flag(node.line_num1, true);
		IO.closeFile(ID);
		
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_getstr node, Object data) {
		Integer ID = (Integer) node.jjtGetChild(0).jjtAccept(this, data);
		Integer n = (Integer) node.jjtGetChild(1).jjtAccept(this, data);
		
		return IO.getStr(this, ID, n);
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_getline node, Object data) {
		Integer ID = (Integer) node.jjtGetChild(0).jjtAccept(this, data);
		
		return IO.getLine(this, ID);
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_putstr node, Object data) {
		Integer ID = (Integer) node.jjtGetChild(0).jjtAccept(this, data);
		Object str = node.jjtGetChild(1).jjtAccept(this, data);
		run_flag(node.line_num1, true);
		IO.putString(ID, str.toString());
		
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_putline node, Object data) {
		Integer ID = (Integer) node.jjtGetChild(0).jjtAccept(this, data);
		Object str = node.jjtGetChild(1).jjtAccept(this, data);
		run_flag(node.line_num1, true);
		IO.putLine(ID, str.toString());
		
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_flush node, Object data) {
		Integer ID = (Integer) node.jjtGetChild(0).jjtAccept(this, data);
		run_flag(node.line_num1, true);
		IO.flush(ID);
		
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_isfile node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		String flag = IO.isFile(i.toString());
		
		return flag;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_rename node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		Object j = node.jjtGetChild(1).jjtAccept(this, data);
		run_flag(node.line_num1, true);
		IO.rename(i.toString(), j.toString());
		
		return null;
	}

	/**
	 * IntVFileIOクラス参照
	 * @see IntVFileIO
	 */
	public Object visit(ASTFile_remove node, Object data) {
		Object i = node.jjtGetChild(0).jjtAccept(this, data);
		run_flag(node.line_num1, true);
		IO.remove(i.toString());
		
		return null;
	}
	
	/**
	 * sleep() : プログラムの一時停止
	 */
	public Object visit(ASTSleep node, Object data) {
		run_flag(node.line_num1, true);
		int wait = Double.valueOf(node.jjtGetChild(0).jjtAccept(this, data).toString()).intValue();
		
		if(wait > 0) {
			mysleep((long)wait);
		} else {
			gui.consoleAppend.appendAll("### " + node.line_num1 + "行目：sleep(" + wait + ")\n");
			gui.consoleAppend.appendAll("### 引数の値が0以下です\n");
			runBreak(true);
		}
		
		return null;
	}

	/**
	 * 整数を表すノードで整数を返す
	 */
	public Object visit(ASTLiteral node, Object data) {
		return node.litValue;
	}

	/**
	 * 実数を表すノードで実数を返す
	 */
	public Object visit(ASTFloatLiteral node, Object data) {
		return node.litValue;
	}

	/**
	 * 「真」を返す
	 */
	public Object visit(ASTTrue node, Object data) {
		return new Boolean(true);
	}
	
	/**
	 * 「偽」を返す
	 */
	public Object visit(ASTFalse node, Object data) {
		return new Boolean(false);
	}
	
	/**
	 * 文字列を表すノードで文字列を返す
	 */
	public Object visit(ASTStrlit node, Object data) {
		return node.litString;
	}

	/**
	 * EOF
	 */
	public Object visit(ASTEXTRA_STR node, Object data) {
		String str = Character.toString(node.char_code[0]);
		if(node.char_code[1] != 0x0000){
			str = str + Character.toString(node.char_code[1]);
		}
		return new String(str);
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
		if(FunctionTable.containsKey(varName)) {
			Queue callByRef = new Queue();
			Object[] obj = (Object[]) FunctionTable.get(varName);
			ASTFunction fc	= (ASTFunction) obj[0];
			Object fc_data	= obj[1];
			int x, y, k, i;
	
			run_flag(node.line_num1, true);
			node.line_num1 = gui.run_point.getLineCount();
	
			k = node.jjtGetNumChildren();
			for(i = 0; i < k; i++){
				Object vartmp = node.jjtGetChild(i).jjtAccept(this, data);
				if( vartmp instanceof Vector){
					callByRef.push(((ASTIdent)(node.jjtGetChild(i))).varName);
				}
				callVar.push(vartmp);
			//	callVar.push(symTable.get(((ASTIdent)node.jjtGetChild(i)).varName));
			}
			
			stacksymTable.push(symTable.clone());
			stackTableNoTable.push(TableNoTable.clone());
			
			symTable.clear();
			TableNoTable.clear();
			
			Object[] temp_table = gui.vt_model.getDataVector().toArray();
			mysleep(15);
			
			gui.vt_model.setRowCount(0);
			
			run_flag(fc.line_num1, true);
			
			Object return_var =null;
			k = fc.jjtGetNumChildren();
			int runNode = 0;
			if( k > 1) {
				fc.jjtGetChild(0).jjtAccept(this, fc_data);
				runNode = 1;
			}
			callVar.clear();
			return_var = fc.jjtGetChild(runNode).jjtAccept(this, fc_data);
			
			if(return_var == null) {
				run_flag(fc.line_num2, true);
			} else if(return_var instanceof ASTReturn) {
				return_var = ((ASTReturn) return_var).returnValue;
			}
	
			symTable	= (Hashtable) stacksymTable.pop();
			TableNoTable	= (Hashtable) stackTableNoTable.pop();
			
			gui.vt_model.setRowCount(0);
	
			for(i = 0; i < temp_table.length; i++){
				gui.vt_model.addRow((Vector)temp_table[i]);
			}
	
			for(i = 0; i < callByRef.size(); i++){
				String nametmp = (String) callByRef.pop();
				callByReference((Vector) symTable.get(nametmp), nametmp + "[");
			}
			
			run_flag(node.line_num1, true);
			
			if(return_var instanceof Vector){
				return (Vector) return_var;
			} else if(fc.decl == PenProperties.DECLARATION_INT){
				return Double.valueOf(return_var.toString()).intValue();
			} else if(fc.decl == PenProperties.DECLARATION_DOUBLE){
				return Double.valueOf(return_var.toString());
			} else if(fc.decl == PenProperties.DECLARATION_STRING){
				return return_var.toString();
			} else if(fc.decl == PenProperties.DECLARATION_BOOLEAN){
				return Boolean.valueOf(return_var.toString());
			} else {
				return null;
			}
		} else if(gui.penPlugin.containsMethod(varName)){
			int k = node.jjtGetNumChildren();
			Class[] c;
			Object[] o;
			if( k == 0){
				c = null;
				o = null;
			} else {
				c = new Class[k];
				o = new Object[k];
				for(int i = 0; i < k; i++){
					Object vartmp = node.jjtGetChild(i).jjtAccept(this, data);
					if( vartmp instanceof Integer){
						c[i] = int.class;
					} else if( vartmp instanceof Double){
						c[i] = double.class;
					} else if( vartmp instanceof String){
						c[i] = String.class;
					} else if( vartmp instanceof Boolean){
						c[i] = boolean.class;
					}
					o[i] = vartmp;
				}
			}
			run_flag(node.line_num1, true);
			return gui.penPlugin.runMethood(varName, c, o);
		} else {
			gui.consoleAppend.appendAll("### "  + gui.run_point.getLineCount() + "行目の\""+ varName + "\"という関数名は存在しません。\n");
			runBreak(true);
			return null;
		}
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
		for(int i = 0; i < vec.size(); i++){
			Object ver = vec.get(i);
			
			if( ver instanceof Vector){
				callByReference( (Vector) ver, var_name + i + ",");
			} else if(ver != null) {
				String name = var_name + i + "]";
				Integer TableNo = (Integer)TableNoTable.get(name);
				gui.vt_model.setValueAt(ver.toString(),TableNo.intValue(),2);
			}
		}
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
		if(!gui.Flags.RunFlag){ runBreak(false); }
		if( num > 0 ){ run_num(num); }
		if(mysleep_flag){
			double i = gui.run_time.getValue();
			long msec = (long) (Math.pow(i / 1000, 2) * 500);
			if(!gui.Flags.StepFlag && msec > 0) { mysleep(msec); }
			if(gui.Flags.StepFlag && flag) { gui.RunButton.MySuspend(); }
			while(gui.Flags.SuspendFlag){ mysleep(100); }
			flag = true;
		}
		if(!gui.Flags.RunFlag){ runBreak(false); }
	}

	/**
	 * プログラムの実行を一時停止するためのメソッド
	 * 
	 * @param sleep_msec
	 * 実行を停止させてい時間 (ミリ秒)
	 */
	public synchronized void mysleep(long sleep_msec) {
		try{
			wait(sleep_msec);
		}catch(InterruptedException e){ }
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
		int num = gui.run_point.getLineCount();
		
		if(run > num){
			for(int i = num; i < run; i++){
				gui.run_point.insert("\n", 0);
			}
		}else if(run < num){
			for(int i = run; i < num; i++){
				gui.run_point.replaceRange("",0,1);
			}
		}
	}

	/**
	 * コンソール画面からの入力を処理するためのメソッド
	 * 
	 * @return
	 * コンソール画面に入力された文字列
	 */
	public Object input_wait(){
		gui.run_print.input();
		gui.console_tab.setSelectedIndex(0);
		
		JTextArea console = gui.consoleAppend.getTextArea(ConsoleAppend.CONSOLE);
		
		console.setCaretPosition(console.getText().length());
		console.requestFocusInWindow();
		
		gui.Flags.InputFlag = true;
		
		ConsoleKeyListener ckl = (ConsoleKeyListener) console.getKeyListeners()[0];

		try {
			int get_line	= console.getLineCount();
			ckl.setStartOffset(console.getLineEndOffset(get_line - 1));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		console.setBackground(new Color(255,255,255));
		console.setEditable(true);
		while(gui.Flags.InputFlag){
			mysleep(100);
		}
		console.setEditable(false);
		console.setBackground(new Color(240, 240, 240));
		if(!gui.Flags.RunFlag){ runBreak(false); }

		gui.consoleAppend.append(ckl.getInputLine() + "\n", ConsoleAppend.CONSOLE_LOG);
		
		if(gui.Flags.StepFlag || gui.Flags.SuspendFlag){
			gui.run_print.stop();
		} else {
			gui.run_print.run();
		}
		
		String input_data = ckl.getInputLine();
		if(input_data.matches("-?\\d+?")){
			return new Long(input_data);
		} else if(input_data.matches("-?\\d+?.\\d+?")){
			return new Double(input_data);
		} else if(input_data.toLowerCase().matches("true") || input_data.matches("真")){
			return new Boolean(true);
		} else if(input_data.toLowerCase().matches("false") || input_data.matches("偽")){
			return new Boolean(false);
		} else {
			return input_data;
		}
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
		String[] stat_data = {null, varName ,in.toString()};
		if(in instanceof String){
			stat_data[0] = "文字列";
		} else if(in instanceof Boolean){
			stat_data[0] = "真偽";
		} else if(in instanceof Double || gui.penPro.getProperty(PenProperties.EXECUTER_VAR_DECLARATION).equals("3")){
			stat_data[0] = "実数";
			in = Double.valueOf(in.toString());
			stat_data[2] = in.toString();
		} else if(in instanceof Long){
			stat_data[0] = "長整数";
		} else if(in instanceof Integer){
			stat_data[0] = "整数";
		}
		TableNoTable.put(varName,new Integer(gui.vt_model.getRowCount()));
		
		symTable.put(varName, in);
		gui.vt_model.addRow(stat_data);
		
		if(varError){
			gui.consoleAppend.appendAll("### "  + gui.run_point.getLineCount()
					+ "行目で宣言されていない変数 "+ varName +" が使用されましたが、"
					+ stat_data[0] + "とみなして実行を継続します\n\n");
		}
	}
	
	/**
	 * 実行停止ボタンを押すことによって呼び出されるメソッド
	 */
	public void runBreak(boolean flag){
		gui.Flags.RunFlag = flag;
		IO.closeFileAll();

		throw new ThreadRunStop();
	}
}
