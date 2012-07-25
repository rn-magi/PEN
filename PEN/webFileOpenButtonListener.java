import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class webFileOpenButtonListener implements ActionListener, TreeSelectionListener {
	private MainGUI gui;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("sample");
	private JTree tree = new JTree(root);
	private JScrollPane scrollPane = new JScrollPane();
	private JFrame frame = new JFrame();
	
	private final String FILE_OPEN		= "ファイルを開きますか？";
	private final String[] OPTION		= { "はい", "いいえ"};
	
	private final String LIST_ERROR	= "リストが開けませんでした";
	private final String FILE_ERROR	= "ファイルが開けませんでした";
	
	private final String FILE_EXTENSION = ".pen";
	
	public webFileOpenButtonListener(MainGUI gui){
		this.gui = gui;
		
		root.setAllowsChildren(true);
		tree.addTreeSelectionListener(this);
		
		scrollPane.getViewport().setView(tree);
		frame.add(scrollPane);
		frame.setTitle("プログラム");
		frame.setSize(200, 300);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			DefaultMutableTreeNode node = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(new URL(((WebMain)gui.main_window).getBase(), "list.php").openStream()));
			String read = "";
			while(true){
				read = in.readLine();
				if(read != null){
					if(read.substring(0,1).equals("+")){
						node = new DefaultMutableTreeNode(read.substring(1, read.length()));
						root.add(node);
					} else {
						node.add(new DefaultMutableTreeNode(read.substring(1, read.length())));
					}
				}else{
					in.close();
					break;
				}
			}
			frame.setVisible(true);
		} catch (MalformedURLException e1) {
		} catch (IOException e1) {
			JOptionPane.showOptionDialog((WebMain)gui.main_window, LIST_ERROR,"", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
		}
	}

	public void valueChanged(TreeSelectionEvent e) {
		TreePath path = tree.getSelectionPath();
		if (path != null){
			String p = "";
			int count = path.getPathCount();
			Object[] data = path.getPath();
			for (int i = 0 ; i < count ; i++){
				p += data[i];
				if(i < count - 1){
					p += "/";
				}
			}
			if(new File(p).getPath().endsWith(FILE_EXTENSION)){
				int retValue = JOptionPane.showOptionDialog((WebMain)gui.main_window, FILE_OPEN,"", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null,OPTION,OPTION[0]);
				if(retValue==JOptionPane.YES_OPTION){
					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(new URL(((WebMain)gui.main_window).getBase(), p).openStream(), "SJIS"));
						String read = "";
						String add	= "";
						while(true){
							read = in.readLine();
							if(read != null){
								add += read + "\n";
							}else{
								in.close();
								break;
							}
						}
						new RunClean(gui);
						gui.edit_area.setText(add);
						frame.setVisible(false);
					} catch (MalformedURLException e1) {
					} catch (IOException e1) {
						JOptionPane.showOptionDialog((WebMain)gui.main_window, FILE_ERROR,"", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
					}
				}
			}
		}
	}
}
