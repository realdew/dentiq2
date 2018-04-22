package dentiq2.api.code;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;



@JsonInclude(Include.NON_NULL)
@JsonNaming(MyPropertyNamingStrategy.UpperCaseStrategy.class)
public class SystemConstants {
	
	@Getter private final String API_VERSION = "20180418";
	
	
	@Getter private String HOSPITAL_RESOURCE_URL_SERVER_ROOT;							// rest api로 클라이언트에 전달. 웹에 표시될 서버 주소 (http://resouces.dentalplus.com)
	@Getter	private String HOSPITAL_RESOURCE_URL_DIR_ROOT;								// rest api로 클라이언트에 전달. 웹에 표시될 경로명 (/resources/hospital)
	@Getter private String HOSPITAL_RESOURCE_URL_FULL;									// rest api로 클라이언트에 전달. 웹에 표시될 전체 경로 (http://resouces.dentalplus.com/resources/hospital)
	
	@Getter private String HOSPITAL_RESOURCE_FILE_NAME_LOGO_SMALL;						// rest api 및 서버 공통 사용
	@Getter private String HOSPITAL_RESOURCE_FILE_NAME_LOGO_MEDIUM;						// rest api 및 서버 공통 사용
	@Getter private String HOSPITAL_RESOURCE_FILE_NAME_LOGO_LARGE;						// rest api 및 서버 공통 사용
		
	private String HOSPITAL_RESOURCE_PHYSICAL_DIR_ROOT;									// 서버상에 저장될 절대경로 ( /var/www/dentalplus/resources/hospital )
	@JsonIgnore public String getHOSPITAL_RESOURCE_PHYSICAL_DIR_ROOT() { return this.HOSPITAL_RESOURCE_PHYSICAL_DIR_ROOT; }
	
	
	
	
	@Getter private String JOB_SEEKER_RESOURCE_URL_SERVER_ROOT;							// rest api로 클라이언트에 전달. 웹에 표시될 서버 주소 (http://resouces.dentalplus.com)	
	@Getter private String JOB_SEEKER_RESOURCE_URL_DIR_ROOT;							// rest api로 클라이언트에 전달. 웹에 표시될 경로명 (/resources/jobseeker)
	@Getter private String JOB_SEEKER_RESOURCE_URL_FULL;								// rest api로 클라이언트에 전달. 웹에 표시될 전체 경로 (http://resouces.dentalplus.com/resources/jobseeker)
	
	@Getter private String JOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_SMALL;					// rest api 및 서버 공통 사용
	@Getter private String JOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_MEDIUM;				// rest api 및 서버 공통 사용
	@Getter private String JOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_LARGE;					// rest api 및 서버 공통 사용
		
	private String JOB_SEEKER_RESOURCE_PHYSICAL_DIR_ROOT;								// 서버상에 저장될 절대경로 ( /var/www/dentalplus/resources/jobseeker )
	@JsonIgnore public String getJOB_SEEKER_RESOURCE_PHYSICAL_DIR_ROOT() { return this.JOB_SEEKER_RESOURCE_PHYSICAL_DIR_ROOT; }
	
	
	// 실제 파일이 저장되는 위치 예 : c:/work/dentalplus/resources/jobSeeker/{사용자ID}/profile_small.jpg
	
	
	
