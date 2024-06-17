package liaison.linkit.profile.domain.Attach;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.dto.request.attach.AttachUrlUpdateRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attach_url")
public class AttachUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attach_url_id")
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    // 웹 링크 이름
    @Column(nullable = false)
    private String attachUrlName;

    @Column(nullable = false)
    private String attachUrl;

    public static AttachUrl of(
            final Profile profile,
            final String attachUrlName,
            final String attachUrl
    ) {
        return new AttachUrl(
                null,
                profile,
                attachUrlName,
                attachUrl
        );
    }

    public void update(final AttachUrlUpdateRequest updateRequest) {
        this.attachUrlName = updateRequest.getAttachUrlName();
        this.attachUrl = updateRequest.getAttachUrl();
    }
}
