package liaison.linkit.profile.dto.request.education;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EducationCreateRequest {

    @NotNull(message = "입학 연도를 입력해주세요")
    private final int admissionYear;
    private final int graduationYear;

    // 학교명
    @NotNull(message = "학교 이름을 입력해주세요")
    private final String universityName;

    // 전공명
    @NotNull(message = "전공명을 입력해주세요")
    private final String majorName;

    // 재학 기간 관련 타입
    @NotNull(message = "학적을 입력해주세요")
    private final String degreeName;
}
