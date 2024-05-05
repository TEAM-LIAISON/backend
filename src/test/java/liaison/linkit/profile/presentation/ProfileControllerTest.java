package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import liaison.linkit.profile.dto.response.ProfileResponse;
import liaison.linkit.profile.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class ProfileControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        given(profileService.validateProfileByMember(1L)).willReturn(1L);
    }

    private ResultActions performGetRequest() throws Exception {
        return mockMvc.perform(
                get("/profile")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performPatchRequest(final ProfileUpdateRequest updateRequest) throws Exception {
        return mockMvc.perform(
                patch("/profile")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
        );
    }

    @DisplayName("프로필 자기소개 항목을 조회할 수 있다.")
    @Test
    void getProfileIntroduction() throws Exception{
        // given
        final ProfileResponse response = new ProfileResponse(
                "프로필 자기소개 항목입니다."
        );

        given(profileService.getProfileDetail(1L))
                .willReturn(response);

        // when
        final ResultActions resultActions = performGetRequest();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
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
                                        fieldWithPath("introduction")
                                                .type(JsonFieldType.STRING)
                                                .description("자기 소개")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }


    @DisplayName("프로필 자기소개 항목을 수정할 수 있다.")
    @Test
    void updateProfileIntroduction() throws Exception {
        // given
        final ProfileUpdateRequest updateRequest = new ProfileUpdateRequest(
                "자기소개를 수정하려고 합니다."
        );

        doNothing().when(profileService).update(anyLong(), any(ProfileUpdateRequest.class));

        // when
        final ResultActions resultActions = performPatchRequest(updateRequest);

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                requestCookies(
                                        cookieWithName("refresh-token")
                                                .description("갱신 토큰")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization")
                                                .description("access token")
                                                .attributes(field("constraint", "문자열(jwt)"))
                                ),
                                requestFields(
                                        fieldWithPath("introduction")
                                                .type(JsonFieldType.STRING)
                                                .description("자기 소개")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

//    @DisplayName("프로필 자기소개 항목 수정할 수 있다.")
//    @Test
//    void updateProfile() throws Exception {
//        // given
//
//        final ProfileUpdateRequest updateRequest = new ProfileUpdateRequest(
//
//        );
//
//        doNothing().when(profileService).update(anyLong(), any(ProfileUpdateRequest.class));
//
//        // when
//        final ResultActions resultActions = performPutRequest(updateRequest);
//
//        // then
//        resultActions.andExpect(status().isNoContent())
//                .andDo(
//                        restDocs.document(
//                                requestHeaders(
//                                        headerWithName("Authorization")
//                                                .description("access token")
//                                                .attributes(field("constraint", "문자열(jwt)"))
//                                ),
//                                requestCookies(
//                                        cookieWithName("refresh-token")
//                                                .description("갱신 토큰")
//                                ),
//                                requestFields(
//                                        fieldWithPath("introduction")
//                                                .type(JsonFieldType.STRING)
//                                                .description("자기소개")
//                                                .attributes(field("constraint", "문자열"))
//                                )
//                        )
//                );
//    }

}
