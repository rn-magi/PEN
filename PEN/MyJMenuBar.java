import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit.CopyAction;
import javax.swing.text.DefaultEditorKit.CutAction;
import javax.swing.text.DefaultEditorKit.PasteAction;

public class MyJMenuBar extends JMenuBar{
	MainGUI gui;
	private JFileChooser file_c;
	private PenFrame window;
	private JTextArea edit_area;
	private JTextArea console;
	private JTable var_table;
	private String WindowName;
	private String SystemName;
	private String Version;
	private EditAreaUndoableEditListener undo;
	
	private JMenuBar MenuBar = new JMenuBar();
	
	private MyJMenu FileMenu = new MyJMenu("ファイル");
	private MyJMenu EditMenu = new MyJMenu("編集");
	private MyJMenu HelpMenu = new MyJMenu("ヘルプ");
	
	private MyJMenuItem NewFileMenuItem		= new MyJMenuItem("新規");
	private MyJMenuItem FileOpenMenuItem		= new MyJMenuItem("開く");
//	private MyJMenuItem FileSaveMenuItem		= new MyJMenuItem("上書き保存");
	private MyJMenuItem FileReSaveMenuItem		= new MyJMenuItem("名前を付けて保存");
	private MyJMenuItem ExitMenuItem		= new MyJMenuItem("PENを終了する");
	
	private MyJMenuItem UndoMenuItem		= new MyJMenuItem("元に戻す");
	private MyJMenuItem RedoMenuItem		= new MyJMenuItem("やり直し");
	private MyJMenuItem CutMenuItem		= new MyJMenuItem("切り取り"	, new CutAction());
	private MyJMenuItem CopyMenuItem		= new MyJMenuItem("コピー"	, new CopyAction());
	private MyJMenuItem PasteMenuItem		= new MyJMenuItem("貼り付け"	, new PasteAction());
	private MyJMenuItem ConsoleCopyMenuItem	= new MyJMenuItem("実行画面をコピー");
	private MyJMenuItem VarCopyMenuItem		= new MyJMenuItem("変数表示画面をコピー");

	private MyJMenuItem ConfigMenu			= new MyJMenuItem("設定確認");
	private MyJMenuItem HelpPenMenuItem		= new MyJMenuItem("PENについて");
	
	public MyJMenuBar(MainGUI gui){
		this.gui		= gui;
		this.file_c		= gui.fc;
		this.window		= gui.main_window;
		this.edit_area		= gui.edit_area;
		this.console		= gui.console;
		this.var_table		= gui.var_table;
		this.SystemName		= gui.SystemName;
		this.Version		= gui.Version;
		this.WindowName		= gui.SystemName + " " + gui.Version;
		this.undo		= gui.undo;
	}
	
	public JMenuBar createMenuBar(){
		NewFileMenuItem.addActionListener(new NewFileButtonListener(gui));
		FileOpenMenuItem.addActionListener(new FileOpenButtonListener(gui));
		FileReSaveMenuItem.addActionListener(new FileSaveButtonListener(gui));
		ExitMenuItem.addActionListener(new ExitButtonListener(gui));
		
		UndoMenuItem.addActionListener(undo);
		RedoMenuItem.addActionListener(undo);
		
		ConsoleCopyMenuItem.addActionListener(new ConsoleCopyButtonListener(console));
		VarCopyMenuItem.addActionListener(new VarCopyButtonListener(var_table));

		ConfigMenu.addActionListener(new ConfigButtonListener(gui, ConfigMenu.getText()));
		HelpPenMenuItem.addActionListener(new HelpPenButtonListener(Version));
		
		FileMenu.add(NewFileMenuItem);
		FileMenu.add(FileOpenMenuItem);
		//FileMenu.add(FileSaveMenuItem);
		FileMenu.add(FileReSaveMenuItem);
		FileMenu.add(new JSeparator());
		FileMenu.add(ExitMenuItem);

		EditMenu.add(UndoMenuItem);
		EditMenu.add(RedoMenuItem);
		EditMenu.add(new JSeparator());
		EditMenu.add(CutMenuItem);
		EditMenu.add(CopyMenuItem);
		EditMenu.add(PasteMenuItem);
		EditMenu.add(new JSeparator());
		EditMenu.add(ConsoleCopyMenuItem);
		EditMenu.add(VarCopyMenuItem);

		HelpMenu.add(ConfigMenu);
		HelpMenu.add(new JSeparator());
		HelpMenu.add(HelpPenMenuItem);
		
		MenuBar.add(FileMenu);
		MenuBar.add(EditMenu);
		MenuBar.add(HelpMenu);

		return MenuBar;
	}

	public void MysetEnabled(boolean f){
		NewFileMenuItem.setEnabled(f);
		FileOpenMenuItem.setEnabled(f);
		FileReSaveMenuItem.setEnabled(f);
		ExitMenuItem.setEnabled(f);
		
		UndoMenuItem.setEnabled(f);
		RedoMenuItem.setEnabled(f);
		CutMenuItem.setEnabled(f);
		CopyMenuItem.setEnabled(f);
		PasteMenuItem.setEnabled(f);
		ConsoleCopyMenuItem.setEnabled(f);
		VarCopyMenuItem.setEnabled(f);
	}
	
	public void LogCopy(boolean f){
		ConsoleCopyMenuItem.setEnabled(f);
		VarCopyMenuItem.setEnabled(f);
	}
}
