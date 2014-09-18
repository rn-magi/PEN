import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.TooManyListenersException;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * GUIの基礎部分です。
 * 
 * @author Ryota Nakamura
 *
 */
public class MainGUI {
	public String Version			= "ver1.20_13";
	public String SystemName		= "PEN";
	public String WindowName		= SystemName + " " + Version;
	public PenFrame main_window;
	public JPanel menu_panel		= new JPanel();
	public JPanel run_time_panel	= new JPanel();
	public JPanel edit_panel		= new JPanel();
	public JPanel edit_area_panel	= new JPanel();
	public JPanel console_panel		= new JPanel();
	public JPanel var_panel			= new JPanel();
	public JSplitPane main_splitpane;
	public JSplitPane run_splitpane;
	public JScrollPane edit_JSP;
	public JScrollPane console_JSP;
	public JScrollPane console_log_JSP;
	public JScrollPane var_JSP;

	public String PropertyFileName	= "Property.ini";
	public String FileSeparator		= System.getProperty("file.separator");

	public DropTarget edit_area_drop	= new DropTarget();
	
	public Font font				= new Font("Monospaced", 0, 14);
	public String ButtonListFile	= "";
	public Document edit_doc		= new PlainDocument();
	public EditAreaUndoableEditListener undo	= new EditAreaUndoableEditListener();
	
	public String[] columnNames		= {"型" , "変数名" , "値"};
	public int[] COLSIZE			= {50,80,95};
	
	public DefaultTableModel vt_model;
	public MyJMenuBar MenuBar;
	public MyRunJLabel run_print	= new MyRunJLabel();
	public MenuButton new_button	= new MenuButton("新規", 60, 30);
	public MenuButton open_button	= new MenuButton("開く", 60, 30);
	public MenuButton save_button	= new MenuButton("保存", 60, 30);
	public MenuButton run_button	= new MenuButton("実行", 90, 30);
	public MenuButton step_button	= new MenuButton("一行実行", 90, 30);
	public MenuButton stop_button	= new MenuButton("始めに戻る", 90, 30);
	public JTabbedPane console_tab	= new JTabbedPane();
	public JTextArea run_point		= new JTextArea(1,1);
	public JTextArea breakpoint		= new JTextArea(1,1);
	public JTextArea numbar_area	= new JTextArea(1,2);
	public JTextArea edit_area		= new JTextArea();
	public JTextArea console		= new JTextArea();
	public JTextArea console_log	= new JTextArea();
	public JTable var_table			= new JTable();
	public JSlider run_time			= new JSlider(SwingConstants.HORIZONTAL,0,2000,0);
	public JFileChooser fc;
	public ConsoleAppend consoleAppend		= new ConsoleAppend();
	public RunButtonListener RunButton		= new RunButtonListener(this);
	public IntVgOutputWindow gDrawWindow	= new IntVgOutputWindow(this);
	
	public PenFlags Flags = new PenFlags();
	
	public PenPlugin penPlugin = new PenPlugin();

	private PenFileFilter filter[] = {
			new PenFileFilter("txt" , "Text File （*.txt）") ,
			new PenFileFilter("pen" , "PEN File （*.pen）")
		};
	
	public ErrorSave error_dump = new ErrorSave();
	
	public PenProperties penPro;
	
