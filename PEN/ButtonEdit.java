import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/**
 * 入力支援ボタン定義ファイルから入力支援ボタンを配置するためのクラス
 * 
 * @author Ryota Nakamura
 */
public class ButtonEdit {
	MainGUI gui;
	JPanel edit_area_panel		= new JPanel();
	JPanel edit_button_area_panel		= new JPanel();

	/**
	 * <code>edit_button_area_layout</code> のコメント
	 * このインスタンスに生成された入力支援ボタンが格納される
	 */
	JToolBar edit_button_area_toolbar	= new JToolBar();
	 
	GridBagLayout edit_button_area_layout	= new GridBagLayout();
	GridBagConstraints gbc			= new GridBagConstraints();	

	/**
	 * 入力支援ボタンを格納するツールバーの初期設定を決める
	 * 
	 * @param ListFile
	 * 入力支援ボタン定義ファイルの絶対パス
	 * @throws UnsupportedEncodingException 
	 */
	public ButtonEdit(String ListFile, MainGUI gui) throws UnsupportedEncodingException{
		this.gui = gui;
		edit_button_area_toolbar.setLayout(new BoxLayout(edit_button_area_toolbar,BoxLayout.Y_AXIS));
		edit_button_area_toolbar.setMaximumSize(new Dimension(550, 550));
		edit_button_area_panel.setLayout(edit_button_area_layout);
		gbc.insets = new Insets(0, 0, 0, 0);
		
		if (gui.main_window instanceof JFrame) {
			try {
				File file = new File(ListFile);
				setEditButton(new BufferedReader(new InputStreamReader(new FileInputStream(file), "JISAutoDetect")));
			} catch (FileNotFoundException e) {
				System.out.println(e);
				InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream(PenProperties.BUTTON_LIST_FILE), "JISAutoDetect");
				setEditButton(new BufferedReader(isr));
			}
		} else {
			try {
				setEditButton(new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(ListFile), "JISAutoDetect")));
			} catch (IOException e) {
			}
		}
	}

	/**
	 * デフォルトの入力支援ボタンの配置
	 */
