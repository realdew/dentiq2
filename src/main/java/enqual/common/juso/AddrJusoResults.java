package enqual.common.juso;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AddrJusoResults {
	@Getter @Setter AddrResultCommon	common;
	@Getter @Setter AddrJuso[]			juso;
}
