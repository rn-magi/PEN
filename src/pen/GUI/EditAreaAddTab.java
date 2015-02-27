package pen.GUI;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

/**
 * インデントの個数を数え追加すべきインデントを返します
 * 
 * @author Ryota Nakamura
 */
public class EditAreaAddTab {	
	/**
	 * @param start
	 * インデントを数える行頭のオフセット
	 * @param end
	 * インデントを数える行末のオフセット
	 * @return
	 * 追加すべきインデントを返します
	 */
	public String AddTab(int start, int end, JTextArea edit_area){
		String add = "";
		try {
			for(int i = start; i < end; i++){
				String str = edit_area.getText(i,1);
				if(str.equals("｜") || str.equals("|") || str.equals(" ") || str.equals("　")){
					if(str.equals("｜")){
						add += "  | ";
					}else if(str.equals("|")){
						add += "  | ";
					}
				}else{
					break;
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return add;
	}
}
