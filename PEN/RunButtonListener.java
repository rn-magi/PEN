import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

class RunButtonListener implements ActionListener {
	MainGUI gui;
	private volatile ThreadRun RUN;
	
	public RunButtonListener(MainGUI gui){
		this.gui = gui;
	}

	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton)(e.getSource());
		if(source.getText().equals("実行") || source.getText().equals("始めから実行")){
			MyRun();
		}else if(source.getText().equals("一時停止")){
			gui.Flags.SuspendFlag = true;
			if(!gui.Flags.InputFlag){ gui.run_print.stop(); }
			if(gui.MenuBar != null){ gui.MenuBar.LogCopy(true); }
			gui.run_button.setText("再開");
		}else if(source.getText().equals("再開")){
			gui.Flags.StepFlag = false;
			gui.Flags.SuspendFlag = false;
			if(!gui.Flags.InputFlag){ gui.run_print.run(); }
			if(gui.MenuBar != null){ gui.MenuBar.LogCopy(false); }
			gui.run_button.setText("一時停止");
		}else if(source.getText().equals("一行実行")){
			if(!gui.run_button.getText().equals("始めから実行")){
				gui.Flags.StepFlag = true;
				gui.Flags.SuspendFlag = false;
				if(!gui.Flags.RunFlag){
					MyRun();
					gui.run_print.stop();
				}
				if(gui.MenuBar != null){ gui.MenuBar.LogCopy(true); }
				if(!gui.Flags.InputFlag){ gui.run_print.stop(); }
				gui.run_button.setText("再開");
			}
		}else if(source.getText().equals("始めに戻る")){
			if(gui.Flags.RunFlag){
				gui.Flags.RunFlag	= false;
				gui.Flags.StepFlag	= false;
				gui.Flags.SuspendFlag	= false;
				gui.Flags.InputFlag	= false;
				gui.Flags.StopFlag	= true;
				gui.gDrawWindow.gCloseWindow();
				RUN.ThreadStop();
				RUN = null;
			} else {
				new RunClean(gui);
			}
		}
	}
	
	public void MyRun(){
		gui.run_print.run();
		gui.run_point.setText("●");
		gui.consoleAppend.clean(ConsoleAppend.CONSOLE);

//		int x = MainGUI.vt_model.getRowCount();
//		for(int i = 1; i <= x; i++){
//			MainGUI.vt_model.removeRow(0);
//		}
		gui.vt_model.setRowCount(0);
		if(gui.MenuBar != null){ gui.MenuBar.MysetEnabled(false); }
		gui.edit_area.setEditable(false);
		gui.edit_area.setBackground(new Color(240, 240, 240));
		gui.numbar_area.setBackground(new Color(240, 240, 240));
		gui.new_button.setEnabled(false);
		gui.save_button.setEnabled(false);
		gui.open_button.setEnabled(false);
		gui.run_button.setText("一時停止");
		gui.Flags.RunFlag = true;
		gui.Flags.BugFlag = false;
		gui.gDrawWindow.gCloseWindow();
		RUN = new ThreadRun(gui);
		RUN.start();
	}
	
	public void MySuspend(){
		gui.Flags.SuspendFlag = true;
	}
}
