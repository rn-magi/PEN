import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JTable;
import javax.swing.JTextArea;

import java.util.Date;

/**
 * dumpファイル生成用クラス
 * 
 * @author Ryota Nakamura
 * @author Takeo Yamamoto
 */
public class ErrorSave {
	private String file_name = "";
	
	final String line_separator = System.getProperty("line.separator");
	final String file_separator = System.getProperty("file.separator");
	
	private JTable variable;
	private JTextArea console;
	private JTextArea edit;
	private JTextArea point;
	
	private String destDir	= "";
	private String destPath	= "";
	private String tempDir	= "";
	private String tempPath	= "";
	
	private String header = "";
	
	private boolean dump_flag = false;
	
	/**
	 * ErrorDump処理を行うための初期化および、
	 * システムの環境情報を取得します。
	 */
	public void environment(JTable variable, JTextArea console, JTextArea edit, JTextArea point){
		this.variable	= variable;
		this.console	= console;
		this.edit	= edit;
		this.point	= point;
		
		dump_flag = true;
		
		// Javaのversion情報取得
		String JavaVersion = System.getProperty("java.version");
		
		// OSのversion情報取得
		String OSVersion = System.getProperty("os.name");
		
		// ユーザーの名前取得
		String UserName = System.getProperty("user.name");
		
		// ホームディレクトリ
		String UserHome = System.getProperty("user.home");

		// PEN起動時の時間を格納
		long Time	= System.currentTimeMillis();	
		String Date	= new Date().toString();
		
		// HostName の取得
		String PcName = "";
		try {
			PcName = java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			PcName = "unknown";
			System.out.println(e);
		}

		file_name = UserName + "-" + PcName + "-" + Time + ".log";
		setTempDir(UserHome);
		
		header += "@TimeMillis:" + Time + line_separator;
		header += "@Time:" + Date + line_separator;
		header += "@JavaVersion:" + JavaVersion + line_separator;
		header += "@OS:" + OSVersion + line_separator;
		header += "@UserName:" + UserName + line_separator;		
		header += "@Computer:" + PcName + line_separator;		
	}
	
	/**
	 * エラー時の状態情報を取得し、テンポラリファイルに保存します
	 */
	public void state() {
		if(dump_flag){
			if(!new File(tempPath).canRead()){
				postScript(header);
			}
			
			String var = "";
			for(int i = 0; i < variable.getRowCount(); i++){
				var += variable.getValueAt(i,0) + ","
					+ variable.getValueAt(i,1) + ","
					+ variable.getValueAt(i,2)
					+ line_separator;
			}
			
			JTextArea dump = new JTextArea();
			dump.append("@--------------------------------\n");
			dump.append("@Time:" + System.currentTimeMillis() + "\n");
			dump.append("@Line:" + point.getLineCount() + "\n");
			dump.append("@Console:\n");
			dump.append(console.getText() + "\n");
			dump.append("@END\n");
			dump.append("@Variable:\n");
			dump.append(var);
			dump.append("@END\n");
			dump.append("@Program:\n");
			dump.append(edit.getText() + "\n");
			dump.append("@END\n");
			
			postScript(dump);
		}
	}
	
	/**	
	 * エラー情報をテンポラリファイルに追記します
	 * 
	 * @param str 保存する文字列
	 */
	public void postScript(String str){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempPath, true));
			bw.write(str);
			bw.flush();
			bw.close();
		}catch (FileNotFoundException e){
			System.out.println(e);
		}catch (IOException e){
			System.out.println(e);
		}
	}
	
	/**	
	 * エラー情報をテンポラリファイルに追記します
	 * 
	 * @param textarea 保存するJTextArea
	 */
	public void postScript(JTextArea textarea){
		try{
			FileWriter fw = new FileWriter(tempPath, true);
			textarea.write(fw);
			fw.flush();
			fw.close();
		}catch (FileNotFoundException e){
			System.out.println(e);
		}catch (IOException e){
			System.out.println(e);
		}
	}
	
	public void exit(){
		if(dump_flag && destPath != ""){
			File file = new File(tempPath);
			if(file.canRead()){
				file.renameTo(new File(destPath));
			}
		}
	}
	
	public void setDestDir(String path){
		destPath = path + file_separator + file_name;
	}
	
	public String getDestDir(){
		return destDir;
	}
	
	public String getDestPath(){
		return destPath;
	}
	
	public void setTempDir(String path){
		tempDir = path;
		tempPath = path + file_separator + file_name;
	}
	
	public String getTempDir(){
		return tempDir;
	}
	
	public String getTempPath(){
		return tempPath;
	}
}
