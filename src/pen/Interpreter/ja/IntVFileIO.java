package pen.Interpreter.ja;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import org.mozilla.universalchardet.UniversalDetector;

import pen.GUI.ConsoleAppend;
import pen.GUI.MainGUI;
import pen.GUI.ThreadRunStop;

/**
 * FileI/O を管理/実装するクラス
 * 
 * @author Ryota Nakamura
 */
public class IntVFileIO {
	// 開いているファイルを管理するテーブル
	private Hashtable OpenFileTable = new Hashtable();
	
	// System に使用されている改行コードの取得
	private String LineSeparator =System.getProperty("line.separator");
	
	private MainGUI gui = null;
	private ConsoleAppend consoleAppend = null;
	
	private String CharCode = "";
	
	public IntVFileIO(){
		initialize();
	}
	
	/**
	 * @param gui
	 */
	public IntVFileIO(MainGUI gui){
		this.gui = gui;
		consoleAppend = gui.consoleAppend;

		initialize();
	}
	
	/**
	 * ファイルテーブルの初期化
	 */
	public void initialize(){
		OpenFileTable.put(new Integer(0),  new Object[]{"input", "標準入力"});
		OpenFileTable.put(new Integer(1),  new Object[]{"output", "標準出力"});
		OpenFileTable.put(new Integer(2),  new Object[]{"error", "エラー出力"});
	}
	
	/**
	 * @param FilePath
	 * @return FileID
	 */
	public Integer openRead(String FilePath){
		// 読み込みモード でファイルを開く
		// ファイルが存在しなかった場合、エラーを吐き停止
		
		File file = new File(FilePath);
		try {
			// ファイルテーブルに追加
			Integer ID = getFileID();
			
			String code = getCharSet(file);
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), code);
			BufferedReader reader = new BufferedReader(isr);
			
