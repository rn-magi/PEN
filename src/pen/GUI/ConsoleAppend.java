import java.util.Vector;

import javax.swing.JTextArea;

/**
 * コンソール画面の「実行画面」と「履歴」に文字列を追加するクラス
 * 
 * @author Ryota Nakamura
 */
public class ConsoleAppend {
	
	/**
	 * console 画面のID
	 */
	public final static int CONSOLE = 0;
	
	/**
	 * console_log 画面のID
	 */
	public final static int CONSOLE_LOG = 1;
	
	private Vector vecTextArea = new Vector();

	/**
	 * 空の ConsoleAppend クラスを生成します
	 */
	public ConsoleAppend(){
	}
	
	/**
	 * 複数の要素( JTextArea )を格納した ConsoleAppend クラスを生成します
	 * 
	 * @param textArea
	 * 格納する複数の JTextArea クラス
	 */
	public ConsoleAppend(Object textArea[]){
		for(int i = 0; i < textArea.length; i++){
			addTextArea((JTextArea) textArea[i]);
		}
	}

	/**
	 * JTextArea を格納します
	 * 
	 * @param textArea
	 * 格納する要素
	 * @return
	 * 現在の要素数を返します
	 */
	public int addTextArea(JTextArea textArea){
		vecTextArea.add(textArea);
		
		return(vecTextArea.size()-1);
	}
	
	/**
	 * 指定された位置にある要素に文字列を追加します
	 * 
	 * @param str
	 * 指定する要素に追加する文字列
	 * @param number
	 * 文字列を追加する要素のインデックス
	 */
	public void append(String str, int number){
		JTextArea textArea = getTextArea(number);
		textArea.append(str);
		textArea.setCaretPosition(textArea.getText().length());
	}
	
	/**
	 * すべての要素に文字列を追加します
	 * 
	 * @param str
	 * 追加する文字列
	 */
	public void appendAll(String str){
		for( int i = 0; i < vecTextArea.size(); i++){
			append(str, i);
		}
	}
	
	/**
	 * すべての要素に文字列を追加します
	 * 
	 * @param str
	 * 追加する文字列
	 * @param error
	 * エラー番号
	 */
	public void appendAll(String str, int error){
		appendAll(str);
		
		switch(error) {
			default:
				break;
		}
	}
	
	/**
	 * 指定された位置にある要素を初期化します
	 * 
	 * @param number
	 * 初期化する要素のインデックス
	 */
	public void clean(int number){
		JTextArea textArea = getTextArea(number);
		textArea.setText("");
	}
	
	/**
	 * すべての要素を初期化します
	 */
	public void cleanAll(){
		for( int i = 0; i < vecTextArea.size(); i++){
			clean(i);
		}
	}
	
	/**
	 * 指定された位置にある要素を返します
	 * 
	 * @param number
	 * 返される要素のインデックス
	 * @return
	 * 指定されたインデックスにあるオブジェクト
	 */
	public JTextArea getTextArea(int number){
		return (JTextArea) vecTextArea.get(number);
	}
	
	/**
	 * 要素のインデックスを返します
	 * 
	 * @param textArea
	 * 返されるインデックスの要素
	 * @return
	 * 要素のインデックス
	 */
	public int getNumber(JTextArea textArea){
		for( int i = 0; i < vecTextArea.size(); i++){
			if(textArea.equals(vecTextArea.get(i))){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 要素数を返します
	 * 
	 * @return
	 * 要素数
	 */
	public int getSize(){
		return vecTextArea.size();
	}
}
