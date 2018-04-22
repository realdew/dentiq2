package dentiq2.api.code;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;


public class AttrCodeManager {
	
	private Map<String, AttrCode> attrGroups;
	
	
	public class AttrCode {
		String code;
		String type;
		String text;
		String order;
		String deprecatedYn;
		boolean deprecated;
		
		String parentCode;
		
		Map<String, AttrCode> elements;
//		public void addElement(AttrCode attrCode) {
//			
//			if ( attr)
//			
//		}
	}
	
}
/*
public class AttrCodeManager {
	
	
	Map<String, AttrCodeGroup> groups;
	
	
	
	class AttrCodeGroup extends AttrCode {	
		//String code;
		
		
		//int order;
		//String text;
		//boolean deprecated;
		
		@Getter private Map<String, AttrCode> elements;
		
		public void addElement(AttrCode attrCode) {
			if ( this.elements == null ) this.elements =  new Hashtable<String, AttrCode>();
			this.elements.put(attrCode.getCode(), attrCode);
		}
		
		public String toString() {
			return "GROUP_CODE:" + this.code + "  TEXT: " + this.text + "  ORDER: " + this.order + "\n"
				+ this.elements;
		}
	}
	
	
	class AttrCode {
		public AttrCode() {}
		
		
		@Getter @Setter protected String code;
		
		@Getter @Setter private String groupCode;
		
		@Getter @Setter protected int order;
		@Getter @Setter protected String text;
		@Getter @Setter protected boolean deprecated;
		
		public String toString() {
			return "\n"
					+ "\tCODE:" + this.code + "  TEXT: " + this.text + "  ORDER: " + this.order + "\n";
		}
	}
	
	public static void main(String[] args) throws Exception {
		AttrCodeManager man = new AttrCodeManager();
		man.exec();
	}
	public void exec() {
		List<Data> dataList = generateData();
		
		if ( dataList==null || dataList.size()<1 ) return;
		
		this.groups = new Hashtable<String, AttrCodeGroup>();
		for ( Data data : dataList ) {
			String groupCodeStr = data.groupId;
			
			AttrCodeGroup codeGroup = this.groups.get(groupCodeStr);
			if ( codeGroup==null ) {
				codeGroup = new AttrCodeGroup();
				codeGroup.setCode(groupCodeStr);
				codeGroup.setOrder(data.groupDisplayOrder);
				codeGroup.setText(data.groupName);
				
				this.groups.put(codeGroup.getCode(), codeGroup);
			}
			
			AttrCode attrCode = new AttrCode();
			attrCode.setCode(data.codeId);
			attrCode.setOrder(data.codeDisplayOrder);
			attrCode.setText(data.codeName);
			
			codeGroup.addElement(attrCode);
			
			
		}
		
		System.out.println(this.groups);
	}
	
	
	
	
	class Data {
		String codeId;
		String codeName;
		int codeDisplayOrder;
		
		String groupId;
		String groupName;
		int groupDisplayOrder;
		
		String deprecatedYn;
		
		public Data(String code, String text, int order, String groupCode, String groupText, int groupOrder, String deprecatedYn) {
			this.codeId = code;
			this.codeName = text;
			this.codeDisplayOrder = order;
			
			this.groupId = groupCode;
			this.groupName = groupText;
			this.groupDisplayOrder = groupOrder;
			
			this.deprecatedYn = deprecatedYn;
		}
			
	}
	
	public List<Data> generateData() {
		List<Data> dataList = new ArrayList<Data>();
		
		dataList.add( new Data("AREA.1",	"치위생사"		, 1,	"AREA"	,	"채용부문"	,	3,	"N")	);
		dataList.add( new Data("AREA.2",	"간호조무사"	, 2,	"AREA"	,	"채용부문"	,	3,	"N")	);
		dataList.add( new Data("AREA.3",	"치기공사"		, 3,	"AREA"	,	"채용부문"	,	3,	"N")	);
		dataList.add( new Data("AREA.4",	"보험청구사"	, 4,	"AREA"	,	"채용부문"	,	3,	"N")	);
		dataList.add( new Data("AREA.5",	"코디네이터"	, 5,	"AREA"	,	"채용부문"	,	3,	"N")	);
		dataList.add( new Data("AREA.6",	"의사"			, 6,	"AREA"	,	"채용부문"	,	3,	"N")	);
		dataList.add( new Data("AREA.7",	"기타"			, 7,	"AREA"	,	"채용부문"	,	3,	"N")	);
		dataList.add( new Data("EMP.1"	,	"정규직"		, 1,	"EMP"	,	"고용형태"	,	1,	"N")	);
		dataList.add( new Data("EMP.2"	,	"계약직"		, 2,	"EMP"	,	"고용형태"	,	1,	"N")	);
		dataList.add( new Data("EMP.3"	,	"아르바이트"	, 3,	"EMP"	,	"고용형태"	,	1,	"N")	);
		dataList.add( new Data("TASK.1",	"진료실"		, 1,	"TASK"	,	"담당업무"	,	2,	"N")	);
		dataList.add( new Data("TASK.2",	"데스크"		, 2,	"TASK"	,	"담당업무"	,	2,	"N")	);
		dataList.add( new Data("TASK.3",	"상담"			, 3,	"TASK"	,	"담당업무"	,	2,	"N")	);
		dataList.add( new Data("TASK.4",	"보험청구"		, 4,	"TASK"	,	"담당업무"	,	2,	"N")	);
		dataList.add( new Data("TASK.5",	"치과내 기공실"	, 5,	"TASK"	,	"담당업무"	,	2,	"N")	);
		dataList.add( new Data("TASK.6",	"치과기공소"	, 6,	"TASK"	,	"담당업무"	,	2,	"N")	);
		dataList.add( new Data("TASK.7",	"관리 및 경영"	, 7,	"TASK"	,	"담당업무"	,	2,	"N")	);
		dataList.add( new Data("TASK.8",	"의사"			, 8,	"TASK"	,	"담당업무"	,	2,	"N")	);
		dataList.add( new Data("TASK.9",	"기타"			, 9,	"TASK"	,	"담당업무"	,	2,	"N")	);
		
		return dataList;
	}

}
*/
