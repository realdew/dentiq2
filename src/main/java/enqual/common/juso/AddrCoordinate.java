package enqual.common.juso;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AddrCoordinate {
	@Getter @Setter String	admCd;		//	String	Y	행정구역코드
	@Getter @Setter String	rnMgtSn;	//	String	Y	도로명코드
	@Getter @Setter String	bdMgtSn;	//	String	Y	건물관리번호
	@Getter @Setter String	udrtYn;		//	String	Y	지하여부(0 : 지상, 1 : 지하)
	@Getter @Setter String	buldMnnm;	//	Number	Y	건물본번
	@Getter @Setter String	buldSlno;	//	Number	Y	건물부번
	
	
	@Getter @Setter String	entX;		//	String	Y	X좌표
	@Getter @Setter String	entY;		//	String	Y	Y좌표
	@Getter @Setter String	bdNm;		//	String	N	건물명
}
