package liaison.linkit.admin.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.admin.domain.AdminMember;
import liaison.linkit.admin.domain.type.AdminType;
import liaison.linkit.admin.dto.request.AdminLoginRequest;
import liaison.linkit.admin.service.AdminLoginService;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.dto.AccessTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static liaison.linkit.admin.domain.type.AdminType.ADMIN;
import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminLoginController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class AdminLoginControllerTest extends ControllerTest {

    private final static String REFRESH_TOKEN = "refreshToken";
    private final static String ACCESS_TOKEN = "accessToken";
    private final static String RENEW_ACCESS_TOKEN = "I'mNewAccessToken!";

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminLoginService adminLoginService;

    @DisplayName("관리자 로그인을 할 수 있다.")
    @Test
    void login() throws Exception {
        // given
        final AdminLoginRequest adminLoginRequest = new AdminLoginRequest("username", "password");
        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, ACCESS_TOKEN);

        when(adminLoginService.login(any(AdminLoginRequest.class)))
                .thenReturn(memberTokens);

        final ResultActions resultActions = mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLoginRequest))
        );

        // when
        final MvcResult mvcResult = resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("username")
                                        .type(JsonFieldType.STRING)
                                        .description("사용자 이름")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("password")
                                        .type(JsonFieldType.STRING)
                                        .description("비밀번호")
                                        .attributes(field("constraint", "문자열"))
                        ),
                        responseFields(
                                fieldWithPath("accessToken")
                                        .type(JsonFieldType.STRING)
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        )
                ))
                .andReturn();

        final AccessTokenResponse expected = new AccessTokenResponse(memberTokens.getAccessToken());

        final AccessTokenResponse actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                AccessTokenResponse.class
        );

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("accessToken 재발급을 통해 관리자 로그인을 연장할 수 있다.")
    @Test
    void extendLogin() throws Exception {
        // given
        given(adminMemberRepository.existsByIdAndAdminType(anyLong(), any(AdminType.class)))
                .willReturn(false);

        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refresh-token", memberTokens.getRefreshToken());

        when(adminLoginService.renewalAccessToken(REFRESH_TOKEN, ACCESS_TOKEN))
                .thenReturn(RENEW_ACCESS_TOKEN);

        // when
        final ResultActions resultActions = mockMvc.perform(post("/admin/token")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, ACCESS_TOKEN)
                .cookie(cookie)
        );

        final MvcResult mvcResult = resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token")
                                        .description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        ),
                        responseFields(
                                fieldWithPath("accessToken")
                                        .type(JsonFieldType.STRING)
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        )
                ))
                .andReturn();

        final AccessTokenResponse expected = new AccessTokenResponse(memberTokens.getAccessToken());

        final AccessTokenResponse actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                AccessTokenResponse.class
        );

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("관리자 멤버의 refreshToken을 삭제하고 로그아웃 할 수 있다.")
    @Test
    void logout() throws Exception {
        // given
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(adminLoginService).removeRefreshToken(anyString());
        given(adminMemberRepository.findById(1L)).willReturn(Optional.of(new AdminMember(
                1L,
                "username",
                "password",
                ADMIN
        )));

        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refresh-token", memberTokens.getRefreshToken());

        // when
        final ResultActions resultActions = mockMvc.perform(delete("/admin/logout")
                .header(AUTHORIZATION, ACCESS_TOKEN)
                .cookie(cookie)
        );

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token")
                                        .description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        )
                ));

        // then
        verify(adminLoginService).removeRefreshToken(anyString());
    }
}
