package enqual.common.juso;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AddrCoordinateResults {
	@Getter @Setter AddrResultCommon	common;
	@Getter @Setter AddrCoordinate[]	juso;
}
