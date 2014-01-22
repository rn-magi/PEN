import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class PenProperties {
	private Properties PROPERTY = new Properties();

	private String FileSeparator	= System.getProperty("file.separator");
	private String PropertyFileName	= "Property.ini";
	
	public final static String BUTTON_LIST_FILE		= "ButtonList/Default.ini";
	
	public final static String PEN_BUTTON_PATH		= "pen.button.path";
	public final static String PEN_TEACHER_FLAG		= "pen.teacher.flag";
	public final static String PEN_DEBUG_FLAG		= "pen.debug.flag";
	public final static String PEN_DUMP_FLAG		= "pen.dump.flag";
	public final static String PEN_DUMP_TEMPDIR		= "pen.dump.tempdir";
	public final static String PEN_DUMP_DESTDIR		= "pen.dump.destdir";
	public final static String PEN_SYSTEM_DIR		= "pen.system.dir";
	public final static String PEN_SYSTEM_CODE		= "pen.system.code";
	public final static String PEN_SYSTEM_HOME		= "pen.system.home";
	public final static String EXECUTER_GRAPHIC_ORIGIN	= "executer.graphic.origin";
	public final static String EXECUTER_VAR_DECLARATION	= "executer.var.declaration";
	public final static String EXECUTER_VAR_ORIGIN		= "executer.array.origin";
	public final static String EXECUTER_VAR_NAMES		= "executer.var.names";
	public final static String EXECUTER_DIV_MODE		= "executer.div.mode";
	
	public final static String Arduino_EXEC_PATH		= "arduino.exec.path";

	public final static int DECLARATION_PROCEDURAL	= 0;
	public final static int DECLARATION_INT			= 1;
	public final static int DECLARATION_LONG		= 2;
	public final static int DECLARATION_DOUBLE		= 3;
	public final static int DECLARATION_STRING		= 4;
	public final static int DECLARATION_BOOLEAN		= 5;
	
	public PenProperties(boolean isApplet){
		String dir = "";
		if (!isApplet){
			File path = new File(new File(".").getAbsoluteFile().getParent());
			
			if( path.getParent() == null ) {
				dir = "." + FileSeparator;
			} else if( path.isDirectory() ){
				dir = path.getPath() + FileSeparator;
			} else {
				dir = path.getParent() + FileSeparator;
			}
			setProperty(PenProperties.PEN_SYSTEM_DIR, dir);
			setProperty(PenProperties.PEN_SYSTEM_HOME, System.getProperty("user.home"));
		}
		
		try {
			PROPERTY.load(getClass().getResourceAsStream(PropertyFileName));
			if (!isApplet){
				PROPERTY.load(new FileInputStream(dir + PropertyFileName));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean containsKey(String key){
		return PROPERTY.containsKey(key);
	}
	
	public String getProperty(String key){
		return PROPERTY.getProperty(key);
	}
	
	public Enumeration propertyNames(){
		return PROPERTY.propertyNames();
	}
	
	public void setProperty(String key, String value){
		PROPERTY.setProperty(key, value);
	}
}
