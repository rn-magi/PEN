import java.awt.Color;

public class EditButtonList {
	public String ButtonText;
	public String TipText;
	public String[] AppendText;
	public int TextWidth;
	public int Width;
	public int Height;
	public Color Color;
	
	public EditButtonList(String ButtonText, String TipText, String[] AppendText, int Width, int Height, Color Color){
		this.ButtonText	= ButtonText;
		this.TipText	= TipText;
		this.AppendText	= AppendText;
		this.Width	= Width;
		this.Height	= Height;
		this.Color	= Color;
	}
	
	public EditButtonList(String button){
		String[] split	= button.split("@");
		ButtonText	= split[0];
		TextWidth	= new Integer(split[1]).intValue();
		Width	= (int) Math.round(TextWidth * 10.83);
		Height	= 25;

		if(split.length > 2 && !split[2].equals("")) {
			String[] color	= split[2].split(",");
			Color = new Color(Integer.valueOf(color[0]), Integer.valueOf(color[1]), Integer.valueOf(color[2]));
		}
		
		if( split.length >= 4 ){
			if( split.length == 4 ){
				TipText	= "<html><pre>" + split[3].replaceAll("<br>", "\n") + "</pre></html>";
			} else if( split.length == 5) {
				TipText	= "<html><pre>" + split[4].replaceAll("<br>", "\n") + "</pre></html>";
			}
			
			if(split[3].endsWith("<br>")) {
				split[3] += " ";
			}
			AppendText	= split[3].split("<br>");
			for(int i = 0; i < AppendText.length - 1; i++) {
				AppendText[i] += "\n";
			}

			if(AppendText[AppendText.length-1].equals(" ")) {
				AppendText[AppendText.length-1] = "";
			}
		} else {
			String str[] = {""};
			TipText = "";
			AppendText = str;
		}
	}
}