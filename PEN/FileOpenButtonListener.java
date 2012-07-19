import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileOpenButtonListener implements ActionListener{
	private FileOpen file_open;
	
	public FileOpenButtonListener(MainGUI gui){
		file_open = new FileOpen(gui);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(file_open.FileOpenConfirm()){
			file_open.FileChooser();
		}
	}
}
