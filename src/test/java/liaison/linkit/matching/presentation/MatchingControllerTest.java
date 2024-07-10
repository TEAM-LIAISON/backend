package liaison.linkit.matching.presentation;


import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.matching.domain.type.MatchingType;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.matching.dto.response.ReceivedMatchingResponse;
import liaison.linkit.matching.dto.response.toPrivateMatching.PrivateMatchingResponse;
import liaison.linkit.matching.dto.response.toTeamMatching.TeamMatchingResponse;
import liaison.linkit.matching.service.MatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchingController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class MatchingControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    // 내 이력서에서 내 이력서로 매칭 요청
    private ResultActions performPrivateProfileMatchingToPrivate(
            final int profileId,
            final MatchingCreateRequest matchingCreateRequest
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/private/profile/matching/private/{profileId}", profileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchingCreateRequest))
        );
    }
    // 팀 소개서에서 내 이력서로 매칭 요청
    private ResultActions performTeamProfileMatchingToPrivate(
            final int profileId,
            final MatchingCreateRequest matchingCreateRequest
    ) throws Exception{
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/team/profile/matching/private/{profileId}", profileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchingCreateRequest))
        );
    }

    // 팀 소개서에서 팀 소개서로 매칭 요청
    private ResultActions performTeamProfileMatchingToTeam(
            final int teamProfileId,
            final MatchingCreateRequest matchingCreateRequest
    ) throws Exception{
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/team/profile/matching/team/{teamProfileId}", teamProfileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchingCreateRequest))
        );
    }

    // 내 이력서에서 팀 소개서로 매칭 요청
    private ResultActions performPrivateProfileMatchingToTeam(
            final int teamProfileId,
            final MatchingCreateRequest matchingCreateRequest
    ) throws Exception{
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/private/profile/matching/team/{teamProfileId}", teamProfileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchingCreateRequest))
        );
    }

    private ResultActions performGetReceivedMatching () throws Exception {
        return mockMvc.perform(
                get("/matching/received")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    @DisplayName("내 이력서로 내 이력서에 매칭 요청을 보낼 수 있다.")
    @Test
    void createPrivateProfileMatchingToPrivate() throws Exception {
        // given
        final MatchingCreateRequest matchingCreateRequest = new MatchingCreateRequest(
                "매칭 요청 메시지입니다."
        );

        // when
        final ResultActions resultActions = performPrivateProfileMatchingToPrivate(1, matchingCreateRequest);

        // then
        verify(matchingService).createPrivateProfileMatchingToPrivate(eq(1L), eq(1L), any(MatchingCreateRequest.class));

        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileId")
                                        .description("내 이력서 ID")
                        ),
                        requestFields(
                                fieldWithPath("requestMessage")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 메시지입니다.")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }

    @DisplayName("팀 소개서로 내 이력서에 매칭 요청을 보낼 수 있다.")
    @Test
    void createTeamProfileMatchingToPrivate() throws Exception {
        // given
        final MatchingCreateRequest matchingCreateRequest = new MatchingCreateRequest(
                "매칭 요청 메시지입니다."
        );

        // when
        final ResultActions resultActions = performTeamProfileMatchingToPrivate(1, matchingCreateRequest);

        // then
        verify(matchingService).createTeamProfileMatchingToPrivate(eq(1L), eq(1L), any(MatchingCreateRequest.class));

        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileId")
                                        .description("내 이력서 ID")
                        ),
                        requestFields(
                                fieldWithPath("requestMessage")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 메시지입니다.")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }

    @DisplayName("팀 소개서로 팀 소개서에 매칭 요청을 보낼 수 있다.")
    @Test
    void createTeamProfileMatchingToTeam() throws Exception {
        // given
        final MatchingCreateRequest matchingCreateRequest = new MatchingCreateRequest(
                "매칭 요청 메시지입니다."
        );

        // when
        final ResultActions resultActions = performTeamProfileMatchingToTeam(1, matchingCreateRequest);

        // then
        verify(matchingService).createTeamProfileMatchingToTeam(eq(1L), eq(1L), any(MatchingCreateRequest.class));

        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamProfileId")
                                        .description("팀 소개서 ID")
                        ),
                        requestFields(
                                fieldWithPath("requestMessage")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 메시지입니다.")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }

    @DisplayName("내 이력서로 팀 소개서에 매칭 요청을 보낼 수 있다.")
    @Test
    void createPrivateProfileMatchingToTeam() throws Exception {
        // given
        final MatchingCreateRequest matchingCreateRequest = new MatchingCreateRequest(
                "매칭 요청 메시지입니다."
        );
        // when
        final ResultActions resultActions = performPrivateProfileMatchingToTeam(1, matchingCreateRequest);

        // then
        verify(matchingService).createPrivateProfileMatchingToTeam(eq(1L), eq(1L), any(MatchingCreateRequest.class));

        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamProfileId")
                                        .description("팀 소개서 ID")
                        ),
                        requestFields(
                                fieldWithPath("requestMessage")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 메시지입니다.")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }

    @DisplayName("내가 받은 매칭을 전체 조회할 수 있다.")
    @Test
    void getReceivedMatching () throws Exception {
        // given
        final PrivateMatchingResponse firstPrivateMatchingResponse = new PrivateMatchingResponse(
                "김동혁",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                MatchingType.PROFILE
        );

        final PrivateMatchingResponse secondPrivateMatchingResponse = new PrivateMatchingResponse(
                "권동민",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                MatchingType.PROFILE
        );

        final List<PrivateMatchingResponse> privateMatchingResponseList = Arrays.asList(firstPrivateMatchingResponse, secondPrivateMatchingResponse);

        final TeamMatchingResponse firstTeamMatchingResponse = new TeamMatchingResponse(
                "링킷",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2023, 12 ,10),
                MatchingType.TEAM_PROFILE
        );

        final TeamMatchingResponse secondTeamMatchingResponse = new TeamMatchingResponse(
                "링컬쳐",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2022, 10 ,10),
                MatchingType.TEAM_PROFILE
        );

        final List<TeamMatchingResponse> teamMatchingResponseList = Arrays.asList(firstTeamMatchingResponse, secondTeamMatchingResponse);

        final ReceivedMatchingResponse receivedMatchingResponse = new ReceivedMatchingResponse(
                privateMatchingResponseList,
                teamMatchingResponseList
        );

        given(matchingService.getReceivedMatching(1L)).willReturn(receivedMatchingResponse);
        // when
        final ResultActions resultActions = performGetReceivedMatching();

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
                                        fieldWithPath("privateMatchingResponseList[].senderName").description("발신자 이름"),
                                        fieldWithPath("privateMatchingResponseList[].requestMessage").description("매칭 요청 메시지"),
                                        fieldWithPath("privateMatchingResponseList[].requestOccurTime").description("매칭 요청 발생 날짜").type(JsonFieldType.STRING),
                                        fieldWithPath("privateMatchingResponseList[].matchingType").description("매칭 요청 타입"),
                                        subsectionWithPath("teamMatchingResponseList").description("팀 매칭 응답 리스트")
                                )
                        )
                );
    }
}
