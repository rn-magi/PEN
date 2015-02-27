package pen.GUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class FileSave {
	private String file_name;
	
	private String[] obj = {"既にファイルがあります、上書きしますか？"};
	private String[] option = { "上書き", "別名で保存", "取り消し" };
	
	public FileSave(File file, JTextArea ed){
		saveData(setFilePath(file), ed);
	}

	public FileSave(File file, JTextArea ed, JFileChooser fc, MyJFrame mw){
		int retValue;
		String file_path = setFilePath(file);
		if(new File(file_path).canRead()){			//file_pathを読み込めるかどうか
			retValue = JOptionPane.showOptionDialog(null, obj,"", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null,option,option[0]);
			if(retValue==JOptionPane.YES_OPTION){
				saveData(file_path, ed);
			}else if(retValue==JOptionPane.NO_OPTION){
				retValue = fc.showSaveDialog(mw);
				if (retValue == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					file_name = new FileSave(file, ed, fc, mw).getFileName();
				}
			}else if(retValue==JOptionPane.CANCEL_OPTION){
			}
		}else{
			saveData(file_path, ed);
		}
	}
	
	public String setFilePath(File file){
		String ext = "";
		
		file_name = file.getName();
		int i = file_name.lastIndexOf('.');
		if (i > 0 &&  i < file_name.length() - 1) {
			ext = file_name.substring(i + 1).toLowerCase();
		}
		if (!ext.equals("pen") || ext == ""){
			file_name += ".pen";
		}

		return(file.getParent() + System.getProperty("file.separator") + file_name);
	}
	
	public void saveData(String file_path, JTextArea ed){
		try{
			FileWriter writer = new FileWriter(file_path);
			ed.write(writer);
			writer.close();
		}catch (FileNotFoundException ex){
			System.out.println(ex);
		}catch (IOException ex){
			System.out.println(ex);
		}
	}
	
	public String getFileName(){
		return file_name;
	}
}