	/**
	 * 実行時の引数の解析 および 設定ファイルを読み込む
	 */
	public MainGUI(String argv[], boolean isApplet){
		penPro = new PenProperties(isApplet);

		if (!isApplet){
			CreateFrame();
			ButtonListFile = penPro.getProperty(PenProperties.PEN_SYSTEM_DIR) + PenProperties.BUTTON_LIST_FILE;
		} else {
			ButtonListFile = PenProperties.BUTTON_LIST_FILE;
		}

		
		if (argv != null) {
			for(int i = 0; i < argv.length; i++) {
				if(argv[i].equals("-teacher") || argv[i].equals("-t")){
					enableTeacherMode();
				} else if(argv[i].equals("-debug") || argv[i].equals("-d")) {
					enableDebugMode();
				} else if(argv[i].equals("-dump")) {
					enableErrorDump();
				}
			}
		}

		if(penPro.containsKey(PenProperties.PEN_BUTTON_PATH)) {
			setButtonListFile(penPro.getProperty(PenProperties.PEN_BUTTON_PATH));
		}
		
		if(penPro.containsKey(PenProperties.PEN_TEACHER_FLAG) && Integer.parseInt(penPro.getProperty(PenProperties.PEN_TEACHER_FLAG)) == 1) {
			enableTeacherMode();
		}

		if(penPro.containsKey(PenProperties.PEN_DEBUG_FLAG) && Integer.parseInt(penPro.getProperty(PenProperties.PEN_DEBUG_FLAG)) == 1) {
			enableDebugMode();
		}

		if(penPro.containsKey(PenProperties.PEN_DUMP_FLAG) && Integer.parseInt(penPro.getProperty(PenProperties.PEN_DUMP_FLAG)) == 1) {
			enableErrorDump();
			
			if(penPro.containsKey(PenProperties.PEN_DUMP_TEMPDIR)) {
				error_dump.setTempDir(penPro.getProperty(PenProperties.PEN_DUMP_TEMPDIR));
			}
			if(penPro.containsKey(PenProperties.PEN_DUMP_DESTDIR)) {
				error_dump.setDestDir(penPro.getProperty(PenProperties.PEN_DUMP_DESTDIR));
			}
        }
        	
		if(penPro.containsKey(PenProperties.EXECUTER_GRAPHIC_ORIGIN) && Integer.parseInt(penPro.getProperty(PenProperties.EXECUTER_GRAPHIC_ORIGIN)) == 1) {
			gDrawWindow.enableOriginChange();
		}

		try {
			System.setProperty("java.library.path", penPro.getProperty(PenProperties.PEN_SYSTEM_LIBRARY) + System.getProperty("path.separator") + System.getProperty("java.library.path"));
			Field fieldSysPath;
			fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(System.class.getClassLoader(), null);
			
			if(System.getProperty("os.name").indexOf("Mac") < 0){
				File load = new File(penPro.getProperty(PenProperties.PEN_SYSTEM_LIBRARY));
				for(int i = 0; i < load.listFiles().length; i++){
					String fileName = load.listFiles()[i].getName();
					if(fileName.indexOf(".dll") >= 0){
						fileName = fileName.substring(0, fileName.indexOf(".dll"));
					} else if(fileName.indexOf(".jnilib") >= 0){
						fileName = fileName.substring(3, fileName.indexOf(".jnilib"));
					} else if(fileName.indexOf(".so") >= 0){
						fileName = fileName.substring(3, fileName.indexOf(".so"));
					} else {
						continue;
					}
					System.loadLibrary(fileName);
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

	// Application
	public void CreateFrame(){
		main_window	= new MyJFrame("NewFile - " + WindowName);
		fc		= new JFileChooser("./");
		penPro.setProperty(PenProperties.PEN_SYSTEM_HOME, System.getProperty("user.home"));
	}

	// Applet
	public void SetFrame(WebMain f){
		main_window = f;
		f.setTitle("NewFile - " + WindowName);
	}

	/**
	 * 各コンポーネントの初期化や配置を行う
	 */
	public void CreateGUI(boolean isApplet){
//		try{
//			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//			SwingUtilities.updateComponentTreeUI((JFrame)main_window);
//		}catch(Exception e){
//			System.out.println(e);
//		}
		
		if (!isApplet){
			for(int i = 0 ; i < filter.length ; i++)
				fc.addChoosableFileFilter(filter[i]);

			((JFrame)main_window).addWindowListener(new MyWindowAdapter(this));
			((JFrame)main_window).setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("pen.png")));
			((JFrame)main_window).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			((JFrame)main_window).setSize(820,600);
			((JFrame)main_window).setLocation(100,100);
		
			MenuBar = new MyJMenuBar(this);
			((JFrame)main_window).setJMenuBar(MenuBar.createMenuBar());
		}
		
		menu_panel.setLayout(new BoxLayout(menu_panel,BoxLayout.X_AXIS));
			if (main_window instanceof JFrame){
				// Application版
				new_button.addActionListener(new NewFileButtonListener(this));
				menu_panel.add(new_button);
				open_button.addActionListener(new FileOpenButtonListener(this));
				menu_panel.add(open_button);
				save_button.addActionListener(new FileSaveButtonListener(this));
				menu_panel.add(save_button);
			} else {
				// Applet版
				MenuButton webNewButton = new MenuButton("新規", 65, 30);
				webNewButton.addActionListener(new webNewButtonListener(this));
				menu_panel.add(webNewButton);
				MenuButton webFileOpenButton = new MenuButton("開く", 65, 30);
				webFileOpenButton.addActionListener(new webFileOpenButtonListener(this));
				menu_panel.add(webFileOpenButton);
			}
			menu_panel.add(new MyJLabel(new Dimension(20,30)));
			run_button.addActionListener(RunButton);
			menu_panel.add(run_button);
			step_button.addActionListener(RunButton);
			menu_panel.add(step_button);
			stop_button.addActionListener(RunButton);
			menu_panel.add(stop_button);
			menu_panel.add(new MyJLabel(new Dimension(10,30)));
			run_time_panel.setLayout(new BoxLayout(run_time_panel,BoxLayout.Y_AXIS));
				run_time_panel.add(new MyJLabel("(速)　　←　　実行速度　　→　　(遅)"));
				run_time.addMouseListener(new RunTimeMouseListener(run_time));
				run_time.addChangeListener(new RunTimeChangeListener(run_time));
				run_time.setPaintTicks(true);
				run_time.setMajorTickSpacing(200);
				run_time.setMinorTickSpacing(100);
				run_time_panel.add(run_time);
			menu_panel.add(run_time_panel);
			menu_panel.add(new MyJLabel(new Dimension(10,30)));
			menu_panel.add(run_print);
			menu_panel.add(new MyJLabel(new Dimension(10,30)));

		main_window.getContentPane().add(menu_panel,BorderLayout.NORTH);

		edit_panel.setLayout(new BoxLayout(edit_panel,BoxLayout.Y_AXIS));
			edit_area_panel.setLayout(new BoxLayout(edit_area_panel,BoxLayout.X_AXIS));
				breakpoint.setEditable(false);
				breakpoint.setBackground(new Color(220, 220, 220));
				breakpoint.setFont(font);
				breakpoint.addMouseListener(new BreakPointMouseListener(breakpoint));
				breakpoint.append("　");

				run_point.setEditable(false);
				run_point.setBackground(new Color(220, 220, 220));
				run_point.setForeground(new Color(255, 0 ,0));
				run_point.setFont(font);
				run_point.append("●");
				
				numbar_area.setEditable(false);
				numbar_area.setFont(font);
				numbar_area.append("  1:" + "\n");
				
				edit_doc.addDocumentListener(new EditAreaDocumentListener(main_window, edit_area, numbar_area));
				edit_doc.addUndoableEditListener(undo);
				edit_area.setDocument(edit_doc);
				edit_area.setTabSize(4);
				edit_area.setFont(font);
				edit_area.addKeyListener(new EditAreaKeyListener(edit_area));
				edit_area.addMouseListener(new EditAreaMouseListener(edit_area));
				
				try {
					// Application
					if (main_window instanceof JFrame) {
						edit_area_drop.addDropTargetListener(new FileDropOpen(this));
					}
					edit_area.setDropTarget(edit_area_drop);
				} catch (TooManyListenersException e) {
					e.printStackTrace();
				}
				
				edit_area_panel.add(breakpoint);
				edit_area_panel.add(run_point);
				edit_area_panel.add(numbar_area);
				edit_JSP = new JScrollPane(edit_area);
				edit_JSP.setRowHeaderView(edit_area_panel);
				edit_JSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			edit_panel.add(new MyJLabel("編集画面"));
			edit_panel.add(edit_JSP);
			try {
				edit_panel.add(new ButtonEdit(ButtonListFile, this).edit_button_area_toolbar);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			console_panel.setLayout(new BoxLayout(console_panel,BoxLayout.Y_AXIS));
				console.addKeyListener(new ConsoleKeyListener(this, console));
				console.setEditable(false);
				console.setLineWrap(true);
				console.setFont(font);
				console.setMargin(new Insets(1,5,1,1));
				console.setBackground(new Color(240, 240, 240));
				console_JSP = new JScrollPane(console);
				console_JSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				
				console_log.setEditable(false);
				console_log.setLineWrap(true);
				console_log.setFont(font);
				console_log.setMargin(new Insets(1,5,1,1));
				console_log.setBackground(new Color(240, 240, 240));
				console_log_JSP = new JScrollPane(console_log);
				console_log_JSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

				console_tab.setMinimumSize(new Dimension(230,200));
				console_tab.setFont(new Font("", 0, 12));
				console_tab.addTab("実行画面", console_JSP);
				console_tab.addTab("履歴", console_log_JSP);
				
				console_panel.add(new MyJLabel("コンソール画面"));
				console_panel.add(console_tab);

			consoleAppend.addTextArea(console);
			consoleAppend.addTextArea(console_log);
				
			var_panel.setLayout(new BoxLayout(var_panel,BoxLayout.Y_AXIS));
				vt_model= new DefaultTableModel(columnNames,0);
				var_table.setPreferredScrollableViewportSize(new Dimension(250,200));
				var_table.setModel(vt_model);
				var_table.setFont(font);
				var_table.setEnabled(false);
				for(int i=0; i < COLSIZE.length; i++)
					var_table.getColumnModel().getColumn(i).setPreferredWidth(COLSIZE[i]);
				var_table.setRowHeight(20);
				var_JSP = new JScrollPane(var_table);
				var_JSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				var_panel.add(new MyJLabel("変数表示画面"));
				var_panel.add(var_JSP);

		run_splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, console_panel, var_panel);
		run_splitpane.setPreferredSize(new Dimension(250,500));
		run_splitpane.setDividerSize(3);
		
		main_splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, edit_panel, run_splitpane);
		main_splitpane.setDividerSize(3);
		
		main_window.getContentPane().add(main_splitpane,BorderLayout.CENTER);
		
		main_window.setVisible(true);
		edit_area.requestFocus();
	}
	
	/**
	 * TeacherModeを有効にする
	 */
	public void enableTeacherMode(){
		COLSIZE[0] = 80;
		font = new Font("Monospaced", 0, 22);
	}
	
	/**
	 * DebugModeを有効にする
	 */
	public void enableDebugMode(){
		Flags.DebugFlag = true;
	}
	
	/**
	 * ErrorDumpを有効にする
	 */
	public void enableErrorDump(){
		error_dump.environment(var_table, console, edit_area, run_point);
	}
	
	/**
	 * 入力支援ボタン定義ファイルを変更する
	 * @param path
	 * 入力支援ボタン定義ファイルのパス
	 */
	public void setButtonListFile(String path){
		ButtonListFile = path;
	}
}
