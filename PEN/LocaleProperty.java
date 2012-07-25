import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocaleProperty {
	/**
	 * Localeファイルを保存しているディレクトリ
	 */
	public final static String LOCALE_DIR		= "Locale";
	
	/**
	 * Localeファイルのファイル名
	 */
	public final static String LOCALE_FILE	= "Locale";
	
	/**
	 * LocaleファイルのPATH
	 */
	public final static String LOCALE_PATH	= LOCALE_DIR + "/" + LOCALE_FILE;
	
	/**
	 * Systemの言語コード
	 */
	public final static String LANGUAGE	= System.getProperty("user.language");
	
	/**
	 * Systemの国コード
	 */
	public final static String COUNTRY	= System.getProperty("user.country");
	
	/**
	 * Localeの設定
	 */
	public final static Locale Locale	= new Locale(LANGUAGE, COUNTRY);
	
	/**
	 * Localeファイルの取得
	 */
	private static ResourceBundle LOCALE_PROPERTY = ResourceBundle.getBundle(LOCALE_PATH , Locale);
	
	// プロパティー名
	public final static String CONFIG_LABEL_NAME		= "config.label.0";
	public final static String CONFIG_LABEL_SET		= "config.label.1";
	public final static String CONFIG_LABEL_Locale	= "congig.label.locale";
	
	private LocaleProperty(){}
	
	/**
	 * このリソースバンドルまたはその親リソースバンドルのいずれかから指定されたキーの文字列を取得します。
	 *
	 * @param key 望ましい文字列のキー
	 * @exception NullPointerException <code>key</code> が <code>null</code> の場合
	 * @exception MissingResourceException 指定されたキーのオブジェクトが見つからない場合
	 * @exception ClassCastException 指定されたキーの見つかったオブジェクトが文字列でない場合
	 * @return 指定されたキーの文字列
	 */
	public final static String getString(String key){
		return LOCALE_PROPERTY.getString(key);
	}
	
	/**
	 * ロケールの言語コードを返します。空の文字列または小文字の ISO 639 コードのどちらかが返されます。
	 * <p>注: ISO 639 は確定した規格ではありません。
	 * 一部の言語のコードは変更されています。
	 * ロケールのコンストラクタは、コードが変更された言語の新しいコードと従来のコードの両方を認識しますが、
	 * この関数は常に古い方のコードを返します。
	 */
	public final static String getLanguage(){
		return Locale.getLanguage();
	}
	
	/**
	 * ロケールの国/地域コードを返します。
	 * 空の文字列または 2 桁の大文字の ISO 3166 コードのどちらかが返されます。 
	 */
	public final static String getCountry(){
		return Locale.getCountry();
	}
	
	/**
	 * リソースバンドルの Locale を返します。
	 * このメソッドは、返されたリソースバンドルが本当に要求されたロケールに対応しているか、
	 * またはフォールバックであるかを判定するために、getBundle() を呼び出したあとで使用できます。 
	 *
	 * @return このリソースバンドルのロケール
	 */
	public final static Locale getLocale(){
		return LOCALE_PROPERTY.getLocale();
	}
}
