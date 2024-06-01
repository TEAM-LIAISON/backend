package liaison.linkit.profile.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileRegionResponse {
    private final String cityName;
    private final String divisionName;
}
