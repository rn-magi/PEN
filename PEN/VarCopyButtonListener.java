import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;

public class VarCopyButtonListener implements ActionListener {
	private JTable var_table;
	
	public VarCopyButtonListener(JTable vt){
		var_table	= vt;
	}
	
	public void actionPerformed(ActionEvent e) {
		String var = "";
		for(int i = 0; i < var_table.getRowCount(); i++){
			var += var_table.getValueAt(i,0) + " : " + var_table.getValueAt(i,1) + " : " + var_table.getValueAt(i,2) + "\n";
		}
		new ClipBoard(var);
	}

}
