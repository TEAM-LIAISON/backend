package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.request.AntecedentsCreateRequest;
import liaison.linkit.profile.dto.request.AntecedentsUpdateRequest;
import liaison.linkit.profile.dto.response.AntecedentsResponse;
import liaison.linkit.profile.service.AntecedentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AntecedentsController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class AntecedentsControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AntecedentsService antecedentsService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        given(antecedentsService.validateAntecedentsByMember(1L)).willReturn(1L);
    }

    private void makeAntecedents() throws Exception {
        final AntecedentsCreateRequest antecedentsCreateRequest = new AntecedentsCreateRequest(
                "오더이즈",
                "프로젝트 매니저",
                2023,
                3,
                2023,
                6
        );
    }

    // 단일 이력 항목 조회 테스트
    private ResultActions performGetRequest() throws Exception {
        return mockMvc.perform(
                get("/antecedents")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    // 단일 이력 항목 등록 테스트
    private ResultActions performPostRequest(final AntecedentsCreateRequest createRequest) throws Exception {
        return mockMvc.perform(
                post("/antecedents")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
        );
    }

    // 단일 이력 항목 수정 테스트
    private ResultActions performPutUpdateRequest(final AntecedentsUpdateRequest updateRequest) throws Exception {
        return mockMvc.perform(
                put("/antecedents")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
        );
    }

    // 단일 이력 항목 삭제 테스트
    private ResultActions performDeleteRequest() throws Exception {
        return mockMvc.perform(
                delete("/antecedents")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("이력 항목을 조회할 수 있다.")
    @Test
    void getAntecedents() throws Exception {
        // given
        final AntecedentsResponse response = new AntecedentsResponse(
                1L,
                "오더이즈",
                "프로젝트 매니저",
                2023,
                3,
                2023,
                6
        );

        given(antecedentsService.getAntecedentsDetail(1L))
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
                                        fieldWithPath("id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("이력 항목 ID")
                                                .attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("projectName")
                                                .type(JsonFieldType.STRING)
                                                .description("기업명(프로젝트명)")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("projectRole")
                                                .type(JsonFieldType.STRING)
                                                .description("직무(역할)")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("startYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("시작 연도")
                                                .attributes(field("constraint", "4자리 숫자")),
                                        fieldWithPath("startMonth")
                                                .type(JsonFieldType.NUMBER)
                                                .description("시작 월")
                                                .attributes(field("constraint", "1부터 12까지의 숫자 중에서 선택")),
                                        fieldWithPath("endYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("종료 연도")
                                                .attributes(field("constraint", "4자리 숫자")),
                                        fieldWithPath("endMonth")
                                                .type(JsonFieldType.NUMBER)
                                                .description("종료 월")
                                                .attributes(field("constraint", "1부터 12까지의 숫자 중에서 선택"))
                                )
                        )
                );
    }

    @DisplayName("단일 이력 항목을 생성할 수 있다.")
    @Test
    void createAntecedents() throws Exception {

        // given
        final AntecedentsCreateRequest antecedentsCreateRequest = new AntecedentsCreateRequest(
                "오더이즈",
                "프로젝트 매니저",
                2023,
                3,
                2023,
                6
        );

        final AntecedentsResponse antecedentsResponse = new AntecedentsResponse(
                1L,
                "오더이즈",
                "프로젝트 매니저",
                2023,
                3,
                2023,
                6
        );

        when(antecedentsService.save(anyLong(), any(AntecedentsCreateRequest.class)))
                .thenReturn(antecedentsResponse);

        // when
        final ResultActions resultActions = performPostRequest(antecedentsCreateRequest);

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
                                requestFields(
                                        fieldWithPath("projectName")
                                                .type(JsonFieldType.STRING)
                                                .description("기업명(프로젝트명)")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("projectRole")
                                                .type(JsonFieldType.STRING)
                                                .description("직무(역할)")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("startYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("시작 연도")
                                                .attributes(field("constraint", "4자리 숫자")),
                                        fieldWithPath("startMonth")
                                                .type(JsonFieldType.NUMBER)
                                                .description("시작 월")
                                                .attributes(field("constraint", "1부터 12까지의 숫자 중에서 선택")),
                                        fieldWithPath("endYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("종료 연도")
                                                .attributes(field("constraint", "4자리 숫자")),
                                        fieldWithPath("endMonth")
                                                .type(JsonFieldType.NUMBER)
                                                .description("종료 월")
                                                .attributes(field("constraint", "1부터 12까지의 숫자 중에서 선택"))
                                ),
                                responseFields(
                                        fieldWithPath("id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("이력 ID"),
                                        fieldWithPath("projectName")
                                                .type(JsonFieldType.STRING)
                                                .description("기업명(프로젝트명)"),
                                        fieldWithPath("projectRole")
                                                .type(JsonFieldType.STRING)
                                                .description("직무(역할)"),
                                        fieldWithPath("startYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("시작 연도"),
                                        fieldWithPath("startMonth")
                                                .type(JsonFieldType.NUMBER)
                                                .description("시작 월"),
                                        fieldWithPath("endYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("종료 연도"),
                                        fieldWithPath("endMonth")
                                                .type(JsonFieldType.NUMBER)
                                                .description("종료 월")
                                )
                        )
                );
    }

//    @DisplayName("단일 이력 항목을 수정할 수 있다.")
//    @Test
//    void updateAntecedents() throws Exception {
//        // given
//        final AntecedentsUpdateRequest updateRequest = new AntecedentsUpdateRequest(
//                "피드미",
//                "개발자",
//                2023,
//                8,
//                2024,
//                1,
//                "개인 맞춤형 뉴스 큐레이션 플랫폼"
//        );
//
//        doNothing().when(antecedentsService).update(anyLong(), any(AntecedentsUpdateRequest.class));
//
//        // when
//        final ResultActions resultActions = performPutUpdateRequest(updateRequest);
//
//        // then
//        resultActions.andExpect(status().isNoContent())
//                .andDo(
//                        restDocs.document(
//                                requestCookies(
//                                        cookieWithName("refresh-token")
//                                                .description("갱신 토큰")
//                                ),
//                                requestHeaders(
//                                        headerWithName("Authorization")
//                                                .description("access token")
//                                                .attributes(field("constraint", "문자열(jwt)"))
//                                ),
//                                requestFields(
//                                        fieldWithPath("projectName")
//                                                .type(JsonFieldType.STRING)
//                                                .description("기업명(프로젝트명)")
//                                                .attributes(field("constraint", "문자열")),
//                                        fieldWithPath("projectRole")
//                                                .type(JsonFieldType.STRING)
//                                                .description("직무(역할)")
//                                                .attributes(field("constraint", "문자열")),
//                                        fieldWithPath("startYear")
//                                                .type(JsonFieldType.NUMBER)
//                                                .description("시작 연도")
//                                                .attributes(field("constraint", "4자리 숫자")),
//                                        fieldWithPath("startMonth")
//                                                .type(JsonFieldType.NUMBER)
//                                                .description("시작 월")
//                                                .attributes(field("constraint", "1부터 12까지의 숫자 중에서 선택")),
//                                        fieldWithPath("endYear")
//                                                .type(JsonFieldType.NUMBER)
//                                                .description("종료 연도")
//                                                .attributes(field("constraint", "4자리 숫자")),
//                                        fieldWithPath("endMonth")
//                                                .type(JsonFieldType.NUMBER)
//                                                .description("종료 월")
//                                                .attributes(field("constraint", "1부터 12까지의 숫자 중에서 선택")),
//                                        fieldWithPath("antecedentsDescription")
//                                                .type(JsonFieldType.STRING)
//                                                .description("이력 설명")
//                                                .attributes(field("constraint", "문자열"))
//                                )
//                        )
//                );
//    }

    @DisplayName("단일 이력 항목을 삭제할 수 있다.")
    @Test
    void deleteAntecedents() throws Exception {
        // given
        makeAntecedents();
        doNothing().when(antecedentsService).delete(anyLong());

        // when
        final ResultActions resultActions = performDeleteRequest();

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
                                )
                        )
                );
    }
}
