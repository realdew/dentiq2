package dentiq2.api.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 채용 속성
 * 
 * 
 * @author lee
 *
 */
@ToString
public class JobAttr {
	
	/**
	 * JOB_AD_ATTR 테이블에 대한 삭제/추가를 할 때 사용한다.
	 * @param attrStrList	예: EMP.1, EMP.3, TASK.1
	 * @return				예: JobAttr(for EMP.1), JobAttr(for EMP.3), JobAttr(for TASK.1)
	 * @throws Exception	
	 */
	public static List<JobAttr> createJobAttrList(List<String> attrStrList) throws Exception {
		if ( attrStrList==null || attrStrList.size()<0 ) return null;
		
		List<JobAttr> attrList = new ArrayList<JobAttr>();
		for ( String attrStr : attrStrList ) {
			if ( attrStr==null || attrStr.trim().equals("") ) continue;
			JobAttr attr = new JobAttr(attrStr);
			attrList.add(attr);
		}
		return attrList;
	}
	
	
	@Getter @Setter private String codeId;				// EMP.1
	@Getter @Setter private String groupId;				// EMP
	@Getter @Setter @JsonIgnore private String attrSuffix;	// 1
	
	public JobAttr(String attrStr) throws Exception {
		checkFormat(attrStr);
				
		String[] token = attrStr.trim().split("\\.");
		this.codeId = attrStr.trim();
		this.groupId = token[0].trim();
		this.attrSuffix = token[1].trim();
	}
	
	public static void checkFormat(String attrStr) throws Exception {
		if ( attrStr==null ) throw new Exception("Invalid Format. [" + attrStr + "]");
		String temp = attrStr.trim();
		if ( temp.equals("") ) throw new Exception("Invalid Format. [" + attrStr + "]");
		if ( temp.indexOf(".") < 0 ) throw new Exception("Invalid Format. [" + attrStr + "]");
	}
	
	public boolean equals(Object obj) {
		if ( obj==null ) return false;
		if ( !(obj instanceof JobAttr) ) return false;		
		JobAttr other = (JobAttr) obj;		
		if ( this.codeId.equals(other.codeId) ) return true;		
		return false;
	}
}