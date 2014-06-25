public class ParseIsolateCause {
	public static String isolateCause(Exception e){
		if(e instanceof ParseException) {
			return "### 構文エラーで実行できませんでした\n";
		} else {
			return "### 構文エラーで実行できませんでした\n"
					+ "### エラーコード：" + e + "\n";
		}
	}
	
	public static String isolateCause(Error e){
		if(e instanceof ThreadRunStop) {
			return "";
		} else if(e instanceof TokenMgrError) {
			return "### 構文エラーで実行できませんでした\n"
					+ "### " + e.getMessage() + "\n";
		} else if(e instanceof VarNameError) {
			return "### " + ((VarNameError) e).line +"行目でエラーです。\n"
					+ "### 全角文字の '" + e.getMessage() + "' を使用\n";
		} else if(e instanceof ParseError) {
			return "### 構文エラーで実行できませんでした\n"
					+ "### エラーコード：ParseError " + e.getMessage() + "\n";
		} else {
			return "### 構文エラーで実行できませんでした\n"
					+ "### エラーコード：Error " + e + "\n";
		}
	}
}
