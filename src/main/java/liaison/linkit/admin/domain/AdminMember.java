package liaison.linkit.admin.domain;

import jakarta.persistence.*;
import liaison.linkit.admin.domain.type.AdminType;
import liaison.linkit.member.domain.type.MemberState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE admin_member SET status = 'DELETED' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
public class AdminMember {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(nullable = false)
    private LocalDateTime lastLoginDate;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private AdminType adminType;

    @Enumerated(value = STRING)
    private MemberState status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public AdminMember(final Long id, final String username, final String password, final AdminType adminType) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.lastLoginDate = LocalDateTime.now();
        this.adminType = adminType;
        this.status = MemberState.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public AdminMember(final String username, final String password, final AdminType adminType) {
        this(null, username, password, adminType);
    }
}
