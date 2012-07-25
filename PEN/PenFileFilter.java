import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PenFileFilter extends FileFilter{
	private String extension , message;
	
	public PenFileFilter(String exten, String msg){
		extension = exten;
		message = msg;
	}
	
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String ext = "";
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 &&  i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}

		if (ext.equals(extension)){
			return true;
		} else {
			return false;
		}
	}
 
   public String getDescription() {
		return message;
	}
}
