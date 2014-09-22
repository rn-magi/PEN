import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Hashtable;

public class PenPlugin {
	private Hashtable objTable= new Hashtable();
	private Hashtable functionTable= new Hashtable();
	private ArrayList objectName = new ArrayList();
	
	public PenPlugin() {
		try {
			String libPath = "lib";
			if(getOSBit().equals("64")){
				libPath += "64";
			}
			File load = new File(libPath);
			if(load.isDirectory()){
				for(int i = 0; i < load.listFiles().length; i++){
					String filePath = load.listFiles()[i].getPath();
					if(filePath.indexOf(".jar") >= 0){
						addClassPathToClassLoader(load.listFiles()[i]);
					}
				}
			}
			
			FileInputStream fis = new FileInputStream("./functionTable.ini");
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
			String str = reader.readLine();
			while (str != null) {
				String[] line = str.split("\t");
				if(!objTable.containsKey(line[1])){

					//ClassLoader loader = ClassLoader.getSystemClassLoader();

					URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file:./plugin/")}, getClass().getClassLoader());
					// 呼び出すClassを指定する
					Class claszz = loader.loadClass(line[1]);
					
					// インスタンスの生成
					Object obj = claszz.newInstance();
					
					objTable.put(line[1], obj);
					
					objectName.add(line[1]);
				}
				Object[] om = new Object[]{ objTable.get(line[1]), line[2]};
				
				functionTable.put(line[0], om);

				str = reader.readLine();
			}

			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	public Object runMethood(String methodName, Class[] c, Object[] o){
        Object[] obj = (Object[]) functionTable.get(methodName);
        Object ret = null;
        
		try {
			// 呼び出すMethodを指定する
			Method method = obj[0].getClass().getMethod(obj[1].toString(), c);
			
			// メソッドの実行
			ret = method.invoke(obj[0], o);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public boolean containsMethod(String methodName){
		return functionTable.containsKey(methodName);
	}
	
	public void destruction(){
		for(int i = 0; i < objectName.size(); i++){
			Object obj = objTable.get(objectName.get(i));
			try {
				Method method = obj.getClass().getMethod("destruction", new Class[]{ null });
				
				method.invoke(obj, new Object[]{ null });
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addClassPathToClassLoader(File classPath) throws Exception {
		addClassPathToClassLoader((URLClassLoader) ClassLoader.getSystemClassLoader(), classPath);
	}
	
	public void addClassPathToClassLoader(URLClassLoader classLoader, File classPath) throws Exception {
		Class classClassLoader = URLClassLoader.class;

		Method methodAddUrl = classClassLoader.getDeclaredMethod("addURL", URL.class);

		methodAddUrl.setAccessible(true);
		methodAddUrl.invoke(classLoader, classPath.toURI().toURL());
	}
	
	public String getOSBit() {
		String os = System.getProperty("sun.arch.data.model") ;
		if( os != null && (os = os.trim()).length() > 0 ) {
			return os;
		}
		os = System.getProperty("os.arch") ;
		if( os == null || (os = os.trim()).length() <= 0 ) {
			return "";
		}
		if( os.endsWith("86") ) {
			return "32";
		} else if( os.endsWith("64") ) {
			return "64";
		}
		return "32";
	}
}