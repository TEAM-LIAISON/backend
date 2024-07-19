package liaison.linkit.member.domain.repository;

import liaison.linkit.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialLoginId(String socialLoginId);

    boolean existsByEmail(String email);

    @Modifying
    @Query("""
           DELETE FROM Member member WHERE member.id = :memberId
            """)
    void deleteByMemberId(@Param("memberId") final Long memberId);
}
