import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

public class PenPlugin {
	private Hashtable objTable= new Hashtable();
	private Hashtable functionTable= new Hashtable();
	
	public PenPlugin() {
		try {
			FileInputStream fis = new FileInputStream("./functionTable.ini");
			InputStreamReader isr = new InputStreamReader(fis, "SJIS");
			BufferedReader reader = new BufferedReader(isr);
			String str = reader.readLine();
			while (str != null) {
				String[] line = str.split("\t");
				if(!objTable.containsKey(line[1])){

					ClassLoader loader = ClassLoader.getSystemClassLoader();
					
					// 呼び出すClassを指定する
					Class claszz = loader.loadClass("plugin.TestCallMethod");
					
					// インスタンスの生成
					Object obj = claszz.newInstance();
					
					objTable.put(line[1], obj);
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
		}
	}
	
	public Object runMethood(String m){
        Object[] obj = (Object[]) functionTable.get(m);
        Object ret = null;
        
		try {
			// 呼び出すMethodを指定する
			Method met = obj[0].getClass().getMethod(obj[1].toString(), null);
			
			// メソッドの実行
			ret = met.invoke(obj[0], null);
		} catch (SecurityException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		return ret;
	}
}
