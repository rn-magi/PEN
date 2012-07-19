import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PenPlugin {
	public static void main(String[] args) {
		try {
			ClassLoader loader = ClassLoader.getSystemClassLoader();
			
			// 呼び出すClassを指定する
			Class claszz = loader.loadClass("plugin.TestCallMethod");
			
			// インスタンスの生成
			Object obj = claszz.newInstance();
			
			// 呼び出すMethodを指定する
			Method m = obj.getClass().getMethod("print", null);
			
			// メソッドの実行
			m.invoke(obj, null);
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
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
