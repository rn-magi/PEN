import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.mozilla.universalchardet.UniversalDetector;

public class FileOpen {
	private JFileChooser file_c;
	private PenFrame window;
	private JTextArea edit_area;
	private String WindowName;
	
	private String[] obj = {"ファイルを開く前に保存しますか？"};
	private String[] option = { "保存", "破棄", "取り消し" };
	
	private String file_name = "";
	
	private FileOpen file_open;
	
	MainGUI gui;
	
	public FileOpen(MainGUI gui){
		this.gui	= gui;
		file_c		= gui.fc;
		window		= gui.main_window;
		edit_area	= gui.edit_area;
		WindowName	= gui.WindowName;
	}
	
	public boolean FileOpenConfirm() {
		int retValue;
		String window_name = window.getTitle();
		if(window_name.substring(0, 1).equals("*")) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			retValue = JOptionPane.showOptionDialog((JFrame)window, obj,"", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null,option,option[0]);
			
			if(retValue==JOptionPane.YES_OPTION){
				int returnVal = gui.fc.showSaveDialog((JFrame)window);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file_name = new FileSave(gui.fc.getSelectedFile(), edit_area, gui.fc, window).getFileName();
					if(file_name != null){
						return true;
					}
				}else{
				}
			}else if(retValue==JOptionPane.NO_OPTION){
				return true;
			}else if(retValue==JOptionPane.CANCEL_OPTION){ }
		}else{
			return true;
		}
		return false;
	}
	

	
	public void FileChooser(){
		int retValue;
		retValue = file_c.showOpenDialog((JFrame)window);
		
		if (retValue == JFileChooser.APPROVE_OPTION) {
			FileOpenToEditArea(file_c.getSelectedFile());
		}else{
			if(!file_name.equals("")){
				new RunClean(gui);
				window.setTitle(file_name + " - " + WindowName);
			}
		}
	}
	
	public void FileOpenToEditArea(File file){
		String code = getCharSet(file);
		
		try{
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), code);
			BufferedReader reader = new BufferedReader(isr);
			String read = "";
			String add	= "";
			while(true){
				read = reader.readLine();
				if(read != null){
					add += read + "\n";
				}else{
					reader.close();
					break;
				}
			}
			new RunClean(gui);
			edit_area.setText(add);
			edit_area.requestFocus();
			window.setTitle(file.getName() + " - " + WindowName);
		}catch (FileNotFoundException ex){
			String messege = "ファイル \"" + file.getName() + "\" が見つかりません";
			JOptionPane.showMessageDialog(null, messege, "エラー", JOptionPane.ERROR_MESSAGE);
		}catch (IOException ex){
		}catch (ClassCastException ex){
		}
	}
	
	public String getCharSet(File file) {
		if(gui.penPro.containsKey(PenProperties.PEN_SYSTEM_CODE)){
			return gui.penPro.getProperty(PenProperties.PEN_SYSTEM_CODE);
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
}
