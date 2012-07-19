import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RunTimeChangeListener implements ChangeListener{
	private JSlider JSlider;
	
	public RunTimeChangeListener(JSlider js){
		JSlider = js;
		JSlider.setToolTipText("0.0秒");
	}
	
	public void stateChanged(ChangeEvent e) {
		double i = JSlider.getValue();
		i = Math.round(Math.pow(i/1000,2)*500);
		i = i / 1000;
		
		JSlider.setToolTipText(i + "秒");
	}
}
