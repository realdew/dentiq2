package dentiq2.api;

public enum ErrorCode implements ErrorCodable {

//	ERR_001("ERR_001", "%1은(는) 필수입력항목입니다."),
//	ERR_002("ERR_002", "%1은(는) 숫자형식입니다.")
//	;
	
	USER_001("USER_001", "해당 사용자(%1)는 가입되어 있지 않습니다."),
	USER_002("USER_002", "비밀번호가 일치하지 않습니다."),
	
		
	AUTH_901("AUTH_901", "로그인되어 있지 않습니다."),				// 로그인되어 있지 않습니다.
	AUTH_902("AUTH_902", "사용시간이 만료되었습니다. 다시 로그인해 주세요."),	// expireTimeAt 초과의 경우
	
	AUTH_001("AUTH_001", "병원회원만 사용가능합니다."),				// 병원회원 메뉴에 병원회원 아닌 자가 접근
	AUTH_002("AUTH_002", "해당 병원에 대한 접근권한이 없습니다."),	// 병원회원의 hospitalId와 입력된 hospitalId가 불일치
	AUTH_005("AUTH_005", "해당 병원에 대한 접근권한이 없습니다."),	// 입력된 병원 정보 상의 userId와 세션 userId가 불일치
	AUTH_006("AUTH_006", "해당 병원에 대한 접근권한이 없습니다."),	// 입력된 병원 정보 상의 hospitalId와 세션 hospitalId가 불일치
	AUTH_007("AUTH_007", "해당 병원에 대한 접근권한이 없습니다."),	// 로딩된 병원 정보의 USER_ID와 요청된 USER_ID가 불일치
	
	AUTH_010("AUTH_010", "병원 정보를 먼저 등록해 주십시오."),		// 세션에 병원 ID 없음 ==> 공고 등록에서는 '병원 정보를 먼저 등록해주십시오'
	AUTH_011("AUTH_011", "회원님의 정보와 공고 정보가 불일치합니다. 다시 로그인 후 사용하여 주십시오."),		// 세션의 병원ID와 등록하려고 하는 공고의 병원 ID가 불일치 ==> 공고 등록에서는 '회원님의 정보와 공고 정보가 불일치합니다. 다시 로그인 후 사용하여 주십시오.'
	
	
	
	AUTH_101("AUTH_101", "개인회원만 사용가능합니다."),				// 개인회원 메뉴에 개인회원 아닌 자가 접근
	AUTH_102("AUTH_102", "해당 사용자 정보에 접근할 수 없습니다."),	// 개인회원 사용자 ID 불일치
	
	//USER_601("USER_601", "해당 이력서가 존재하지 않습니다."),		// 이력서 조회에서 resumeI에 해당하는 이력서가 없는 경우.
	
	USER_501("USER_501", "우리동네가 설정되어 있지 않습니다."),		// 우리동네가 설정되어 있지 않습니다.
	USER_502("USER_502", "관심지역이 설정되어 있지 않습니다."),		// 관심지역이 설정되어 있지 않습니다.
	
	
	
	ERR_999("ERR_999", "잠시 후 다시 시도해 주십시오.")
	;
	
	
	private String code;
	
	private String message;
	
	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage(String... args) {
		return ErrorCodeUtil.parseMessage(this.message, args);
	}
	
	private ErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

}
class ErrorCodeUtil {
	public static String parseMessage(String message, String...args) {
      if (message == null || message.trim().length() <= 0)
         return message;
  
      if (args == null || args.length <= 0) return message;
  
      String[] splitMsgs = message.split("%");
      if (splitMsgs == null || splitMsgs.length <= 1)
         return message;
  
      for (int i = 0; i < args.length; i++) {
         String replaceChar = "%" + (i + 1);
         message = message.replaceFirst(replaceChar, args[i]);
      }
      return message;
   }
}

interface ErrorCodable {

	public String getCode();
	
	public String getMessage(String... args);

}
