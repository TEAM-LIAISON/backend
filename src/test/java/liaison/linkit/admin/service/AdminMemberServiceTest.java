package liaison.linkit.admin.service;

import liaison.linkit.admin.domain.AdminMember;
import liaison.linkit.admin.domain.repository.AdminMemberRepository;
import liaison.linkit.admin.domain.type.AdminType;
import liaison.linkit.admin.dto.request.AdminMemberCreateRequest;
import liaison.linkit.admin.dto.request.PasswordUpdateRequest;
import liaison.linkit.admin.infrastructure.PasswordEncoder;
import liaison.linkit.global.exception.AdminException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdminMemberServiceTest {

    @InjectMocks
    private AdminMemberService adminMemberService;

    @Mock
    private AdminMemberRepository adminMemberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("관리자 멤버를 생성한 후 관리자 id를 반환한다.")
    @Test
    void createAdminMember() {
        // given
        final AdminMemberCreateRequest request = new AdminMemberCreateRequest(
                "username",
                "password",
                "ADMIN"
        );
        final AdminMember adminMember = new AdminMember(1L, "username", "password, ", AdminType.ADMIN);

        given(adminMemberRepository.existsByUsername("username")).willReturn(false);
        given(adminMemberRepository.save(any(AdminMember.class))).willReturn(adminMember);
        given(passwordEncoder.encode("password")).willReturn(anyString());
        // when
        final Long actualId = adminMemberService.createAdminMember(request);

        // then
        assertThat(actualId).isEqualTo(1L);
    }

    @DisplayName("관리자 계정의 비밀번호를 변경한다.")
    @Test
    void updatePassword() {
        // given
        final PasswordUpdateRequest request = new PasswordUpdateRequest("password", "newPassword");
        final AdminMember adminMember = new AdminMember(1L, "username", "password", AdminType.ADMIN);

        given(adminMemberRepository.findById(anyLong())).willReturn(Optional.of(adminMember));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(passwordEncoder.encode("newPassword")).willReturn(anyString());

        // when
        adminMemberService.updatePassword(adminMember.getId(), request);

        // then
        verify(adminMemberRepository).save(any(AdminMember.class));
    }

    @DisplayName("기존 비밀번호가 일치하지 않으면 비밀번호를 변경할 수 없다.")
    @Test
    void updatePassword_invalidPassword() {
        // given
        final PasswordUpdateRequest request = new PasswordUpdateRequest("password", "newPassword");
        final AdminMember adminMember = new AdminMember(1L, "username", "password", AdminType.ADMIN);

        given(adminMemberRepository.findById(anyLong())).willReturn(Optional.of(adminMember));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> adminMemberService.updatePassword(adminMember.getId(), request))
                .isInstanceOf(AdminException.class);
    }
}
