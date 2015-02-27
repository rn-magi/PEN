
public enum Declaration {
	PROCEDURAL("手続き", ""),
	INT("整数", "0"){
		public Object getInitObject() {
			return new Integer(INT.getDeclarationInit());
		}
	},
	LONG("長整数", "0"){
		public Object getInitObject() {
			return new Long(LONG.getDeclarationInit());
		}
	},
	DOUBLE("実数", "0.0"){
		public Object getInitObject() {
			return new Double(DOUBLE.getDeclarationInit());
		}
	},
	STRING("文字列", ""){
		public Object getInitObject() {
			return new String(STRING.getDeclarationInit());
		}
	},
	BOOLEAN("真偽", "false"){
		public Object getInitObject() {
			return new Boolean(BOOLEAN.getDeclarationInit());
		}
	};
	
	private String declarationName;
	private final String declarationInit;
	
	private Declaration(String declarationName, String declarationInit) {
		this.declarationName = declarationName;
		this.declarationInit = declarationInit;
	}
	
	public void setDeclarationName(String declarationName) {
		this.declarationName = declarationName;
	}
    
	public String getDeclarationName() {
		return declarationName;
	}
    
	public String getDeclarationInit() {
		return declarationInit;
	}
    
	public Object getInitObject() {
		return null;
	}
}
