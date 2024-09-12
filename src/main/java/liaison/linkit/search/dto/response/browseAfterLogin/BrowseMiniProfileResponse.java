package liaison.linkit.search.dto.response.browseAfterLogin;

import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class BrowseMiniProfileResponse {

    private final Long id;
    private final String profileTitle;
    private final String miniProfileImg;
    private final Boolean isActivate;
    private final List<String> myKeywordNames;
    private final String memberName;
    private final Boolean isPrivateSaved;

    public static BrowseMiniProfileResponse personalBrowseMiniProfile(
            final MiniProfile miniProfile,
            final List<MiniProfileKeyword> miniProfileKeywords,
            final String memberName,
            final Boolean isPrivateSaved
    ) {
        List<String> myKeywordNames = miniProfileKeywords.stream()
                .map(MiniProfileKeyword::getMyKeywordNames) // 올바른 메서드 참조 사용
                .collect(Collectors.toList());

        return new BrowseMiniProfileResponse(
                miniProfile.getId(),
                miniProfile.getProfileTitle(),
                miniProfile.getMiniProfileImg(),
                miniProfile.isActivate(),
                myKeywordNames,
                memberName,
                isPrivateSaved
        );
    }
}
