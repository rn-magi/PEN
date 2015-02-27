import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JSlider;

public class RunTimeMouseListener implements MouseListener {
	JSlider run_time;
	
	public RunTimeMouseListener(JSlider js){
		run_time = js;
	}

	public void mouseClicked(MouseEvent e) {
	
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {
		run_time.setValue(run_time.getMaximum() * e.getX() / run_time.getWidth());
	}
}
