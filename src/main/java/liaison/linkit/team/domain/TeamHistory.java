package liaison.linkit.team.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TeamHistory {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    
    private String historyName;
    private String historyStartDate;
    private String historyEndDate;
    private boolean isHistoryInProgress;
    private String historyDescription;
    private Boolean isHistoryCertified;
    private Boolean isHistoryVerified;
    private String historyCertificationAttachFileName;
    private String historyCertificationAttachFilePath;
    private String historyCertificationDescription;

}
