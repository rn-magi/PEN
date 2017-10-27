import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.mozilla.universalchardet.UniversalDetector;

public class xDNCL2ArduinoFileDropOpen implements DropTargetListener {
	private JTextArea text;
	
	public xDNCL2ArduinoFileDropOpen(JTextArea text){
		this.text = text;
	}

	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void drop(DropTargetDropEvent dtde) {
		dtde.acceptDrop(dtde.getDropAction());
		try {
			Transferable trans = dtde.getTransferable();
			DataFlavor flavors[] = trans.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; ++i) {
				if (flavors[i].isFlavorJavaFileListType()) {
					List list = (List) trans.getTransferData(flavors[i]);
					
					File file = (File) list.get(0);

					try{
						InputStreamReader isr = new InputStreamReader(new FileInputStream(file), getCharSet(file));
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

						text.setText(add);
						text.requestFocus();
					}catch (FileNotFoundException ex){
						String messege = "ファイル \"" + file.getName() + "\" が見つかりません";
						JOptionPane.showMessageDialog(null, messege, "エラー", JOptionPane.ERROR_MESSAGE);
					}catch (IOException ex){
					}catch (ClassCastException ex){
					}
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
		} finally {
			dtde.dropComplete(true);
		}
	}

	public void dragExit(DropTargetEvent dte) {
	}
	
	public String getCharSet(File file) {
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
