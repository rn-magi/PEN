package pen.GUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MyRunJLabel extends JPanel{
	private Dimension size	= new Dimension(80,35);
	private JLabel Label	= new JLabel();
	
	public MyRunJLabel(){
		breaks();
		Label.setFont(new Font("Dialog",0,14));
		//Label.setMaximumSize(size);
		//Label.setMinimumSize(size);
		//Label.setPreferredSize(size);
		Label.setHorizontalAlignment(SwingConstants.HORIZONTAL);
		
		setMaximumSize(size);
		setMinimumSize(size);
		setPreferredSize(size);
		
		setLayout(new BorderLayout());
		add(Label, BorderLayout.CENTER);
	}
	
	public void run(){
		Label.setText("実行中");
		Label.setFont(new Font("Dialog",1,14));
		Label.setForeground(new Color(0, 0, 0));
		setBackground(new Color(255,200,200));
	}

	public void input(){
		Label.setText("入力待ち");
		Label.setFont(new Font("Dialog",1,14));
		Label.setForeground(new Color(255, 0, 0));
		setBackground(new Color(255,255,150));
	}
	
	public void stop(){
		Label.setText("一時停止中");
		Label.setFont(new Font("Dialog",1,14));
		Label.setForeground(new Color(255, 0, 0));
		setBackground(new Color(255,255,150));
	}

	public void breaks(){
		Label.setText("実行待ち");
		Label.setFont(new Font("Dialog",0,14));
		Label.setForeground(new Color(0, 0, 0));
		setBackground(new Color(204,204,204));
	}
	
	public void end(){
		Label.setText("実行終了");
		Label.setFont(new Font("Dialog",0,14));
		Label.setForeground(new Color(0, 0, 0));
		setBackground(new Color(204,204,204));
	}
	
	public String getText(){
		return Label.getText();
	}
}
