package dentiq2.api.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static String getDayOfDate(String yyyymmdd) throws Exception {
		if ( yyyymmdd == null || yyyymmdd.trim().length() != 8 ) {
			throw new Exception("년월일 형식에 맞지 않음 [" + yyyymmdd + "]");
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date date = format.parse(yyyymmdd.trim());
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		String dayStr = "";
		
		int day = cal.get(Calendar.DAY_OF_WEEK);
		switch(day) {
			case 1 : dayStr = "일";		break;
			case 2 : dayStr = "월";		break;
			case 3 : dayStr = "화";		break;
			case 4 : dayStr = "수";		break;
			case 5 : dayStr = "목";		break;
			case 6 : dayStr = "금";		break;
			case 7 : dayStr = "토";		break;			
		}
		
		return dayStr;
	}
	
	public static String parseToYYYYMMDD(Date date) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		return fmt.format(date);
	}
	
	public static long diffDays(String startYYYYMMDD, String endYYYYMMDD) throws Exception {
		if ( startYYYYMMDD == null || startYYYYMMDD.length()!=8 || endYYYYMMDD==null || endYYYYMMDD.length()!=8 )
			throw new Exception("Arguments Invalid [" + startYYYYMMDD + "] [" + endYYYYMMDD + "]");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date startDate = format.parse(startYYYYMMDD);
		Date endDate   = format.parse(endYYYYMMDD);
		
		long diff = endDate.getTime() - startDate.getTime();
		
		//long diffDays = diff / (24 * 60 * 60 * 1000);
		
		return (long) (diff / (24 * 60 * 60 * 1000));
	}
	
	public static int diffMonths(String startYYYYMMDD, String endYYYYMMDD) throws Exception {
		
		if ( 	startYYYYMMDD == null || !(startYYYYMMDD.length()==6 || startYYYYMMDD.length()==8)
			|| 	endYYYYMMDD == null   || !(endYYYYMMDD.length()==6   || endYYYYMMDD.length()==8) )
					throw new Exception("Arguments Invalid [" + startYYYYMMDD + "] [" + endYYYYMMDD + "]");
		
		if ( startYYYYMMDD.length()==6 )	startYYYYMMDD = startYYYYMMDD + "01";
		if ( endYYYYMMDD.length()==6 ) 		endYYYYMMDD = endYYYYMMDD + "01";
		
		long diffDays = diffDays(startYYYYMMDD, endYYYYMMDD);
		
		return (int) (diffDays / 30);
	}
	
	
	public static int diffYears(String startYYYYMMDD, String endYYYYMMDD) throws Exception {
		long diffDays = diffDays(startYYYYMMDD, endYYYYMMDD);
		
		return (int) (diffDays / 365);
	}
	
	public static String todayYYYYMMDD() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(new Date());
	}
	
	public static String todayYYYYMM() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		return format.format(new Date());
	}
	
	
	
	/**
	 * 만 나이 계산
	 * @param birthYYYYMMDD	생일 YYYYMMDD 문자열
	 * @return
	 * @throws Exception
	 */
	public static int calAge(String birthYYYYMMDD) throws Exception {
		if ( birthYYYYMMDD==null || birthYYYYMMDD.trim().length() != 8 ) {
			throw new Exception("Number Format Exception : YYYYMMDD [" + birthYYYYMMDD + "]");
		}
		
		int age = 0;
		try {
			Calendar current = Calendar.getInstance();
			
			Calendar birthMMDD = Calendar.getInstance();
			birthMMDD.set(
							current.get(Calendar.YEAR),
							Integer.parseInt(birthYYYYMMDD.substring(4, 6)) - 1,
							Integer.parseInt(birthYYYYMMDD.substring(6, 8))
						);
			if ( current.compareTo(birthMMDD) >= 0 ) {	// 오늘 날짜가 생월일과 동일하거나 이후이면,
				age = current.get(Calendar.YEAR) - Integer.parseInt(birthYYYYMMDD.substring(0,  4));
			} else {									// 오늘 날짜가 생월일 이전이면,
				age = current.get(Calendar.YEAR) - Integer.parseInt(birthYYYYMMDD.substring(0,  4)) - 1;
			}
		} catch(Exception ex) {
			throw new Exception("Number Format Exception : YYYYMMDD [" + birthYYYYMMDD + "] [" + ex + "]");
		}
		
		return age;
	}
}