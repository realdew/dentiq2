package dentiq2.api;

import lombok.Getter;

public class LogicalException extends Exception {

	private static final long serialVersionUID = 3094785379543429952L;

	
	@Getter private String code;
	@Getter private String message;
	
	private LogicalException(String code, String message) {
		super("code:" + code + "\t" + message);
		this.code = code;
		this.message = message;
	}
	
	
	public LogicalException(ErrorCodable errorCodable, String...strings) {
		this(errorCodable.getCode(), errorCodable.getMessage(strings));
	}
	public LogicalException(ErrorCodable errorCodable) {
		this(errorCodable.getCode(), errorCodable.getMessage());
	}
	
	@Override
	public String toString() {
		return "LogicalException: CODE[" + this.code + "] MSG[" + this.message + "]";
	}
	

}
