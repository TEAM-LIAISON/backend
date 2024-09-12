package liaison.linkit.team.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.dto.request.announcement.TeamMemberAnnouncementRequest;
import liaison.linkit.team.service.TeamMemberAnnouncementService;
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

import java.util.Arrays;
import java.util.List;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamMemberAnnouncementController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamMemberAnnouncementControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamMemberAnnouncementService teamMemberAnnouncementService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(teamMemberAnnouncementService).validateTeamMemberAnnouncement(anyLong());
    }

    private void makeTeamMemberAnnouncement() throws Exception {
        final TeamMemberAnnouncementRequest firstTeamMemberAnnouncementRequest = new TeamMemberAnnouncementRequest(
                "주요 업무입니다.",
                "지원 절차입니다."
        );

        final TeamMemberAnnouncementRequest secondTeamMemberAnnouncementRequest = new TeamMemberAnnouncementRequest(
                "주요 업무입니다.",
                "지원 절차입니다."
        );

        final List<TeamMemberAnnouncementRequest> teamMemberAnnouncementRequestList = Arrays.asList(firstTeamMemberAnnouncementRequest, secondTeamMemberAnnouncementRequest);

        doNothing().when(teamMemberAnnouncementService).saveAnnouncements(1L, teamMemberAnnouncementRequestList);
        performPostTeamMemberAnnouncementRequest(teamMemberAnnouncementRequestList);
    }

    private ResultActions performUpdateTeamMemberAnnouncement(
            final int teamMemberAnnouncementId,
            final TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/team/member/announcement/{teamMemberAnnouncementId}", teamMemberAnnouncementId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMemberAnnouncementRequest))
        );
    }

    private ResultActions performPostTeamMemberAnnouncement(
            final TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) throws Exception {
        return mockMvc.perform(
                post("/team/member/announcement")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMemberAnnouncementRequest))
        );
    }

    // post request 구현부
    private ResultActions performPostTeamMemberAnnouncementRequest(
            final List<TeamMemberAnnouncementRequest> teamMemberAnnouncementRequestList
    ) throws Exception {
        return mockMvc.perform(
                post("/team/members/announcements")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMemberAnnouncementRequestList))
        );
    }

    private ResultActions performDeleteTeamAttachUrlRequest(
            final int teamMemberAnnouncementId
    )throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/team/members/announcements/{teamMemberAnnouncementId}", teamMemberAnnouncementId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    @DisplayName("단일 팀원 공고를 수정할 수 있다.")
    @Test
    void updateTeamMemberAnnouncement() throws Exception {
        // given
        final TeamMemberAnnouncementRequest teamMemberAnnouncementRequest = new TeamMemberAnnouncementRequest(
                "주요 업무입니다.",
                "지원 절차입니다."
        );

        // when
        final ResultActions resultActions = performUpdateTeamMemberAnnouncement(1, teamMemberAnnouncementRequest);

        // then
        resultActions.andExpect(status().isOk())
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
                        pathParameters(
                                parameterWithName("teamMemberAnnouncementId")
                                        .description("팀원 공고 ID")
                        ),
                        requestFields(
                                fieldWithPath("mainBusiness")
                                        .type(JsonFieldType.STRING)
                                        .description("주요 업무")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("applicationProcess")
                                        .type(JsonFieldType.STRING)
                                        .description("지원 절차")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }

    @DisplayName("단일 팀원 공고를 생성할 수 있다.")
    @Test
    void createTeamMemberAnnouncement() throws Exception {
        // given
        final TeamMemberAnnouncementRequest teamMemberAnnouncementRequest = new TeamMemberAnnouncementRequest(
                "주요 업무입니다.",
                "지원 절차입니다."
        );
        // when
        final ResultActions resultActions = performPostTeamMemberAnnouncement(teamMemberAnnouncementRequest);

        // then
        resultActions.andExpect(status().isCreated())
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
                        requestFields(
                                fieldWithPath("mainBusiness")
                                        .type(JsonFieldType.STRING)
                                        .description("주요 업무")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("applicationProcess")
                                        .type(JsonFieldType.STRING)
                                        .description("지원 절차")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }



    @DisplayName("팀원 공고 리스트를 생성/수정할 수 있다.")
    @Test
    void createTeamMemberAnnouncementList() throws Exception {
        // given
        final TeamMemberAnnouncementRequest firstTeamMemberAnnouncementRequest = new TeamMemberAnnouncementRequest(
                "주요 업무입니다.",
                "지원 절차입니다."
        );

        final TeamMemberAnnouncementRequest secondTeamMemberAnnouncementRequest = new TeamMemberAnnouncementRequest(
                "주요 업무입니다.",
                "지원 절차입니다."
        );

        final List<TeamMemberAnnouncementRequest> teamMemberAnnouncementRequestList = Arrays.asList(firstTeamMemberAnnouncementRequest, secondTeamMemberAnnouncementRequest);
        // when
        final ResultActions resultActions = performPostTeamMemberAnnouncementRequest(teamMemberAnnouncementRequestList);

        // then
        resultActions.andExpect(status().isCreated())
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
                                        fieldWithPath("[].mainBusiness")
                                                .type(JsonFieldType.STRING)
                                                .description("주요 업무")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("[].applicationProcess")
                                                .type(JsonFieldType.STRING)
                                                .description("지원 절차")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

    @DisplayName("팀원 공고 1개를 삭제할 수 있다.")
    @Test
    void deleteTeamMemberAnnouncement() throws Exception {
        // given
        makeTeamMemberAnnouncement();
        doNothing().when(teamMemberAnnouncementService).validateTeamMemberAnnouncement(anyLong());

        // when
        final ResultActions resultActions = performDeleteTeamAttachUrlRequest(1);

        // then
        verify(teamMemberAnnouncementService).deleteTeamMemberAnnouncement(1L, 1L);

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamMemberAnnouncementId")
                                        .description("팀원 공고 ID")
                        )
                ));
    }
}