/*	public void EditButtonDefault(){
		int y = 0;
		for(int i = 0; i < edit_button.length; i++){
			EditButton eb = new EditButton(edit_button[i]);
			gbc.gridx = y % 16;
			gbc.gridy = y / 16 + 1;
			gbc.gridwidth = edit_button[i].Width / 32;
			gbc.gridheight = 1;
			y = y + edit_button[i].Width / 32;
			edit_button_area_layout.setConstraints(eb, gbc);
			edit_button_area_panel.add(eb);
		}
		edit_button_area_toolbar.add(new MyJLabel("（プログラム入力支援ボタン）"));
		edit_button_area_toolbar.add(edit_button_area_panel);
	}
*/

	public void setEditButton(BufferedReader edit_reader){
		for(int i = 0; i < 48; i++){
			JLabel nu = new JLabel();
			Dimension size = new Dimension(10 + i % 2, 0);
			nu.setPreferredSize(size);
			gbc.gridx = i;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			edit_button_area_layout.setConstraints(nu, gbc);
			edit_button_area_panel.add(nu);
		}
		
		try{
			int y = 0;
			String edit_read;
			Hashtable PopupWindows = new Hashtable();

			edit_read = edit_reader.readLine();
			
			while(true){
				
				if(edit_read == null){
					edit_reader.close();
					break;
				} else if(edit_read.equals("") || edit_read.equals("@@MainButton")){
					
					edit_read = edit_reader.readLine();
					
				} else if(edit_read.startsWith("@@")){
					GridBagLayout PopupLayout	= new GridBagLayout();
					GridBagConstraints PopupGBC	= new GridBagConstraints();
					JPanel PopupPanel		= new JPanel();
					int yy = 0;
					
					PopupGBC.insets = new Insets(0, 0, 0, 0);
					
					String[] split	= edit_read.split("@");
					String PopupWindowName	= split[2];
					int PopupWindowWidth	= new Integer(split[3]).intValue();

					JPopupMenu PopupWindow	= new JPopupMenu();
					
					for(int i = 0; i < PopupWindowWidth; i++){
						JLabel NullLabel = new JLabel();
						NullLabel.setPreferredSize(new Dimension(10 + i % 2, 0));
						PopupGBC.gridx = i;
						PopupGBC.gridy = 0;
						PopupGBC.gridwidth = 1;
						PopupGBC.gridheight = 1;
						PopupLayout.setConstraints(NullLabel, PopupGBC);
						PopupPanel.add(NullLabel);
					}
					
					PopupPanel.setLayout(PopupLayout);
					
					PopupGBC.gridx = 0;
					PopupGBC.gridy = 0;
					PopupGBC.gridwidth = PopupWindowWidth;
					PopupGBC.gridheight = 1;
					yy = yy + PopupWindowWidth;
					
					MyJLabel WindowName = new MyJLabel(PopupWindowName,new Dimension((int) Math.round(PopupWindowWidth * 10.83),25));
					PopupLayout.setConstraints(WindowName, PopupGBC);
					PopupPanel.add(WindowName);

					while(true){
						edit_read = edit_reader.readLine();
						
						if(edit_read.equals("") || edit_read.startsWith("@@")){
							PopupPanel.setSize(new Dimension(PopupWindowWidth*11,PopupGBC.gridy*25+5));
							PopupWindow.setPopupSize(PopupPanel.getSize());
							PopupWindow.add(PopupPanel);
							PopupWindows.put(PopupWindowName, PopupWindow);
							
							break;
						} else {
							EditButtonList ebl = new EditButtonList(edit_read);
							
							if( yy / PopupWindowWidth != (yy + ebl.TextWidth - 1) / PopupWindowWidth){
								yy = ( yy / PopupWindowWidth + 1) * PopupWindowWidth;
								PopupGBC.gridx = yy % PopupWindowWidth;
								PopupGBC.gridy = ( yy + ebl.TextWidth ) / PopupWindowWidth;
								yy = (PopupGBC.gridy - 1) * PopupWindowWidth;
							} else {
								PopupGBC.gridx = yy % PopupWindowWidth;
								PopupGBC.gridy = yy / PopupWindowWidth + 1;
							}
							PopupGBC.gridwidth = ebl.TextWidth;
							PopupGBC.gridheight = 1;
							yy = yy + ebl.TextWidth;
							
							if( !ebl.TipText.equals("") ) {
								EditButton eb = new EditButton(ebl, gui);
								PopupLayout.setConstraints(eb, PopupGBC);
								PopupPanel.add(eb);
							} else {
								MyJLabel eb = new MyJLabel(ebl);
								PopupLayout.setConstraints(eb, PopupGBC);
								PopupPanel.add(eb);
							}
						}
					}
				} else {
					EditButtonList ebl = new EditButtonList(edit_read);
					if( y / 48 != (y + ebl.TextWidth - 1) / 48){
						y = ( y / 48 + 1) * 48;
						gbc.gridx = y % 48;
						gbc.gridy = ( y + ebl.TextWidth ) / 48;
						y = (gbc.gridy - 1) * 48;
					} else {
						gbc.gridx = y % 48;
						gbc.gridy = y / 48 + 1;
					}
					gbc.gridwidth = ebl.TextWidth;
					gbc.gridheight = 1;
					y = y + ebl.TextWidth;
	
					if( !ebl.TipText.equals("") ) {
						EditButton eb;
						if(PopupWindows.containsKey(ebl.ButtonText)){
							eb = new EditButton(ebl, gui, (JPopupMenu) PopupWindows.get(ebl.ButtonText));
						} else {
							eb = new EditButton(ebl, gui);
						}
						edit_button_area_layout.setConstraints(eb, gbc);
						edit_button_area_panel.add(eb);
					} else {
						MyJLabel eb = new MyJLabel(ebl);
						edit_button_area_layout.setConstraints(eb, gbc);
						edit_button_area_panel.add(eb);
					}
					
					edit_read = edit_reader.readLine();
				}
			}
		//	edit_button_area_toolbar.add(new MyJLabel("プログラム入力支援ボタン"));
			edit_button_area_toolbar.add(edit_button_area_panel);
		} catch (Exception ex){
			System.out.println(ex);
		}
	}
}
