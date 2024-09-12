package liaison.linkit.matching.presentation;


import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.matching.domain.type.MatchingType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.dto.request.AllowMatchingRequest;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.matching.dto.response.ReceivedMatchingResponse;
import liaison.linkit.matching.dto.response.RequestMatchingResponse;
import liaison.linkit.matching.dto.response.SuccessMatchingResponse;
import liaison.linkit.matching.dto.response.contact.SuccessContactResponse;
import liaison.linkit.matching.dto.response.existence.ExistenceProfileResponse;
import liaison.linkit.matching.dto.response.messageResponse.ReceivedPrivateMatchingMessageResponse;
import liaison.linkit.matching.dto.response.messageResponse.ReceivedTeamMatchingMessageResponse;
import liaison.linkit.matching.dto.response.messageResponse.RequestPrivateMatchingMessageResponse;
import liaison.linkit.matching.dto.response.messageResponse.RequestTeamMatchingMessageResponse;
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
import static org.mockito.Mockito.*;
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

    private ResultActions performGetPrivateSuccessContactResponse(
            final int privateMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/success/private/matching/contact/{privateMatchingId}", privateMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetTeamSuccessContactResponse(
            final int teamMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/success/team/matching/contact/{teamMatchingId}", teamMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performPostAcceptReceivePrivateMatching(
            final int privateMatchingId,
            final AllowMatchingRequest allowMatchingRequest
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/allow/private/matching/{privateMatchingId}", privateMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allowMatchingRequest))
        );
    }

    private ResultActions performPostAcceptReceiveTeamMatching(
            final int teamMatchingId,
            final AllowMatchingRequest allowMatchingRequest
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/allow/team/matching/{teamMatchingId}", teamMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allowMatchingRequest))
        );
    }

    private ResultActions performRequestDeletePrivateMatching(
            final int privateMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/delete/request/private/matching/{privateMatchingId}", privateMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performRequestDeleteTeamMatching(
            final int teamMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/delete/request/team/matching/{teamMatchingId}", teamMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performSuccessDeletePrivateMatching(
            final int privateMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/delete/success/private/matching/{privateMatchingId}", privateMatchingId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performSuccessDeleteTeamMatching(
            final int teamMatchingId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/delete/success/team/matching/{teamMatchingId}", teamMatchingId)
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
                1L,
                "https://image.linkit.im/images/linkit_logo.png",
                "김동혁",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false, // 이력서 수신 여부
                false
        );

        final ToPrivateMatchingResponse secondToPrivateMatchingResponse = new ToPrivateMatchingResponse(
                2L,
                2L,
                "https://image.linkit.im/images/linkit_logo.png",
                "권동민",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false, // 이력서 수신 여부
                false
        );

        final List<ToPrivateMatchingResponse> toPrivateMatchingResponseList = Arrays.asList(firstToPrivateMatchingResponse, secondToPrivateMatchingResponse);

        final ToTeamMatchingResponse firstToTeamMatchingResponse = new ToTeamMatchingResponse(
                3L,
                1L,
                "https://image.linkit.im/images/linkit_logo.png",
                "링킷",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2023, 12, 10),
                SenderType.TEAM,
                MatchingType.TEAM_PROFILE,
                true, // 팀 소개서 수신 여부
                false
        );

        final ToTeamMatchingResponse secondToTeamMatchingResponse = new ToTeamMatchingResponse(
                4L,
                2L,
                "https://image.linkit.im/images/linkit_logo.png",
                "링컬쳐",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2022, 10, 10),
                SenderType.TEAM,
                MatchingType.TEAM_PROFILE,
                true, // 팀 소개서 수신 여부
                false
        );

        final List<ToTeamMatchingResponse> toTeamMatchingResponseList = Arrays.asList(firstToTeamMatchingResponse, secondToTeamMatchingResponse);

        List<ReceivedMatchingResponse> receivedMatchingResponses = ReceivedMatchingResponse.toReceivedMatchingResponse(
                toPrivateMatchingResponseList,
                toTeamMatchingResponseList
        );

        given(matchingService.getReceivedMatching(1L)).willReturn(receivedMatchingResponses);

        // when
        final ResultActions resultActions = performGetReceivedMatching();

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
                                        fieldWithPath("[].profileId").type(JsonFieldType.NUMBER).description("miniProfileId / teamMiniProfileId -> senderType에 따라 프론트에서 처리 바람 -> senderType == team 이면 teamMiniProfileId로, Privated이면 miniProfile로"),
                                        fieldWithPath("[].receivedMatchingId").type(JsonFieldType.NUMBER).description("내 이력서/팀 소개서에 매칭 PK ID"),
                                        fieldWithPath("[].miniProfileImg").type(JsonFieldType.STRING).description("발신자 프로필 이미지"),
                                        fieldWithPath("[].senderName").type(JsonFieldType.STRING).description("발신자 이름"),
                                        fieldWithPath("[].requestMessage").type(JsonFieldType.STRING).description("매칭 요청 메시지"),
                                        fieldWithPath("[].requestOccurTime").type(JsonFieldType.STRING).description("매칭 요청 발생 날짜"),
                                        fieldWithPath("[].senderType").type(JsonFieldType.STRING).description("발신자 이력/소개서 타입"),
                                        fieldWithPath("[].matchingType").type(JsonFieldType.STRING).description("매칭 요청 타입"),
                                        fieldWithPath("[].receivedTeamProfile").type(JsonFieldType.BOOLEAN).description("이력/소개서 수신 여부"),
                                        fieldWithPath("[].isReceiverCheck").type(JsonFieldType.BOOLEAN).description("수신자가 확인했는지 여부")
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
                "https://image.linkit.im/images/linkit_logo.png",
                "주서영",
                "주서영님의 내 이력서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false
        );

        final MyPrivateMatchingResponse secondMyPrivateMatchingResponse = new MyPrivateMatchingResponse(
                2L,
                "https://image.linkit.im/images/linkit_logo.png",
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
                "https://image.linkit.im/images/linkit_logo.png",
                "링컬쳐",
                "링컬쳐님의 팀 소개서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.TEAM_PROFILE,
                true
        );

        final MyTeamMatchingResponse secondMyTeamMatchingResponse = new MyTeamMatchingResponse(
                2L,
                "https://image.linkit.im/images/linkit_logo.png",
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
                                        fieldWithPath("[].miniProfileImg").description("발신자 프로필 이미지"),
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
                1L,
                "https://image.linkit.im/images/linkit_logo.png",
                "김동혁",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false,
                false
        );

        final ToPrivateMatchingResponse secondToPrivateMatchingResponse = new ToPrivateMatchingResponse(
                2L,
                2L,
                "https://image.linkit.im/images/linkit_logo.png",
                "권동민",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false,
                false
        );

        final List<ToPrivateMatchingResponse> toPrivateMatchingResponseList = Arrays.asList(firstToPrivateMatchingResponse, secondToPrivateMatchingResponse);

        final ToTeamMatchingResponse firstToTeamMatchingResponse = new ToTeamMatchingResponse(
                3L,
                1L,
                "https://image.linkit.im/images/linkit_logo.png",
                "링킷",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2023, 12, 10),
                SenderType.TEAM,
                MatchingType.TEAM_PROFILE,
                true,
                false
        );

        final ToTeamMatchingResponse secondToTeamMatchingResponse = new ToTeamMatchingResponse(
                4L,
                2L,
                "https://image.linkit.im/images/linkit_logo.png",
                "링컬쳐",
                "매칭 요청 메시지입니다.",
                LocalDate.of(2022, 10, 10),
                SenderType.TEAM,
                MatchingType.TEAM_PROFILE,
                true,
                false
        );

        final List<ToTeamMatchingResponse> toTeamMatchingResponseList = Arrays.asList(firstToTeamMatchingResponse, secondToTeamMatchingResponse);

        final MyPrivateMatchingResponse firstMyPrivateMatchingResponse = new MyPrivateMatchingResponse(
                1L,
                "https://image.linkit.im/images/linkit_logo.png",
                "주서영",
                "주서영님의 내 이력서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.PROFILE,
                false
        );

        final MyPrivateMatchingResponse secondMyPrivateMatchingResponse = new MyPrivateMatchingResponse(
                2L,
                "https://image.linkit.im/images/linkit_logo.png",
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
                "https://image.linkit.im/images/linkit_logo.png",
                "링컬쳐",
                "링컬쳐님의 팀 소개서에 보낸 매칭 요청 메시지입니다.",
                LocalDate.of(2024, 7, 10),
                SenderType.PRIVATE,
                MatchingType.TEAM_PROFILE,
                true
        );

        final MyTeamMatchingResponse secondMyTeamMatchingResponse = new MyTeamMatchingResponse(
                2L,
                "https://image.linkit.im/images/linkit_logo.png",
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
                "권동민님이 나의 내 이력서에 보낸 매칭 요청 메시지입니다.",
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
                                fieldWithPath("requestMessage").type(JsonFieldType.STRING).description("매칭 요청 메시지"),
                                fieldWithPath("receivedTeamProfile").type(JsonFieldType.BOOLEAN).description("이력/소개서 수신 여부")
                        )
                ));
    }

    @DisplayName("내가 받은 매칭 요청 / sender_type = Team / receivedTeamProfile = false")
    @Test
    void getReceivedTeamToPrivateMatchingMessage() throws Exception {
        // given
        final ReceivedPrivateMatchingMessageResponse receivedPrivateMatchingMessageResponse = new ReceivedPrivateMatchingMessageResponse(
                1L,
                "권동민",
                "권동민님이 나의 내 이력서에 보낸 매칭 요청 메시지입니다.",
                false
        );

        given(matchingService.getReceivedTeamToPrivateMatchingMessage(1L)).willReturn(receivedPrivateMatchingMessageResponse);

        // when
        final ResultActions resultActions = performGetReceivedTeamToPrivateMatchingMessage(1);

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
                                fieldWithPath("requestMessage").type(JsonFieldType.STRING).description("매칭 요청 메시지"),
                                fieldWithPath("receivedTeamProfile").type(JsonFieldType.BOOLEAN).description("이력/소개서 수신 여부")
                        )
                ));
    }

    @DisplayName("내가 받은 매칭 요청 / sender_type = private / receivedTeamProfile = true")
    @Test
    void getReceivedPrivateToTeamMatchingResponse() throws Exception {
        // given
        final ReceivedTeamMatchingMessageResponse receivedTeamMatchingMessageResponse = new ReceivedTeamMatchingMessageResponse(
                1L,
                "권동민",
                "권동민님이 나의 팀 소개서에 보낸 매칭 요청 메시지입니다.",
                false
        );

        given(matchingService.getReceivedPrivateToTeamMatchingMessage(1L)).willReturn(receivedTeamMatchingMessageResponse);

        // when
        final ResultActions resultActions = performGetReceivedPrivateToTeamMatchingResponse(1);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamMatchingId")
                                        .description("팀 소개서 대상 매칭 PK")
                        ),
                        responseFields(
                                fieldWithPath("receivedMatchingId").type(JsonFieldType.NUMBER).description("내 이력서/팀 소개서에 매칭 PK ID"),
                                fieldWithPath("senderName").type(JsonFieldType.STRING).description("발신자 이름"),
                                fieldWithPath("requestMessage").type(JsonFieldType.STRING).description("매칭 요청 메시지"),
                                fieldWithPath("receivedTeamProfile").type(JsonFieldType.BOOLEAN).description("이력/소개서 수신 여부")
                        )
                ));
    }

    @DisplayName("내가 보낸 매칭 요청 / sender_type = private / requestTeamProfile = false")
    @Test
    void getRequestPrivateToPrivateMatchingMessage() throws Exception {
        // given
        final RequestPrivateMatchingMessageResponse requestPrivateMatchingMessageResponse = new RequestPrivateMatchingMessageResponse(
                1L,
                "권동민",
                "권동민님이 나의 내 이력서에 보낸 매칭 요청 메시지입니다.",
                SenderType.PRIVATE,
                false
        );

        given(matchingService.getRequestPrivateToPrivateMatchingMessage(1L)).willReturn(requestPrivateMatchingMessageResponse);

        // when
        final ResultActions resultActions = performGetRequestPrivateToPrivateMatchingMessage(1);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("privateMatchingId")
                                        .description("내 이력서 대상 매칭 PK")
                        ),
                        responseFields(
                                fieldWithPath("requestMatchingId").type(JsonFieldType.NUMBER).description("내 이력서/팀 소개서에 매칭 PK ID"),
                                fieldWithPath("receiverName").type(JsonFieldType.STRING).description("발신자 이름"),
                                fieldWithPath("requestMessage").type(JsonFieldType.STRING).description("매칭 요청 메시지"),
                                fieldWithPath("senderType").type(JsonFieldType.STRING).description("발신자 타입"),
                                fieldWithPath("requestTeamProfile").type(JsonFieldType.BOOLEAN).description("이력/소개서 수신 여부")
                        )
                ));
    }

    @DisplayName("내가 보낸 매칭 요청 / sender_type = Team / requestTeamProfile = false")
    @Test
    void getRequestTeamToPrivateMatchingMessage() throws Exception {
        // given
        final RequestPrivateMatchingMessageResponse requestPrivateMatchingMessageResponse = new RequestPrivateMatchingMessageResponse(
                1L,
                "권동민",
                "리에종님이 나의 내 이력서에 보낸 매칭 요청 메시지입니다.",
                SenderType.TEAM,
                false
        );

        given(matchingService.getRequestTeamToPrivateMatchingMessage(1L)).willReturn(requestPrivateMatchingMessageResponse);

        // when
        final ResultActions resultActions = performGetRequestTeamToPrivateMatchingMessage(1);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("privateMatchingId")
                                        .description("내 이력서 대상 매칭 PK")
                        ),
                        responseFields(
                                fieldWithPath("requestMatchingId").type(JsonFieldType.NUMBER).description("내 이력서/팀 소개서에 매칭 PK ID"),
                                fieldWithPath("receiverName").type(JsonFieldType.STRING).description("수신자 이름"),
                                fieldWithPath("requestMessage").type(JsonFieldType.STRING).description("매칭 요청 메시지"),
                                fieldWithPath("senderType").type(JsonFieldType.STRING).description("발신자 타입"),
                                fieldWithPath("requestTeamProfile").type(JsonFieldType.BOOLEAN).description("이력/소개서 수신 여부")
                        )
                ));
    }

    @DisplayName("내가 보낸 매칭 요청 / sender_type = private / requestTeamProfile = true")
    @Test
    void getRequestPrivateToTeamMatchingMessage() throws Exception {
        // given
        final RequestTeamMatchingMessageResponse requestPrivateMatchingMessageResponse = new RequestTeamMatchingMessageResponse(
                1L,
                "리에종",
                "권동민님이 나의 팀 소개서에 보낸 매칭 요청 메시지입니다.",
                SenderType.TEAM,
                false
        );

        given(matchingService.getRequestPrivateToTeamMatchingMessage(1L)).willReturn(requestPrivateMatchingMessageResponse);

        // when
        final ResultActions resultActions = performGetRequestPrivateToTeamMatchingMessage(1);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamMatchingId")
                                        .description("팀 소개서 대상 매칭 PK")
                        ),
                        responseFields(
                                fieldWithPath("requestMatchingId").type(JsonFieldType.NUMBER).description("내 이력서/팀 소개서에 매칭 PK ID"),
                                fieldWithPath("receiverName").type(JsonFieldType.STRING).description("발신자 이름"),
                                fieldWithPath("requestMessage").type(JsonFieldType.STRING).description("매칭 요청 메시지"),
                                fieldWithPath("senderType").type(JsonFieldType.STRING).description("발신자 타입"),
                                fieldWithPath("requestTeamProfile").type(JsonFieldType.BOOLEAN).description("이력/소개서 수신 여부")
                        )
                ));
    }

    @DisplayName("매칭 성사 / 연락하기 버튼 클릭 / 내 이력서인 경우")
    @Test
    void getPrivateSuccessContactResponse() throws Exception {
        // given
        final SuccessContactResponse successContactResponse = new SuccessContactResponse(
                "권동민",
                "kwondm7@naver.com"
        );

        given(matchingService.getPrivateSuccessContactResponse(1L, 1L)).willReturn(successContactResponse);
        // when
        final ResultActions resultActions = performGetPrivateSuccessContactResponse(1);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("privateMatchingId")
                                        .description("내 이력서 대상 매칭 PK")
                        ),
                        responseFields(
                                fieldWithPath("memberName").type(JsonFieldType.STRING).description("매칭 대상자 연락처"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                        )
                ));
    }

    @DisplayName("매칭 성사 / 연락하기 버튼 클릭 / 팀 소개서인 경우")
    @Test
    void getTeamSuccessContactResponse() throws Exception {
        // given
        final SuccessContactResponse successContactResponse = new SuccessContactResponse(
                "링킷",
                "kwondm7@linkit.im"
        );

        given(matchingService.getTeamSuccessContactResponse(1L, 1L)).willReturn(successContactResponse);
        // when
        final ResultActions resultActions = performGetTeamSuccessContactResponse(1);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamMatchingId")
                                        .description("팀 소개서 대상 매칭 PK")
                        ),
                        responseFields(
                                fieldWithPath("memberName").type(JsonFieldType.STRING).description("매칭 대상자 연락처"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                        )
                ));
    }

    @DisplayName("receivedTeamProfile(false) - 매칭 수락/거절하기 버튼 클릭")
    @Test
    void acceptReceivePrivateMatching() throws Exception {
        // given
        final AllowMatchingRequest allowMatchingRequest = new AllowMatchingRequest(
                true
        );

        // when
        final ResultActions resultActions = performPostAcceptReceivePrivateMatching(1, allowMatchingRequest);

        // then
//        verify(matchingService).acceptPrivateMatching(eq(1L), any(AllowMatchingRequest.class));

        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("privateMatchingId")
                                        .description("내 이력서 대상 매칭 PK")
                        ),
                        requestFields(
                                fieldWithPath("isAllowMatching").type(JsonFieldType.BOOLEAN).description("매칭 요청 수락 여부 (true) -> 수락하기")
                        )
                ));
    }

    @DisplayName("receivedTeamProfile(true) - 매칭 수락/거절하기 버튼 클릭")
    @Test
    void acceptReceiveTeamMatching() throws Exception {
        // given
        final AllowMatchingRequest allowMatchingRequest = new AllowMatchingRequest(
                true
        );

        doNothing().when(matchingService).acceptTeamMatching(1L, allowMatchingRequest);
        // when
        final ResultActions resultActions = performPostAcceptReceiveTeamMatching(1, allowMatchingRequest);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamMatchingId")
                                        .description("팀 소개서 대상 매칭 PK")
                        ),
                        requestFields(
                                fieldWithPath("isAllowMatching").type(JsonFieldType.BOOLEAN).description("매칭 요청 수락 여부 (true) -> 수락하기")
                        )
                ));
    }

    @DisplayName("matchingType -> PROFILE - 내가 보낸 매칭에서 내 이력서 대상 매칭 삭제하기")
    @Test
    void deleteRequestPrivateMatching() throws Exception {
        // given

        // when
        final ResultActions resultActions = performRequestDeletePrivateMatching(1);
        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("privateMatchingId")
                                                .description("내 이력서 ID")
                                )
                        )
                );
    }

    @DisplayName("matchingType -> TEAM_PROFILE - 내가 보낸 매칭에서 팀 소개서 대상 매칭 삭제하기")
    @Test
    void deleteRequestTeamMatching() throws Exception {
        // given

        // when
        final ResultActions resultActions = performRequestDeleteTeamMatching(1);
        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamMatchingId")
                                                .description("팀 소개서 ID")
                                )
                        )
                );
    }

    @DisplayName("matchingType -> PROFILE - 성사된 매칭에서 내 이력서 대상 매칭 삭제하기")
    @Test
    void deleteSuccessPrivateMatching() throws Exception {
        // given

        // when
        final ResultActions resultActions = performSuccessDeletePrivateMatching(1);
        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("privateMatchingId")
                                                .description("내 이력서 ID")
                                )
                        )
                );
    }

    @DisplayName("matchingType -> TEAM_PROFILE - 성사된 매칭에서 팀 소개서 대상 매칭 삭제하기")
    @Test
    void deleteSuccessTeamMatching() throws Exception {
        // given

        // when
        final ResultActions resultActions = performSuccessDeleteTeamMatching(1);
        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamMatchingId")
                                                .description("팀 소개서 ID")
                                )
                        )
                );
    }
}
