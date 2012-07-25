import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.MissingResourceException;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/*
 * 作成日: 2007/01/15
 *
 */

public class ConfigButtonListener implements ActionListener {
	private JScrollPane jsp;
	private String wn;

	public ConfigButtonListener(MainGUI gui, String windowName){
		wn = windowName;

		String[] columnNames = {
				LocaleProperty.getString(LocaleProperty.CONFIG_LABEL_NAME),
				LocaleProperty.getString(LocaleProperty.CONFIG_LABEL_SET)
			};
		DefaultTableModel tableModel = new DefaultTableModel(columnNames,0);
		
		String[] localeNames = {
				LocaleProperty.getString(LocaleProperty.CONFIG_LABEL_Locale),
				LocaleProperty.getLocale().toString()
			};
		tableModel.addRow(localeNames);
		
		for(Enumeration e = gui.penPro.propertyNames(); e.hasMoreElements();) {
			String[] data = {null, null};
			String tmp	= (String) e.nextElement();
			try {
				data[0]	= LocaleProperty.getString(tmp + ".name");
				data[1]	= LocaleProperty.getString(tmp + "." + gui.penPro.getProperty(tmp));
				tableModel.addRow(data);
			} catch (MissingResourceException ex) {
				if(data[0] != null){
					data[1] = gui.penPro.getProperty(tmp);
					tableModel.addRow(data);
				}
			}
		}

		JTable table = new JTable(tableModel);
		table.setEnabled(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(35);
	//	table.getColumnModel().getColumn(1).setPreferredWidth(150);
		
		jsp = new JScrollPane(table);
		jsp.setPreferredSize(new Dimension(350,150));
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(null, jsp, wn, JOptionPane.INFORMATION_MESSAGE);
	}

}
