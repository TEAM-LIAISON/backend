package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.domain.MiniProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MiniProfileResponse {

    private final String oneLineIntroduction;
    private final String interests;
    private final String firstFreeText;
    private final String secondFreeText;


    public static MiniProfileResponse of(final MiniProfile miniProfile) {
        return new MiniProfileResponse(
                miniProfile.getOneLineIntroduction(),
                miniProfile.getInterests(),
                miniProfile.getFirstFreeText(),
                miniProfile.getSecondFreeText()
        );
    }

    public static MiniProfileResponse personalMiniProfile(final MiniProfile miniProfile) {
        return new MiniProfileResponse(
                miniProfile.getOneLineIntroduction(),
                miniProfile.getInterests(),
                miniProfile.getFirstFreeText(),
                miniProfile.getSecondFreeText()
        );
    }


}