	private void setConstants(Properties prop) throws Exception {
		
		this.HOSPITAL_RESOURCE_FILE_NAME_LOGO_SMALL			= getPropValue(prop, "HOSPITAL_RESOURCE_FILE_NAME_LOGO_SMALL");
		this.HOSPITAL_RESOURCE_FILE_NAME_LOGO_MEDIUM		= getPropValue(prop, "HOSPITAL_RESOURCE_FILE_NAME_LOGO_MEDIUM");
		this.HOSPITAL_RESOURCE_FILE_NAME_LOGO_LARGE			= getPropValue(prop, "HOSPITAL_RESOURCE_FILE_NAME_LOGO_LARGE");
		
		this.HOSPITAL_RESOURCE_URL_SERVER_ROOT				= getPropValue(prop, "HOSPITAL_RESOURCE_URL_SERVER_ROOT");
		this.HOSPITAL_RESOURCE_URL_DIR_ROOT					= getPropValue(prop, "HOSPITAL_RESOURCE_URL_DIR_ROOT");
		this.HOSPITAL_RESOURCE_URL_FULL						= this.HOSPITAL_RESOURCE_URL_SERVER_ROOT + "/" + this.HOSPITAL_RESOURCE_URL_DIR_ROOT;
		
		this.HOSPITAL_RESOURCE_PHYSICAL_DIR_ROOT			= getPropValue(prop, "HOSPITAL_RESOURCE_PHYSICAL_DIR_ROOT");
		
		
		
		this.JOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_SMALL	= getPropValue(prop, "JOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_SMALL");
		this.JOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_MEDIUM	= getPropValue(prop, "JOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_MEDIUM");
		this.JOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_LARGE	= getPropValue(prop, "JOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_LARGE");
		
		this.JOB_SEEKER_RESOURCE_URL_SERVER_ROOT			= getPropValue(prop, "JOB_SEEKER_RESOURCE_URL_SERVER_ROOT");
		this.JOB_SEEKER_RESOURCE_URL_DIR_ROOT				= getPropValue(prop, "JOB_SEEKER_RESOURCE_URL_DIR_ROOT");
		this.JOB_SEEKER_RESOURCE_URL_FULL					= this.JOB_SEEKER_RESOURCE_URL_SERVER_ROOT + "/" + this.JOB_SEEKER_RESOURCE_URL_DIR_ROOT;
		
		this.JOB_SEEKER_RESOURCE_PHYSICAL_DIR_ROOT			= getPropValue(prop, "JOB_SEEKER_RESOURCE_PHYSICAL_DIR_ROOT");
		
		
	}
	private static String getPropValue(Properties prop, String key) throws Exception {
		String value = (String) prop.remove(key);
		if ( value==null || value.trim().equals("") ) throw new Exception("KEY[" + key + "]에 대한 VALUE가 없습니다. Properties[" + prop + "]");
		return value.trim();
	}
	
	
	
	
	
	
	
	
	
	
	private SystemConstants() {
		System.out.println("************************* SystemConfig 생성됨 *******************************");	// 혹시라도 2번 이상 로드되는지 확인하기 위해서 넣어 놓음
	}
	public static SystemConstants getInstance() {
		return SystemConfigHolder.instance;
	}
	public static class SystemConfigHolder {
		private static final SystemConstants instance = new SystemConstants();
	}
	
	
	
	
	
	
	// -Ddentiq.configFileName=C:/work/dentalplus/api_server.config
	public static final String VM_ARGUMENT_FOR_SYSTEM_CONFIG_FILE_NAME = "dentiq.configFileName";
	
	
	private String configFileName;
	
		
	public void init() throws Exception {
		
		this.configFileName = System.getProperty("dentiq.configFileName");
		if ( configFileName==null || configFileName.trim().equals("") ) throw new Exception("SystemConfig : 설정 인자가 지정되지 않았습니다. -Ddentiq.configFileName=xxxx 형태의 JVM argument가 필요합니다.");
	
		loadFromFile();
	}
	
	
	//TODO 관리용 Servlet에서 호출할 수 있도록 한다.
	public void loadFromFile() throws Exception {
		if ( configFileName==null || configFileName.trim().equals("") ) throw new Exception("SystemConfig : 설정 파일이 지정되지 않았습니다.");
		
		System.out.println("**** SystemConfig : 설정파일을 읽습니다. from " + configFileName);
		
		Properties prop = new Properties();
		
		File file = new File(configFileName);
		if ( !file.exists() )	throw new Exception("SystemConfig : Config 파일이 존재하지 않습니다. [" + configFileName + "]");
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			
			prop.load(fis);
			
			fis.close();
		} catch(Exception ex) {			
			if ( fis != null ) try { fis.close(); } catch(Exception ignore) {}
			ex.printStackTrace();
			throw new Exception("**** SystemConfig : Config 파일\" + systemConfigFileName + \")을 읽을 수 없습니다. [" + ex + "]");
		}
		
		System.out.println("**** SystemConfig : 로딩 대상 (총 " + prop.size() + "개)\n\t" + prop);
		
		setConstants(prop);
		
		System.out.println("**** SystemConfig : 로딩 완료");		
	}
	
	
	

}


/**
 * Jackson에 Property를 대문자로만 리턴하는 PropertyNamingStrategy가 존재하지 않아서 만듦
 * 
 * com.fasterxml.jackson.databind.PropertyNamingStrategy을 상속받아서 구현했음.
 * 
 * 
 * @author lee *
 */
@SuppressWarnings("serial")
class MyPropertyNamingStrategy extends PropertyNamingStrategy {
	
	public static final PropertyNamingStrategy UPPPER_CASE = new UpperCaseStrategy();
	
	public static class UpperCaseStrategy extends PropertyNamingStrategyBase {
        @Override
        public String translate(String input) {
            return input.toUpperCase();
        }
    }
}
