package dentiq2.api.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dentiq2.api.code.LocationCodeManager;
import dentiq2.api.code.SystemConstants;
import dentiq2.api.mapper.CommonMapper;
import dentiq2.api.model.Location;


/**
 * 시스템 관리용 Controller
 * 
 * 이 Controller에는 오직 시스템 관리자만 접근할 수 있도록 정책 세울 것
 * 
 * @author lee
 *
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class AdminController {
	
	@Autowired CommonMapper commonMapper;
	
	
	// 지역 코드 리로드. 주의 : 상수는 리로드하면 안된다.!!!
//	@RequestMapping(value="/admin/system/", method=RequestMethod.GET)
//	public String sysMain(
//			@RequestParam(value="reload",	required=false)		Long userId,
//			HttpServletRequest httpRequest,
//			HttpServletResponse httpResponse
//		
//			) {
//		
//		
//		
//		return "DONE";
//	}
	
	// 설정들을 로딩한다.
	@PostConstruct
	public void postConstruct() throws Exception {
		
		// 지역 코드 Singleton 로딩
		List<Location> locationList = commonMapper.listLocationCode();
		LocationCodeManager locationCode = LocationCodeManager.getInstance();
		locationCode.setLocationList(locationList);
		
		
		// 시스템 상수 설정 Singleton 로딩
		SystemConstants systemConfig = SystemConstants.getInstance();
		systemConfig.init();
		
		
	}
	
	@RequestMapping(value="/test/", method=RequestMethod.GET)
	public String test() throws Exception {
		
		String result = "READY !!! : " + System.currentTimeMillis();
		
		
		
		return result;
	}
	
	@Autowired private JavaMailSender mailSender;
	
	@RequestMapping(value="/testMail/", method=RequestMethod.GET)
	public String testMail() throws Exception {
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo("leejuhyeon@gmail.com");
		message.setSubject("[덴탈플러스 시스템 관리자 " + System.currentTimeMillis());
		message.setText("테스트 메시지임. " + System.currentTimeMillis());
		
		mailSender.send(message);
		
		
		String result = "A test email was sent !!! : " + System.currentTimeMillis();
		return result;
	}

}
