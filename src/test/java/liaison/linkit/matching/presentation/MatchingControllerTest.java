package liaison.linkit.matching.presentation;


import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.matching.domain.type.MatchingType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.matching.dto.response.ReceivedMatchingResponse;
import liaison.linkit.matching.dto.response.RequestMatchingResponse;
import liaison.linkit.matching.dto.response.SuccessMatchingResponse;
import liaison.linkit.matching.dto.response.existence.ExistenceProfileResponse;
import liaison.linkit.matching.dto.response.messageResponse.ReceivedPrivateMatchingMessageResponse;
import liaison.linkit.matching.dto.response.requestPrivateMatching.MyPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.requestTeamMatching.MyTeamMatchingResponse;
import liaison.linkit.matching.dto.response.toPrivateMatching.ToPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.toTeamMatching.ToTeamMatchingResponse;
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
    ) throws Exception {
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
            final int teamMemberAnnouncementId,
            final MatchingCreateRequest matchingCreateRequest
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/team/profile/matching/team/{teamMemberAnnouncementId}", teamMemberAnnouncementId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchingCreateRequest))
        );
    }

    // 내 이력서에서 팀 소개서로 매칭 요청
    private ResultActions performPrivateProfileMatchingToTeam(
            final int teamMemberAnnouncementId,
            final MatchingCreateRequest matchingCreateRequest
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/private/profile/matching/team/{teamMemberAnnouncementId}", teamMemberAnnouncementId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchingCreateRequest))
        );
    }

    private ResultActions performGetReceivedMatching() throws Exception {
        return mockMvc.perform(
                get("/matching/received")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performGetMyRequestMatching() throws Exception {
        return mockMvc.perform(
                get("/matching/request")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performGetMySuccessMatching() throws Exception {
        return mockMvc.perform(
                get("/matching/success")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performGetExistenceProfile() throws Exception {
        return mockMvc.perform(
                get("/existence/profile")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performGetReceivedPrivateToPrivateMatchingMessage(
            final int privateMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/received/private_to_private/matching/{privateMatchingId}", privateMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }


    private ResultActions performGetReceivedTeamToPrivateMatchingMessage(
            final int privateMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/received/team_to_private/matching/{privateMatchingId}", privateMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetReceivedPrivateToTeamMatchingResponse(
            final int teamMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/received/team_to_team/matching/{teamMatchingId}", teamMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetRequestPrivateToPrivateMatchingMessage(
            final int privateMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/request/private_to_private/matching/{privateMatchingId}", privateMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }


    private ResultActions performGetRequestTeamToPrivateMatchingMessage(
            final int privateMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/request/team_to_private/matching/{privateMatchingId}", privateMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetRequestPrivateToTeamMatchingMessage(
            final int teamMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/request/private_to_team/matching/{teamMatchingId}", teamMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
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
                                parameterWithName("teamMemberAnnouncementId")
                                        .description("팀원 공고 ID")
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
                                parameterWithName("teamMemberAnnouncementId")
                                        .description("팀원 공고 ID")
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
    void getReceivedMatching() throws Exception {
        // given
        final ToPrivateMatchingResponse firstToPrivateMatchingResponse = new ToPrivateMatchingResponse(
                1L,
                "김동혁",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false // 이력서 수신 여부
        );

        final ToPrivateMatchingResponse secondToPrivateMatchingResponse = new ToPrivateMatchingResponse(
                2L,
                "권동민",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false // 이력서 수신 여부
        );

        final List<ToPrivateMatchingResponse> toPrivateMatchingResponseList = Arrays.asList(firstToPrivateMatchingResponse, secondToPrivateMatchingResponse);

        final ToTeamMatchingResponse firstToTeamMatchingResponse = new ToTeamMatchingResponse(
                1L,
                "링킷",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2023, 12, 10),
                SenderType.TEAM,
                MatchingType.TEAM_PROFILE,
                true // 팀 소개서 수신 여부
        );

        final ToTeamMatchingResponse secondToTeamMatchingResponse = new ToTeamMatchingResponse(
                2L,
                "링컬쳐",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2022, 10, 10),
                SenderType.TEAM,
                MatchingType.TEAM_PROFILE,
                true // 팀 소개서 수신 여부
        );

        final List<ToTeamMatchingResponse> toTeamMatchingResponseList = Arrays.asList(firstToTeamMatchingResponse, secondToTeamMatchingResponse);

        List<ReceivedMatchingResponse> receivedMatchingResponses = ReceivedMatchingResponse.toReceivedMatchingResponse(
                toPrivateMatchingResponseList,
                toTeamMatchingResponseList
        );

        given(matchingService.getReceivedMatching(1L)).willReturn(receivedMatchingResponses);

        // when
        final ResultActions resultActions = performGetReceivedMatching();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestCookies(
                                        cookieWithName("refresh-token").description("갱신 토큰")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization").description("access token").attributes(field("constraint", "문자열(jwt)"))
                                ),
                                responseFields(
                                        fieldWithPath("[].receivedMatchingId").type(JsonFieldType.NUMBER).description("내 이력서/팀 소개서에 매칭 PK ID"),
                                        fieldWithPath("[].senderName").type(JsonFieldType.STRING).description("발신자 이름"),
                                        fieldWithPath("[].requestMessage").type(JsonFieldType.STRING).description("매칭 요청 메시지"),
                                        fieldWithPath("[].requestOccurTime").type(JsonFieldType.STRING).description("매칭 요청 발생 날짜"),
                                        fieldWithPath("[].senderType").type(JsonFieldType.STRING).description("발신자 이력/소개서 타입"),
                                        fieldWithPath("[].matchingType").type(JsonFieldType.STRING).description("매칭 요청 타입"),
                                        fieldWithPath("[].receivedTeamProfile").type(JsonFieldType.BOOLEAN).description("이력/소개서 수신 여부")
                                )
                        )
                );
    }


    @DisplayName("내가 보낸 매칭을 전체 조회할 수 있다.")
    @Test
    void getMyRequestMatching() throws Exception {
        // given
        final MyPrivateMatchingResponse firstMyPrivateMatchingResponse = new MyPrivateMatchingResponse(
                1L,
                "주서영",
                "주서영님의 내 이력서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false
        );

        final MyPrivateMatchingResponse secondMyPrivateMatchingResponse = new MyPrivateMatchingResponse(
                2L,
                "주은강",
                "주은강님의 내 이력서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2024, 8, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false
        );

        final List<MyPrivateMatchingResponse> myPrivateMatchingResponseList = Arrays.asList(firstMyPrivateMatchingResponse, secondMyPrivateMatchingResponse);

        final MyTeamMatchingResponse firstMyTeamMatchingResponse = new MyTeamMatchingResponse(
                1L,
                "링컬쳐",
                "링컬쳐님의 팀 소개서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.TEAM_PROFILE,
                true
        );

        final MyTeamMatchingResponse secondMyTeamMatchingResponse = new MyTeamMatchingResponse(
                2L,
                "하이브",
                "하이브님의 팀 소개서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2023, 10, 10),
                SenderType.PRIVATE,
                MatchingType.TEAM_PROFILE,
                true
        );

        final List<MyTeamMatchingResponse> myTeamMatchingResponseList = Arrays.asList(firstMyTeamMatchingResponse, secondMyTeamMatchingResponse);

        final List<RequestMatchingResponse> requestMatchingResponseList = RequestMatchingResponse.requestMatchingResponseList(
                myPrivateMatchingResponseList,
                myTeamMatchingResponseList
        );

        given(matchingService.getMyRequestMatching(1L)).willReturn(requestMatchingResponseList);

        // when
        final ResultActions resultActions = performGetMyRequestMatching();

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
                                        fieldWithPath("[].requestMatchingId").description("매칭 요청 보낸 항목 PK ID"),
                                        fieldWithPath("[].receiverName").description("발신자 이름"),
                                        fieldWithPath("[].requestMessage").description("매칭 요청 메시지"),
                                        fieldWithPath("[].requestOccurTime").description("매칭 요청 발생 날짜").type(JsonFieldType.STRING),
                                        fieldWithPath("[].senderType").description("발신자 요청 타입"),
                                        fieldWithPath("[].matchingType").description("매칭 요청 타입"),
                                        fieldWithPath("[].requestTeamProfile").type(JsonFieldType.BOOLEAN).description("이력/소개서 발신 여부")
                                )
                        )
                );
    }

    @DisplayName("내가 성사된 매칭을 전체 조회할 수 있다.")
    @Test
    void getMySuccessMatching() throws Exception {
        // given
        final ToPrivateMatchingResponse firstToPrivateMatchingResponse = new ToPrivateMatchingResponse(
                1L,
                "김동혁",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false
        );

        final ToPrivateMatchingResponse secondToPrivateMatchingResponse = new ToPrivateMatchingResponse(
                2L,
                "권동민",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false
        );

        final List<ToPrivateMatchingResponse> toPrivateMatchingResponseList = Arrays.asList(firstToPrivateMatchingResponse, secondToPrivateMatchingResponse);

        final ToTeamMatchingResponse firstToTeamMatchingResponse = new ToTeamMatchingResponse(
                1L,
                "링킷",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2023, 12, 10),
                SenderType.TEAM,
                MatchingType.TEAM_PROFILE,
                true
        );

        final ToTeamMatchingResponse secondToTeamMatchingResponse = new ToTeamMatchingResponse(
                2L,
                "링컬쳐",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2022, 10, 10),
                SenderType.TEAM,
                MatchingType.TEAM_PROFILE,
                true
        );

        final List<ToTeamMatchingResponse> toTeamMatchingResponseList = Arrays.asList(firstToTeamMatchingResponse, secondToTeamMatchingResponse);

        final MyPrivateMatchingResponse firstMyPrivateMatchingResponse = new MyPrivateMatchingResponse(
                1L,
                "주서영",
                "주서영님의 내 이력서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false
        );

        final MyPrivateMatchingResponse secondMyPrivateMatchingResponse = new MyPrivateMatchingResponse(
                2L,
                "주은강",
                "주은강님의 내 이력서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2024, 8, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false
        );

        final List<MyPrivateMatchingResponse> myPrivateMatchingResponseList = Arrays.asList(firstMyPrivateMatchingResponse, secondMyPrivateMatchingResponse);

        final MyTeamMatchingResponse firstMyTeamMatchingResponse = new MyTeamMatchingResponse(
                1L,
                "링컬쳐",
                "링컬쳐님의 팀 소개서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.TEAM_PROFILE,
                true
        );

        final MyTeamMatchingResponse secondMyTeamMatchingResponse = new MyTeamMatchingResponse(
                2L,
                "하이브",
                "하이브님의 팀 소개서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2023, 10, 10),
                SenderType.PRIVATE,
                MatchingType.TEAM_PROFILE,
                true
        );

        final List<MyTeamMatchingResponse> myTeamMatchingResponseList = Arrays.asList(firstMyTeamMatchingResponse, secondMyTeamMatchingResponse);

        final List<SuccessMatchingResponse> successMatchingResponses = SuccessMatchingResponse.successMatchingResponseList(
                toPrivateMatchingResponseList,
                toTeamMatchingResponseList,
                myPrivateMatchingResponseList,
                myTeamMatchingResponseList
        );

        given(matchingService.getMySuccessMatching(1L)).willReturn(successMatchingResponses);

        // when
        final ResultActions resultActions = performGetMySuccessMatching();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization").description("Access token for security.")
                        ),
                        responseFields(
                                subsectionWithPath("[]").description("모든 성사 매칭 배열")
                        ).andWithPrefix("[].",
                                fieldWithPath("successMatchingMemberName").description("매칭 성사자 이름/팀명"),
                                fieldWithPath("requestMessage").description("매칭 요청 메시지"),
                                fieldWithPath("requestOccurTime").description("매칭 요청 발생 시간")
                        )
                ));
    }

    @DisplayName("내가 매칭 요청을 보낼 내 이력서의 true/false 값을 판단할 수 있다.")
    @Test
    void getExistenceProfile() throws Exception {
        // given
        final ExistenceProfileResponse existenceProfileResponse = new ExistenceProfileResponse(true, true);
        given(matchingService.getExistenceProfile(1L)).willReturn(existenceProfileResponse);
        // when
        final ResultActions resultActions = performGetExistenceProfile();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("isPrivateProfileMatchingAllow").type(JsonFieldType.BOOLEAN).description("내 이력서로 매칭 요청 가능 여부 true -> 80% 이상"),
                                fieldWithPath("isTeamProfileMatchingAllow").type(JsonFieldType.BOOLEAN).description("팀 소개서로 매칭 요청 가능 여부 true -> 80% 이상")
                        )
                ));
    }

    @DisplayName("내가 받은 매칭 요청 / sender_type = Private / receivedTeamProfile = false")
    @Test
    void getReceivedPrivateToPrivateMatchingMessage() throws Exception {
        // given
        final ReceivedPrivateMatchingMessageResponse receivedPrivateMatchingMessageResponse = new ReceivedPrivateMatchingMessageResponse(
                1L,
                "권동민",
                Arrays.asList("개발·데이터"),
                "권동민님이 나에게 보낸 매칭 요청 메시지입니다.",
                false
        );

        given(matchingService.getReceivedPrivateToPrivateMatchingMessage(1L)).willReturn(receivedPrivateMatchingMessageResponse);

        // when
        final ResultActions resultActions = performGetReceivedPrivateToPrivateMatchingMessage(1);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("privateMatchingId")
                                        .description("내 이력서 대상 매칭 PK")
                        ),
                        responseFields(
                                fieldWithPath("receivedMatchingId").type(JsonFieldType.NUMBER).description("내 이력서/팀 소개서에 매칭 PK ID"),
                                fieldWithPath("senderName").type(JsonFieldType.STRING).description("발신자 이름"),
                                fieldWithPath("jobRoleNames").type(JsonFieldType.ARRAY).description("발신자의 희망 역할 및 직무"),
                                fieldWithPath("requestMessage").type(JsonFieldType.STRING).description("매칭 요청 메시지"),
                                fieldWithPath("receivedTeamProfile").type(JsonFieldType.BOOLEAN).description("이력/소개서 수신 여부")
                        )
                ));
    }
}
