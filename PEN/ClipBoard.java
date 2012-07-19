import java.awt.*;
import java.awt.datatransfer.*;

/**
 *システムのクリップボードに渡された文字列を格納するクラス
 *
 * @author Ryota Nakamura
 */
public class ClipBoard implements ClipboardOwner {
	
	/**
	 * <code>clipboard</code> のコメント
	 * クリップボードクラスの初期化
	 */
	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	/**
	 * @param data
	 * クリップボードに格納する文字列
	 */
	public ClipBoard(String data){
		StringSelection contents = new StringSelection(data);
		clipboard.setContents(contents, this);
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}
}
