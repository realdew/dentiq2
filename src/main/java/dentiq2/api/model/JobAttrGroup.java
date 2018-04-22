package dentiq2.api.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class JobAttrGroup {
	
	// DB 검색 조건을 위한 클래스
	
	/*
		EMP.1, EMP.2, TASK.1 이 입력된 경우.
		
		EMP와 TASK 그룹을 가지고 있어야 한다.
		
	 */
	
	@Getter @Setter private String groupId;
	
	@Getter @Setter private List<JobAttr> attrList;
	
	public JobAttrGroup(String groupId) {
		this.groupId = groupId.trim();
		this.attrList = new ArrayList<JobAttr>();
	}
	
	public boolean add(JobAttr attr) {
		if ( attr==null ) return false;		
		if ( this.attrList.contains(attr) ) return false;		
		this.attrList.add(attr);
		return true;
	}
	
	
	
	
	// 입력된대로 변형하기 위함	
	public static List<JobAttrGroup> createJobAttrGroupFromStringList(List<String> attrStrList) {
		if ( attrStrList==null || attrStrList.size()<0 ) return null;
		
		Map<String, JobAttrGroup> attrGroupMap = new Hashtable<String, JobAttrGroup>();
		for ( String attrStr : attrStrList ) {
			JobAttr attr = null;
			try {	attr = new JobAttr(attrStr);	} catch(Exception skip) { continue; }
			
			JobAttrGroup attrGroup = attrGroupMap.get(attr.getGroupId());
			if ( attrGroup == null ) {
				attrGroup = new JobAttrGroup(attr.getGroupId());
				attrGroupMap.put(attr.getGroupId(), attrGroup);
			}
			attrGroup.add(attr);			
		}
		
		return new ArrayList<JobAttrGroup>(attrGroupMap.values());
	}
}
