package liaison.linkit.team.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.dto.request.TeamIntroductionCreateRequest;
import liaison.linkit.team.dto.response.*;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.attach.TeamAttachResponse;
import liaison.linkit.team.dto.response.attach.TeamAttachUrlResponse;
import liaison.linkit.team.dto.response.completion.TeamCompletionResponse;
import liaison.linkit.team.dto.response.history.HistoryResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.service.*;
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

import java.util.Arrays;
import java.util.List;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TeamProfileController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class TeamProfileControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamProfileService teamProfileService;
    @MockBean
    private TeamMiniProfileService teamMiniProfileService;
    @MockBean
    private TeamCompletionService teamCompletionService;
    @MockBean
    private TeamMemberAnnouncementService teamMemberAnnouncementService;
    @MockBean
    private ActivityService activityService;
    @MockBean
    private TeamMemberIntroductionService teamMemberIntroductionService;
    @MockBean
    private HistoryService historyService;
    @MockBean
    private TeamAttachService teamAttachService;


    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(teamProfileService).validateTeamProfileByMember(1L);
        doNothing().when(teamMiniProfileService).validateTeamMiniProfileByMember(1L);
    }

    private ResultActions performGetTeamProfileRequest() throws Exception {
        return mockMvc.perform(
                get("/team/profile")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performPostTeamIntroductionRequest(final TeamIntroductionCreateRequest teamIntroductionCreateRequest) throws Exception {
        return mockMvc.perform(
                post("/team/introduction")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamIntroductionCreateRequest))
        );
    }

    @DisplayName("팀 소개서 전체 조회를 할 수 있다.")
    @Test
    void getTeamProfile() throws Exception {
        // given

        final TeamProfileIsValueResponse teamProfileIsValueResponse = new TeamProfileIsValueResponse(
                true,
                true,
                true,
                true,
                true,
                true,
                true
        );
        given(teamProfileService.getTeamProfileIsValue(1L)).willReturn(teamProfileIsValueResponse);

        // 팀 소개서 필수 항목 존재 여부
        final boolean isTeamProfileEssential = (teamProfileIsValueResponse.isTeamMiniProfile() && teamProfileIsValueResponse.isActivity());


        // 4.1. 미니 프로필
        final TeamMiniProfileResponse teamMiniProfileResponse = new TeamMiniProfileResponse(
                1L,
                "SaaS",
                "1-5인",
                "리에종",
                "팀 소개서 제목입니다.",
                false,
                "https://image.linkit.im/images/linkit_logo.png",
                Arrays.asList("재택 가능", "Pre-A", "사수 있음", "스톡 제공")
        );
        given(teamMiniProfileService.getPersonalTeamMiniProfile(1L)).willReturn(teamMiniProfileResponse);

        // 4.2. 매칭 추천

        // 4.3. 프로필 완성도
        final TeamCompletionResponse teamCompletionResponse = new TeamCompletionResponse(
                "100.0",
                true,
                true,
                true,
                true,
                true,
                true
        );
        given(teamCompletionService.getTeamCompletion(1L)).willReturn(teamCompletionResponse);

        // 4.5. 팀원 공고
        final TeamMemberAnnouncementResponse firstTeamMemberAnnouncementResponse = new TeamMemberAnnouncementResponse(
                1L,
                "https://image.linkit.im/images/linkit_logo.png",
                "리에종",
                "주요 업무입니다. (첫번째 팀원 공고)",
                "지원 절차입니다. (첫번째 팀원 공고)",
                false
        );

        final TeamMemberAnnouncementResponse secondTeamMemberAnnouncementResponse = new TeamMemberAnnouncementResponse(
                2L,
                "https://image.linkit.im/images/linkit_logo.png",
                "리에종",
                "주요 업무입니다. (두번째 팀원 공고)",
                "지원 절차입니다. (두번째 팀원 공고)",
                false
        );

        final List<TeamMemberAnnouncementResponse> teamMemberAnnouncementResponseList = Arrays.asList(firstTeamMemberAnnouncementResponse, secondTeamMemberAnnouncementResponse);

        given(teamMemberAnnouncementService.getTeamMemberAnnouncements(1L)).willReturn(teamMemberAnnouncementResponseList);

        // 4.6. 활동 방식 + 활동 지역/위치
        final List<String> activityTagName = Arrays.asList("사무실 있음", "비대면 활동");
        final ActivityResponse activityResponse = new ActivityResponse(
                activityTagName,
                "서울특별시",
                "강남구"
        );
        given(activityService.getActivity(1L)).willReturn(activityResponse);

        // 4.7. 팀 소개
        final TeamProfileIntroductionResponse teamProfileIntroductionResponse = new TeamProfileIntroductionResponse(
               "팀 소개입니다."
        );
        given(teamProfileService.getTeamIntroduction(1L)).willReturn(teamProfileIntroductionResponse);

        // 4.8. 팀원 소개
        final TeamMemberIntroductionResponse firstTeamMemberIntroductionResponse = new TeamMemberIntroductionResponse(
                1L,
                "김동혁",
                "프론트엔드 개발자",
                "프론트엔드 개발자입니다."
        );

        final TeamMemberIntroductionResponse secondTeamMemberIntroductionResponse = new TeamMemberIntroductionResponse(
                2L,
                "권동민",
                "백엔드 개발자",
                "백엔드 개발자입니다."
        );

        final List<TeamMemberIntroductionResponse> teamMemberIntroductionResponseList = Arrays.asList(firstTeamMemberIntroductionResponse, secondTeamMemberIntroductionResponse);
        given(teamMemberIntroductionService.getAllTeamMemberIntroduction(1L)).willReturn(teamMemberIntroductionResponseList);

        // 4.9. 연혁
        final HistoryResponse firstHistoryResponse = new HistoryResponse(
                1L,
                "연혁 한 줄 소개입니다.",
                2024,
                2025,
                "연혁 설명입니다.",
                false
        );

        final HistoryResponse secondHistoryResponse = new HistoryResponse(
                2L,
                "연혁 한 줄 소개입니다. 2",
                2023,
                2024,
                "연혁 설명입니다. 2",
                true
        );
        final List<HistoryResponse> historyResponseList = Arrays.asList(firstHistoryResponse, secondHistoryResponse);
        given(historyService.getAllHistories(1L)).willReturn(historyResponseList);

        // 4.10. 첨부
        final TeamAttachUrlResponse firstTeamAttachUrlResponse = new TeamAttachUrlResponse(
                1L,
                "깃허브",
                "https://github.com/TEAM-LIAISON"
        );

        final TeamAttachUrlResponse secondTeamAttachUrlResponse = new TeamAttachUrlResponse(
                2L,
                "노션",
                "https://www.notion.so/ko-kr"
        );

//        final TeamAttachFileResponse firstAttachFileResponse = new TeamAttachFileResponse(
//                1L,
//                "A4+-=1.pdf",
//                "https://linkit-dev-env-bucket.s3.ap-northeast-1.amazonaws.com/files/A4+-+1.pdf"
//        );

        final List<TeamAttachUrlResponse> teamAttachUrlResponseList = Arrays.asList(firstTeamAttachUrlResponse, secondTeamAttachUrlResponse);
//        final List<TeamAttachFileResponse> teamAttachFileResponseList = Arrays.asList(firstAttachFileResponse);
        final TeamAttachResponse teamAttachResponse = new TeamAttachResponse(
                teamAttachUrlResponseList
//                teamAttachFileResponseList
        );
        given(teamAttachService.getTeamAttachList(1L)).willReturn(teamAttachResponse);

        final TeamProfileResponse teamProfileResponse = new TeamProfileResponse(
                isTeamProfileEssential,
                teamMiniProfileResponse,
                teamCompletionResponse,
                teamMemberAnnouncementResponseList,
                activityResponse,
                teamProfileIntroductionResponse,
                teamMemberIntroductionResponseList,
                historyResponseList,
                teamAttachResponse
        );

        given(teamProfileService.getTeamProfileResponse(
                isTeamProfileEssential,
                teamMiniProfileResponse,
                teamCompletionResponse,
                teamMemberAnnouncementResponseList,
                activityResponse,
                teamProfileIntroductionResponse,
                teamMemberIntroductionResponseList,
                historyResponseList,
                teamAttachResponse
        )).willReturn(teamProfileResponse);

        // when
        final ResultActions resultActions = performGetTeamProfileRequest();

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
                                        fieldWithPath("teamProfileEssential").type(JsonFieldType.BOOLEAN).description("팀 소개서 기본 항목 존재 여부"),

                                        // 4.1.
                                        subsectionWithPath("teamMiniProfileResponse").type(JsonFieldType.OBJECT).description("팀 미니 프로필 응답 객체"),

                                        fieldWithPath("teamMiniProfileResponse.sectorName").type(JsonFieldType.STRING).description("팀 미니 프로필 분야"),
                                        fieldWithPath("teamMiniProfileResponse.sizeType").type(JsonFieldType.STRING).description("팀 미니 프로필 규모"),
                                        fieldWithPath("teamMiniProfileResponse.teamName").type(JsonFieldType.STRING).description("팀 이름"),
                                        fieldWithPath("teamMiniProfileResponse.teamProfileTitle").type(JsonFieldType.STRING).description("팀 미니 프로필 제목"),
                                        fieldWithPath("teamMiniProfileResponse.isTeamActivate").type(JsonFieldType.BOOLEAN).description("팀 소개서 활성화 여부"),
                                        fieldWithPath("teamMiniProfileResponse.teamLogoImageUrl").type(JsonFieldType.STRING).description("이미지 파일 소스 경로"),
                                        fieldWithPath("teamMiniProfileResponse.teamKeywordNames").type(JsonFieldType.ARRAY).description("팀 소개 항목").attributes(field("constraint", "문자열 배열")),

                                        // 4.3.
                                        subsectionWithPath("teamCompletionResponse").type(JsonFieldType.OBJECT).description("팀 소개서 완성도 응답 객체"),
                                        fieldWithPath("teamCompletionResponse.teamCompletion").type(JsonFieldType.STRING).description("팀 소개서 완성도 % 값"),
                                        fieldWithPath("teamCompletionResponse.teamMemberAnnouncement").type(JsonFieldType.BOOLEAN).description("팀원 공고"),
                                        fieldWithPath("teamCompletionResponse.activity").type(JsonFieldType.BOOLEAN).description("활동 방식 및 활동 지역 및 위치"),
                                        fieldWithPath("teamCompletionResponse.teamIntroduction").type(JsonFieldType.BOOLEAN).description("팀 소개"),
                                        fieldWithPath("teamCompletionResponse.teamMemberIntroduction").type(JsonFieldType.BOOLEAN).description("팀원 소개"),
                                        fieldWithPath("teamCompletionResponse.history").type(JsonFieldType.BOOLEAN).description("연혁"),
                                        fieldWithPath("teamCompletionResponse.teamAttach").type(JsonFieldType.BOOLEAN).description("첨부"),

                                        // 4.5.
                                        subsectionWithPath("teamMemberAnnouncementResponses").type(JsonFieldType.ARRAY).description("팀원 공고 응답 객체"),
                                        fieldWithPath("teamMemberAnnouncementResponses[].id").type(JsonFieldType.NUMBER).description("팀원 공고 응답 객체 ID"),
                                        fieldWithPath("teamMemberAnnouncementResponses[].teamName").type(JsonFieldType.STRING).description("팀 이름"),
                                        fieldWithPath("teamMemberAnnouncementResponses[].mainBusiness").type(JsonFieldType.STRING).description("팀원 공고 주요 업무"),
                                        fieldWithPath("teamMemberAnnouncementResponses[].applicationProcess").type(JsonFieldType.STRING).description("지원 절차"),

                                        // 4.6.
                                        subsectionWithPath("activityResponse").type(JsonFieldType.OBJECT).description("활동 방식 및 활동 지역 및 위치 응답 객체"),
                                        fieldWithPath("activityResponse.activityTagName").type(JsonFieldType.ARRAY).description("활동 방식"),
                                        fieldWithPath("activityResponse.cityName").type(JsonFieldType.STRING).description("시/도 이름"),
                                        fieldWithPath("activityResponse.divisionName").type(JsonFieldType.STRING).description("시/군/구 이름"),

                                        // 4.7.
                                        subsectionWithPath("teamProfileIntroductionResponse").type(JsonFieldType.OBJECT).description("팀 소개 응답 객체"),
                                        fieldWithPath("teamProfileIntroductionResponse.teamIntroduction").type(JsonFieldType.STRING).description("팀 소개 텍스트"),

                                        // 4.8.
                                        subsectionWithPath("teamMemberIntroductionResponses").type(JsonFieldType.ARRAY).description("팀원 소개 응답 객체"),
                                        fieldWithPath("teamMemberIntroductionResponses[].id").type(JsonFieldType.NUMBER).description("팀원 소개 응답 객체 ID"),
                                        fieldWithPath("teamMemberIntroductionResponses[].teamMemberName").type(JsonFieldType.STRING).description("팀원 이름"),
                                        fieldWithPath("teamMemberIntroductionResponses[].teamMemberRole").type(JsonFieldType.STRING).description("팀원 직무/역할"),
                                        fieldWithPath("teamMemberIntroductionResponses[].teamMemberIntroductionText").type(JsonFieldType.STRING).description("팀원 소개 텍스트"),

                                        // 4.9
                                        subsectionWithPath("historyResponses").type(JsonFieldType.ARRAY).description("연혁 응답 객체"),
                                        fieldWithPath("historyResponses[].id").type(JsonFieldType.NUMBER).description("연혁 응답 객체 ID"),
                                        fieldWithPath("historyResponses[].historyOneLineIntroduction").type(JsonFieldType.STRING).description("연혁 한 줄 소개"),
                                        fieldWithPath("historyResponses[].startYear").type(JsonFieldType.NUMBER).description("시작 연도"),
                                        fieldWithPath("historyResponses[].endYear").type(JsonFieldType.NUMBER).description("종료 연도"),
                                        fieldWithPath("historyResponses[].historyIntroduction").type(JsonFieldType.STRING).description("연혁 소개"),
                                        fieldWithPath("historyResponses[].inProgress").type(JsonFieldType.BOOLEAN).description("진행 여부"),

                                        // 4.10.
                                        subsectionWithPath("teamAttachResponse").type(JsonFieldType.OBJECT).description("팀 첨부 응답 객체"),
                                        fieldWithPath("teamAttachResponse.teamAttachUrlResponseList[].id").type(JsonFieldType.NUMBER).description("첨부 URL 객체 ID"),
                                        fieldWithPath("teamAttachResponse.teamAttachUrlResponseList[].teamAttachUrlName").type(JsonFieldType.STRING).description("팀 첨부 URL 이름"),
                                        fieldWithPath("teamAttachResponse.teamAttachUrlResponseList[].teamAttachUrlPath").type(JsonFieldType.STRING).description("팀 첨부 URL 경로")
                                )
                        )
                );

    }

    @DisplayName("팀 소개서 팀 소개 항목을 생성할 수 있다.")
    @Test
    void createTeamProfileIntroduction() throws Exception {
        // given
        final TeamIntroductionCreateRequest teamIntroductionCreateRequest = new TeamIntroductionCreateRequest(
                "팀 소개 항목을 입력합니다."
        );

        // when
        final ResultActions resultActions = performPostTeamIntroductionRequest(teamIntroductionCreateRequest);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("teamIntroduction")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 소개")
                                                .attributes(field("constraint", "문자열, 공란이면 삭제"))
                                )
                        )
                );
    }
}