			OpenFileTable.put(ID, new Object[]{ "Read", reader });
			return ID;
		} catch (FileNotFoundException e) {
			// 指定されたパス名で示されるファイルが開けなかった時の処理
			// e.printStackTrace();
			exception(e.getLocalizedMessage());
			return null;
		} catch (UnsupportedEncodingException e) {
			// エンコード処理できなかった場合
			// e.printStackTrace();
			exception(e.getLocalizedMessage());
			return null;
		}
	}
	
	/**
	 * @param FilePath
	 * @return FileID
	 */
	public Integer openWrite(String FilePath){
		// 書き込みモード でファイルをオープンする
		// ファイルが存在した場合、オーバーライトする
		
		File file = new File(FilePath);
		try {
			// ファイルテーブルに追加
			Integer ID = getFileID();
			OpenFileTable.put(ID, new Object[]{ "Write", new BufferedWriter(new FileWriter(file))});
			return ID;
		} catch (IOException e) {
			// 入出力処理の失敗、または割り込みの発生
			// e.printStackTrace();
			exception(e.getLocalizedMessage());
			return null;
		}
	}
	
	/**
	 * @param FilePath
	 * @return FileID
	 */
	public Integer openAppend(String FilePath){
		// 追記モード でファイルをオープンする
		// ファイルが存在しなかった場合、ファイルを作成する
		
		File file = new File(FilePath);
		try {
			BufferedWriter bf;
			if( file.isFile() ) {
				// ファイルの内容を add に退避
				String code = getCharSet(file);
				InputStreamReader isr = new InputStreamReader(new FileInputStream(file), code);
				BufferedReader reader = new BufferedReader(isr);
				String read = "";
				String add	= "";
				while(true){
					read = reader.readLine();
					if(read != null){
						add += read + LineSeparator;
					}else{
						reader.close();
						break;
					}
				}
				
				// FileWriter でファイルを開き、退避させたデータの書き込み
				bf = new BufferedWriter(new FileWriter(file));
				bf.write(add);
				bf.flush();
			} else {
				bf = new BufferedWriter(new FileWriter(file));
			}
			// ファイルテーブルに追加
			Integer ID = getFileID();
			OpenFileTable.put(ID, new Object[]{ "Append", bf});
			return ID;
		} catch (UnsupportedEncodingException e) {
			// エンコード処理できなかった場合
			// e.printStackTrace();
			exception(e.getLocalizedMessage());
			return null;
		} catch (IOException e) {
			// 入出力処理の失敗、または割り込みの発生
			// e.printStackTrace();
			exception(e.getLocalizedMessage());
			return null;
		}
	}
	
	/**
	 * @param FilePath
	 * @return ファイルの有無
	 */
	public String isFile(String FilePath){
		// ファイルが存在する場合	: true
		// ファイルが存在しない場合	: false
		
		File file = new File(FilePath);
		Boolean flag = new Boolean(file.isFile());
		
		return flag.toString();
	}
	
	/**
	 * @param FilePath1
	 * @param FilePath2
	 */
	public void rename(String FilePath1, String FilePath2){
		// ファイルのリネーム
		
		File file1 = new File(FilePath1);
		File file2 = new File(FilePath2);
		if( file1.isFile() ){
			if( file2.isFile() ){
				// リネーム先にファイルが存在する場合
				exception("リネーム先にファイルが存在します");
			} else {
				if( file1.renameTo(file2) ){
					// ファイルのリネームが成功した場合
				} else {
					// ファイルのリネームが失敗した場合
					exception("ファイルのリネームが失敗しました");
				}
			}
		} else {
			// リネーム元のファイルが存在しなかった場合
			exception("リネーム元のファイルが存在しません");
		}
	}
	
	/**
	 * @param FilePath
	 */
	public void remove(String FilePath){
		// ファイルの削除
		
		File file = new File(FilePath);
		if( file.isFile() ){
			if( file.delete() ) {
				// ファイルの削除に成功した場合
			} else {
				// ファイルの削除が失敗した場合
				exception("ファイルの削除に失敗しました");
			}
		} else {
			// リネーム元のファイルが存在しなかった場合
			exception("削除するファイルが存在しません");
		}
	}
	
	/**
	 * @param ID
	 * @param n
	 * @return 読み込んだ文字列
	 */
	public String getStr(Integer ID, Integer n){
		// ID で指定されたファイルからデータを n文字 読み込む
		// Windows の場合、改行コードに注意 -> "\r\n" なので [ 2文字 ] として扱われる
		
		if( OpenFileTable.get(ID) != null){
			Object[] obj = (Object[]) OpenFileTable.get(ID);
			if( obj[0].equals("Read") ){
				BufferedReader bf = (BufferedReader) obj[1];
				int c;
				String str = "";
				try {
					// n文字分のデータ読み込み
					for( int i = 0; i < n.intValue(); i++) {
						c = bf.read();
						if( c != -1){
							str += new Character((char) c).toString();
						} else if( str.equals("")){
							return new String(new Character((char) c).toString());
						} else {
							break;
						}
					}
					return str;
				} catch (IOException e) {
					// 入出力処理の失敗、または割り込みの発生
					// e.printStackTrace();
					exception(e.getLocalizedMessage());
					return null;
				}
			} else if( obj[0].equals("input") ){
				String input = getInputKey();
				if( input.length() < n.intValue() ) {
					return input;
				}
				return input.substring(0, n.intValue());
			} else {
				// ファイル状態が [ Write ] なファイルを読み込もうとした時の処理
				exception("ファイルID [ " + ID + " ] は WriteOnly です");
				return null;
			}
		} else {
			// 指定されたIDが存在しない場合の処理
			exception("ファイルID [ " + ID + " ] は存在しません");
			return null;
		}
	}
	/**
	 * @param exe
	 * @param ID
	 * @param n
	 * @return 読み込んだ文字列
	 */
	public String getStr(IntVExecuter exe, Integer ID, Integer n){
		// ID で指定されたファイルからデータを n文字 読み込む
		// Windows の場合、改行コードに注意 -> "\r\n" なので [ 2文字 ] として扱われる
		
		if( OpenFileTable.get(ID) != null){
			Object[] obj = (Object[]) OpenFileTable.get(ID);
			if( obj[0].equals("Read") ){
				BufferedReader bf = (BufferedReader) obj[1];
				int c;
				String str = "";
				try {
					// n文字分のデータ読み込み
					for( int i = 0; i < n.intValue(); i++) {
						c = bf.read();
						if( c != -1){
							str += new Character((char) c).toString();
						} else if( str.equals("")){
							return new String(new Character((char) c).toString());
						} else {
							break;
						}
					}
					return str;
				} catch (IOException e) {
					// 入出力処理の失敗、または割り込みの発生
					// e.printStackTrace();
					exception(e.getLocalizedMessage());
					return null;
				}
			} else if( obj[0].equals("input") ){
				// コンソール画面から入力する処理をここに追加
				String input = exe.input_wait().toString();
				if( input.length() < n.intValue() ) {
					return input;
				}
				return input.substring(0, n.intValue());
			} else {
				// ファイル状態が [ Write ] なファイルを読み込もうとした時の処理
				exception("ファイルID [ " + ID + " ] は WriteOnly です");
				return null;
			}
		} else {
			// 指定されたIDが存在しない場合の処理
			exception("ファイルID [ " + ID + " ] は存在しません");
			return null;
		}
	}
	
	/**
	 * @param ID
	 * @return 読み込んだ文字列
	 */
	public String getLine(Integer ID){
		// ID で指定されたファイルからデータを1行読み込む
		// なお、改行コードは [ 付加してない ] 状態で返しています
		
		if( OpenFileTable.get(ID) != null){
			Object[] obj = (Object[]) OpenFileTable.get(ID);
			if( obj[0].equals("Read") ){
				BufferedReader bf = (BufferedReader) obj[1];
				try {
					// 一行分のデータを読み込み
					String str = bf.readLine();
					if( str == null ){
						int c = -1;
						return new String(new Character((char) c).toString());
					}
					return str;
				} catch (IOException e) {
					// 入出力処理の失敗、または割り込みの発生
					// e.printStackTrace();
					exception(e.getLocalizedMessage());
					return null;
				}
			} else if( obj[0].equals("input") ){
				return getInputKey();
			} else {
				// ファイル状態が [ Write ] なファイルを読み込もうとした時の処理
				exception("ファイルID [ " + ID + " ] は WriteOnly です");
				return null;
			}
		} else {
			// 指定されたIDが存在しない場合の処理
			exception("ファイルID [ " + ID + " ] は存在しません");
			return null;
		}
	}
	
	/**
	 * @param exe
	 * @param ID
	 * @return 読み込んだ文字列
	 */
	public String getLine(IntVExecuter exe, Integer ID){
		// ID で指定されたファイルからデータを1行読み込む
		// なお、改行コードは [ 付加してない ] 状態で返しています
		
		if( OpenFileTable.get(ID) != null){
			Object[] obj = (Object[]) OpenFileTable.get(ID);
			if( obj[0].equals("Read") ){
				BufferedReader bf = (BufferedReader) obj[1];
				try {
					// 一行分のデータを読み込み
					String str = bf.readLine();
					if( str == null ){
						int c = -1;
						return new String(new Character((char) c).toString());
					}
					return str;
				} catch (IOException e) {
					// 入出力処理の失敗、または割り込みの発生
					// e.printStackTrace();
					exception(e.getLocalizedMessage());
					return null;
				}
			} else if( obj[0].equals("input") ){
				// コンソール画面から入力する処理をここに追加
				return exe.input_wait().toString();
			} else {
				// ファイル状態が [ Write ] なファイルを読み込もうとした時の処理
				exception("ファイルID [ " + ID + " ] は WriteOnly です");
				return null;
			}
		} else {
			// 指定されたIDが存在しない場合の処理
			exception("ファイルID [ " + ID + " ] は存在しません");
			return null;
		}
	}
	
	/**
	 * @param ID
	 * @param str
	 */
	public void putLine(Integer ID, String str){
		// ID で指定されたファイルへ データ(str) に改行込みで書き込む
		
		if( OpenFileTable.get(ID) != null){
			Object[] obj = (Object[]) OpenFileTable.get(ID);
			if( obj[0].equals("Write") || obj[0].equals("Append") ){
				BufferedWriter bf = ( BufferedWriter ) obj[1];
				try {
					// ファイルへの書き込み
					bf.write(str);
					bf.newLine();
				} catch (IOException e) {
					// 入出力処理の失敗、または割り込みの発生
					// e.printStackTrace();
					exception(e.getLocalizedMessage());
				}
			} else if( obj[0].equals("output") ){
				// コンソール画面へ出力する処理
				printString(str + "\n");
			} else if( obj[0].equals("error") ){
				// コンソール画面へ出力する処理
				printString("### " + str + "\n");
			} else {
				// ファイル状態が [ Read ] なファイルに書き込もうとした時の処理
				exception("ファイルID [ " + ID + " ] は ReadOnly です");
			}
		} else {
			// 指定されたIDが存在しない場合の処理
			exception("ファイルID [ " + ID + " ] は存在しません");
		}
	}
	
	/**
	 * @param ID
	 * @param str
	 */
	public void putString(Integer ID, String str){
		// ID で指定されたファイルへ データ(str) を書き込む
		
		if( OpenFileTable.get(ID) != null){
			Object[] obj = (Object[]) OpenFileTable.get(ID);
			if( obj[0].equals("Write") || obj[0].equals("Append") ){
				BufferedWriter bf = ( BufferedWriter ) obj[1];
				try {
					// ファイルへの書き込み
					bf.write(str);
				} catch (IOException e) {
					// e.printStackTrace();
					exception(e.getLocalizedMessage());
					e.printStackTrace();
				}
			} else if( obj[0].equals("output") ){
				// コンソール画面へ出力する処理
				printString(str);
			} else if( obj[0].equals("error") ){
				// コンソール画面へ出力する処理
				printString("### " + str);
			} else {
				// ファイル状態が [ Read ] なファイルに書き込もうとした時の処理
				exception("ファイルID [ " + ID + " ] は ReadOnly です");
			}
		} else {
			// 指定されたIDが存在しない場合の処理
			exception("ファイルID [ " + ID + " ] は存在しません");
		}
	}
	
	/**
	 * @param ID
	 */
	public void flush(Integer ID){
		// ストリームをフラッシュ

		if( OpenFileTable.get(ID) != null){
			Object[] obj = (Object[]) OpenFileTable.get(ID);
			if( obj[0].equals("Write") || obj[0].equals("Append") ){
				BufferedWriter bf = ( BufferedWriter ) obj[1];
				try {
					// ストリームをフラッシュ
					bf.flush();
				} catch (IOException e) {
					// 入出力処理の失敗、または割り込みの発生
					// e.printStackTrace();
					exception(e.getLocalizedMessage());
				}
			} else {
				// ファイル状態が [ Read ] なファイルに書き込もうとした時の処理
				exception("ファイルID [ " + ID + " ] は ReadOnly です");
			}
		} else {
			// 指定されたIDが存在しない場合の処理
			exception("ファイルID [ " + ID + " ] は存在しません");
		}
	}
	
	/**
	 * @param ID
	 */
	public void closeFile(Integer ID){
		// ファイルのクローズ処理
		// FileIDRemove(ID) で閉じたファイルを FileTable から削除
		
		if( OpenFileTable.get(ID) != null){
			Object[] obj = (Object[]) OpenFileTable.get(ID);
			try {
				// ファイルのクローズ処理
				// ファイルテーブルからIDを削除
				if( obj[0].equals("Read") ){
					BufferedReader bf = ( BufferedReader ) obj[1];
					bf.close();
					removeFileID(ID);
				} else if( obj[0].equals("Write") || obj[0].equals("Append")){
					BufferedWriter bf = ( BufferedWriter ) obj[1];
					bf.flush();
					bf.close();
					removeFileID(ID);
				} else {
					removeFileID(ID);
				}
			} catch (IOException e) {
				// 入出力処理の失敗、または割り込みの発生
				// e.printStackTrace();
				exception(e.getLocalizedMessage());
			}
		} else {
			// 指定されたIDが存在しない場合の処理
			exception("ファイルID [ " + ID + " ] は存在しません");
		}
	}
	
	/**
	 * @return FileID
	 */
	public Integer getFileID(){
		// 空いている FileID 番号の若い順から調べる

		int i;
		for( i = 3; true; i++){
			if( OpenFileTable.get(new Integer(i)) == null ){
				break;
			}
		}
		return new Integer(i);
	}
	
	/**
	 * @param ID
	 */
	public void removeFileID(Integer ID){
		// FileTable から FileID を開放する処理

		OpenFileTable.remove(ID);
	}
	
	/**
	 * 
	 */
	public void closeFileAll(){
		// オープンされているファイルをすべて閉じる
		
		Object[] obj = OpenFileTable.keySet().toArray();
		
		for(int i = 0; i < obj.length; i++){
			Integer ID = ( Integer ) obj[i];
			closeFile(ID);
		}
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
		
		closeFileAll();
		throw new ThreadRunStop();
	}
	
	private String getInputKey(){
		int i;
                try {
                	BufferedReader r = new BufferedReader(new InputStreamReader(System.in), 1);
                	String s = r.readLine();
                	r.close();
                	return s;
                }
                catch (IOException err) {
                	exception(err.toString());
                	return null;
                }

	}
	
	private void printString(String str){
		if(consoleAppend == null){
			System.out.print(str);
			System.out.flush();
		} else {
			consoleAppend.appendAll(str);
		}
	}

	/**
	 * @param file
	 * @return CharCode
	 */
	private String getCharSet(File file) {
		if(!CharCode.equals("")){
			return CharCode;
		}

		try {
			byte[] buf = new byte[4096];
			FileInputStream fis = new FileInputStream(file);
			UniversalDetector detector = new UniversalDetector(null);
			int nread;
			while ((nread = fis.read(buf)) > 0 && ! detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			detector.dataEnd();
			fis.close();

			String encoding = detector.getDetectedCharset();
			detector.reset();
			if(encoding != null){
				return encoding;
			}
		}catch (Exception e){
		}
		return "JISAutoDetect";
	}
	
	public void setCharCode(String code){
		CharCode = code;
	}
}
