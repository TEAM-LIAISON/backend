package liaison.linkit.wish.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.search.dto.response.browseAfterLogin.BrowseMiniProfileResponse;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.wish.dto.response.WishTeamProfileResponse;
import liaison.linkit.wish.service.WishService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class WishControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WishService wishService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    // 내 이력서 찜하기 메서드
    private ResultActions performCreateWishToPrivateProfile(
            final int miniProfileId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/wish/private/profile/{miniProfileId}", miniProfileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    // 팀 소개서 찜하기 메서드
    private ResultActions performCreateWishToTeamProfile(
            final int teamMemberAnnouncementId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/wish/team/profile/{teamMemberAnnouncementId}", teamMemberAnnouncementId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    // 내 이력서 찜하기 취소 메서드
    private ResultActions performDeleteWishToPrivateProfile(
            final int miniProfileId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/wish/private/profile/{miniProfileId}", miniProfileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    // 팀 소개서 찜하기 취소 메서드
    private ResultActions performDeleteWishToTeamProfile(
            final int teamMemberAnnouncementId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/wish/team/profile/{teamMemberAnnouncementId}", teamMemberAnnouncementId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    // 내 이력서 찜한 리스트 조회
    private ResultActions performGetPrivateProfileWishList() throws Exception {
        return mockMvc.perform(
                get("/wish/private/profile/list")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)

        );
    }

    // 팀 소개서 찜한 리스트 조회
    private ResultActions performGetTeamProfileWishList() throws Exception {
        return mockMvc.perform(
                get("/wish/team/profile/list")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    @DisplayName("상대방의 내 이력서를 찜할 수 있다.")
    @Test
    void createWishToPrivateProfile() throws Exception {
        // when
        final ResultActions resultActions = performCreateWishToPrivateProfile(1);

        // then
        verify(wishService).createWishToPrivateProfile(eq(1L), eq(1L));

        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("miniProfileId")
                                        .description("미니 프로필 ID")
                        )
                ));
    }

    @DisplayName("상대방의 팀 소개서를 찜할 수 있다.")
    @Test
    void createWishToTeamProfile() throws Exception {
        // when
        final ResultActions resultActions = performCreateWishToTeamProfile(1);

        // then
        verify(wishService).createWishToTeamProfile(eq(1L), eq(1L));

        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamMemberAnnouncementId")
                                        .description("팀원 공고 ID")
                        )
                ));
    }

    @DisplayName("상대방의 내 이력서를 찜 취소할 수 있다.")
    @Test
    void cancelWishToPrivateProfile() throws Exception {
        // when
        final ResultActions resultActions = performDeleteWishToPrivateProfile(1);

        // then
        verify(wishService).cancelWishToPrivateProfile(eq(1L), eq(1L));

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("miniProfileId")
                                        .description("미니 프로필 ID")
                        )
                ));
    }

    @DisplayName("상대방의 팀 소개서를 찜 취소할 수 있다.")
    @Test
    void cancelWishToTeamProfile() throws Exception {
        // when
        final ResultActions resultActions = performDeleteWishToTeamProfile(1);

        // then
        verify(wishService).cancelWishToTeamProfile(eq(1L), eq(1L));

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamMemberAnnouncementId")
                                        .description("팀원 공고 ID")
                        )
                ));
    }

    @DisplayName("내가 찜한 내 이력서를 조회할 수 있다.")
    @Test
    void getPrivateProfileWishList() throws Exception {
        // given
        final BrowseMiniProfileResponse firstBrowseMiniProfileResponse = new BrowseMiniProfileResponse(
                1L,
                "시니어 소프트웨어 개발자",
                "https://image.linkit.im/images/linkit_logo.png",
                true,
                Arrays.asList("2024 레드닷 수상", "스타트업 경력", "서울대 디자인", "대기업 경력 3년"),
                "권동민",
                true
        );

        final BrowseMiniProfileResponse secondBrowseMiniProfileResponse = new BrowseMiniProfileResponse(
                1L,
                "시니어 소프트웨어 개발자",
                "https://image.linkit.im/images/linkit_logo.png",
                true,
                Arrays.asList("2024 레드닷 수상", "스타트업 경력", "서울대 디자인", "대기업 경력 3년"),
                "권동민",
                true
        );

        final List<BrowseMiniProfileResponse> browseMiniProfileResponseList = Arrays.asList(firstBrowseMiniProfileResponse, secondBrowseMiniProfileResponse);

        given(wishService.getPrivateProfileWishList(1L)).willReturn(browseMiniProfileResponseList);

        // when
        final ResultActions resultActions = performGetPrivateProfileWishList();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("미니 프로필 ID"),
                                fieldWithPath("[].profileTitle").type(JsonFieldType.STRING).description("프로필의 제목"),
                                fieldWithPath("[].miniProfileImg").type(JsonFieldType.STRING).description("미니 프로필 이미지 URL"),
                                fieldWithPath("[].myKeywordNames").type(JsonFieldType.ARRAY).description("나를 소개하는 키워드 목록"),
                                fieldWithPath("[].isActivate").type(JsonFieldType.BOOLEAN).description("미니 프로필 활성화 여부"),
                                fieldWithPath("[].memberName").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("[].isPrivateSaved").type(JsonFieldType.BOOLEAN).description("찜 여부")
                        )
                ));
    }

    @DisplayName("내가 찜한 팀 소개서를 조회할 수 있다.")
    @Test
    void getTeamProfileWishList() throws Exception {
        // given
        final TeamMiniProfileResponse firstTeamMiniProfileResponse = new TeamMiniProfileResponse(
                1L,
                "SaaS",
                "1-5인",
                "리에종",
                "팀 소개서 제목입니다.",
                true,
                "https://image.linkit.im/images/linkit_logo.png",
                Arrays.asList("재택 가능", "Pre-A", "사수 있음", "스톡 제공")
        );

        final TeamMiniProfileResponse secondTeamMiniProfileResponse = new TeamMiniProfileResponse(
                2L,
                "SaaS",
                "5-10인",
                "팀명팀명",
                "팀 소개서 제목입니다. 22",
                true,
                "https://image.linkit.im/images/linkit_logo.png",
                Arrays.asList("재택 가능", "Pre-A", "사수 있음", "스톡 제공")
        );

        final TeamMemberAnnouncementResponse firstTeamMemberAnnouncementResponse = new TeamMemberAnnouncementResponse(
                1L,
                "https://image.linkit.im/images/linkit_logo.png",
                "리에종",
                "주요 업무입니다.",
                "지원 절차입니다.",
                true
        );

        final TeamMemberAnnouncementResponse secondTeamMemberAnnouncementResponse = new TeamMemberAnnouncementResponse(
                2L,
                "https://image.linkit.im/images/linkit_logo.png",
                "팀명팀명",
                "주요 업무입니다. (두번째 팀원 공고)",
                "지원 절차입니다. (두번째 팀원 공고)",
                true
        );

        final WishTeamProfileResponse firstWishTeamProfileResponse = new WishTeamProfileResponse(firstTeamMiniProfileResponse, firstTeamMemberAnnouncementResponse);
        final WishTeamProfileResponse secondWishTeamProfileResponse = new WishTeamProfileResponse(secondTeamMiniProfileResponse, secondTeamMemberAnnouncementResponse);

        final List<WishTeamProfileResponse> wishTeamProfileResponseList = Arrays.asList(firstWishTeamProfileResponse, secondWishTeamProfileResponse);

        given(wishService.getTeamProfileWishList(1L)).willReturn(wishTeamProfileResponseList);
        // when
        final ResultActions resultActions = performGetTeamProfileWishList();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].teamMiniProfileResponse.id").type(JsonFieldType.NUMBER).description("팀 미니 프로필 ID"),
                                fieldWithPath("[].teamMiniProfileResponse.sectorName").type(JsonFieldType.STRING).description("산업 분야"),
                                fieldWithPath("[].teamMiniProfileResponse.sizeType").type(JsonFieldType.STRING).description("팀 규모"),
                                fieldWithPath("[].teamMiniProfileResponse.teamName").type(JsonFieldType.STRING).description("팀 이름"),
                                fieldWithPath("[].teamMiniProfileResponse.teamProfileTitle").type(JsonFieldType.STRING).description("팀 프로필 제목"),
                                fieldWithPath("[].teamMiniProfileResponse.isTeamActivate").type(JsonFieldType.BOOLEAN).description("팀 활성화 상태"),
                                fieldWithPath("[].teamMiniProfileResponse.teamLogoImageUrl").type(JsonFieldType.STRING).description("팀 로고 이미지 URL"),
                                fieldWithPath("[].teamMiniProfileResponse.teamKeywordNames").type(JsonFieldType.ARRAY).description("팀 키워드 목록"),

                                fieldWithPath("[].teamMemberAnnouncementResponse.id").type(JsonFieldType.NUMBER).description("팀원 공고 ID"),
                                fieldWithPath("[].teamMemberAnnouncementResponse.teamLogoImageUrl").type(JsonFieldType.STRING).description("팀 로고 이미지 경로"),
                                fieldWithPath("[].teamMemberAnnouncementResponse.teamName").type(JsonFieldType.STRING).description("공고에 대한 팀 이름"),
                                fieldWithPath("[].teamMemberAnnouncementResponse.mainBusiness").type(JsonFieldType.STRING).description("주요 업무 내용"),
                                fieldWithPath("[].teamMemberAnnouncementResponse.applicationProcess").type(JsonFieldType.STRING).description("지원 절차 설명"),
                                fieldWithPath("[].teamMemberAnnouncementResponse.isTeamSaved").type(JsonFieldType.BOOLEAN).description("팀 찜 여부")
                        )
                ));

    }
}
