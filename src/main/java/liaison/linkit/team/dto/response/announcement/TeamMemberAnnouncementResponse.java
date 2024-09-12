package liaison.linkit.team.dto.response.announcement;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class  TeamMemberAnnouncementResponse {

    private final Long id;
    private final String teamLogoImageUrl;
    private final String teamName;
    private final String mainBusiness;
    private final String applicationProcess;
    private final Boolean isTeamSaved;

    public static TeamMemberAnnouncementResponse of(
            final String teamLogoImageUrl,
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final String teamName
    ) {
        return new TeamMemberAnnouncementResponse(
                teamMemberAnnouncement.getId(),
                teamLogoImageUrl,
                teamName,
                teamMemberAnnouncement.getMainBusiness(),
                teamMemberAnnouncement.getApplicationProcess(),
                false
        );
    }

    public static TeamMemberAnnouncementResponse afterLogin(
            final String teamLogoImageUrl,
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final String teamName,
            final Boolean isTeamSaved
    ) {
        return new TeamMemberAnnouncementResponse(
                teamMemberAnnouncement.getId(),
                teamLogoImageUrl,
                teamName,
                teamMemberAnnouncement.getMainBusiness(),
                teamMemberAnnouncement.getApplicationProcess(),
                isTeamSaved
        );
    }
}
