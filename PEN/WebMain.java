import javax.swing.JApplet;
import java.net.URL;


public class WebMain extends JApplet implements PenFrame{
	URL codeBase;
	String window_name = "";
	
	public WebMain() {
		MainGUI PEN = new MainGUI(null, true);
		PEN.SetFrame(this);
		PEN.CreateGUI(true);
	}
	
	public void init() {
		codeBase = getCodeBase();
	}
	
	public URL getBase() { return codeBase; }
	
	public void setTitle(String wn) { window_name = wn; }
	
	public String getTitle() { return window_name; }
}
