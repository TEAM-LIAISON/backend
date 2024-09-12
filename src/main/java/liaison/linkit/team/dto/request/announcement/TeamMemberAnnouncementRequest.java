package liaison.linkit.team.dto.request.announcement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamMemberAnnouncementRequest {
    // 2. 주요 업무
    private final String mainBusiness;
    // 4. 지원 절차
    private final String applicationProcess;
}
