package dentiq2.api.model;

import lombok.Getter;
import lombok.Setter;

@Deprecated
public class LocationCounterOLD extends Location {
	@Getter @Setter private String adType;
	@Getter @Setter private Long cnt;
}
