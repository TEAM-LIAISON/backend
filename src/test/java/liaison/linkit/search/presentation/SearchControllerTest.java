package liaison.linkit.search.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.search.dto.response.SearchTeamProfileResponse;
import liaison.linkit.search.dto.response.browseAfterLogin.BrowseMiniProfileResponse;
import liaison.linkit.search.dto.response.browseAfterLogin.SearchBrowseTeamProfileResponse;
import liaison.linkit.search.service.SearchService;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class SearchControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    // 로그인 이전
    // 팀원 공고 및 팀 미니 프로필
    private ResultActions performGetTeamAnnouncementAndTeamMiniProfile() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/search/team/profile")
                .contentType(APPLICATION_JSON);

        String cityName = null; // null로 예시 설정
        if (cityName != null) {
            requestBuilder.queryParam("cityName", cityName);
        }

        String divisionName = null; // null로 예시 설정
        if (divisionName != null) {
            requestBuilder.queryParam("divisionName", divisionName);
        }

        List<String> activityTagNames = null; // null로 예시 설정
        if (activityTagNames != null) {
            activityTagNames.forEach(tag -> requestBuilder.queryParam("activityTagName", tag));
        }

        return mockMvc.perform(requestBuilder);
    }

    // 로그인 이후
    private ResultActions performGetTeamAnnouncementAndTeamMiniProfileAfterLogin() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/search/team/profile/login")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON);

        String cityName = null; // null로 예시 설정
        if (cityName != null) {
            requestBuilder.queryParam("cityName", cityName);
        }

        String divisionName = null; // null로 예시 설정
        if (divisionName != null) {
            requestBuilder.queryParam("divisionName", divisionName);
        }

        List<String> activityTagNames = null; // null로 예시 설정
        if (activityTagNames != null) {
            activityTagNames.forEach(tag -> requestBuilder.queryParam("activityTagName", tag));
        }

        return mockMvc.perform(requestBuilder);
    }

    @Test
    @DisplayName("팀 찾기를 진행할 수 있다.")
    void getTeamAnnouncementAndTeamMiniProfile() throws Exception {
        // given
        final TeamMiniProfileResponse teamMiniProfileResponse = new TeamMiniProfileResponse(
                1L,
                "SaaS",
                "1-5인",
                "리에종",
                "팀 소개서 제목입니다.",
                true,
                "https://image.linkit.im/images/linkit_logo.png",
                Arrays.asList("재택 가능", "Pre-A", "사수 있음", "스톡 제공")
        );

        final TeamMemberAnnouncementResponse teamMemberAnnouncementResponse = new TeamMemberAnnouncementResponse(
                1L,
                "https://image.linkit.im/images/linkit_logo.png",
                "리에종",
                "주요 업무입니다.",
                "지원 절차입니다.",
                false
        );

        final SearchTeamProfileResponse searchTeamProfileResponse = new SearchTeamProfileResponse(
                teamMiniProfileResponse,
                teamMemberAnnouncementResponse
        );

        Page<SearchTeamProfileResponse> page = new PageImpl<>(Collections.singletonList(searchTeamProfileResponse));
        when(searchService.findTeamMemberAnnouncementsWithTeamMiniProfile(
                any(),
                eq(null),
                eq(null),
                eq(null)
        )).thenReturn(page);

        // when
        final ResultActions resultActions = performGetTeamAnnouncementAndTeamMiniProfile();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("cityName").description("지역 (시/도) 필터").optional(),
                                        parameterWithName("divisionName").description("지역 (시/군/구) 필터").optional(),
                                        parameterWithName("activityTagName").description("활동 방식 필터").optional()
                                ),

                                responseFields(
                                        fieldWithPath("content[].teamMiniProfileResponse.id").description("팀 미니 프로필 ID"),
                                        fieldWithPath("content[].teamMiniProfileResponse.sectorName").description("부문 이름"),
                                        fieldWithPath("content[].teamMiniProfileResponse.sizeType").description("팀 크기 유형"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamName").description("팀 이름"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamProfileTitle").description("팀 소개서 제목"),
                                        fieldWithPath("content[].teamMiniProfileResponse.isTeamActivate").description("팀 소개서 활성화 여부"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamLogoImageUrl").description("팀 로고 이미지 URL"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamKeywordNames").description("팀 키워드").optional(),

                                        fieldWithPath("content[].teamMemberAnnouncementResponse.id").description("팀원 공고 ID"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.teamLogoImageUrl").description("팀 로고 이미지 경로"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.teamName").description("팀 이름"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.mainBusiness").description("주요 업무"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.applicationProcess").description("지원 절차"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.isTeamSaved").description("팀원 공고 찜하기 여부"),


                                        // 페이지와 관련된 필드 추가
                                        subsectionWithPath("pageable").ignored(),
                                        subsectionWithPath("sort").ignored(),
                                        fieldWithPath("last").description("마지막 페이지 여부"),
                                        fieldWithPath("totalPages").description("전체 페이지 수"),
                                        fieldWithPath("totalElements").description("전체 요소 수"),
                                        fieldWithPath("first").description("첫 페이지 여부"),
                                        fieldWithPath("size").description("페이지당 요소 수"),
                                        fieldWithPath("number").description("페이지 번호"),
                                        fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                                        fieldWithPath("empty").description("페이지가 비어 있는지 여부")
                                )
                        )
                );
    }

    @Test
    @DisplayName("로그인 이후 팀 찾기를 진행할 수 있다.")
    void getTeamAnnouncementAndTeamMiniProfileAfterLogin() throws Exception {
        // given
        final TeamMiniProfileResponse teamMiniProfileResponse = new TeamMiniProfileResponse(
                1L,
                "SaaS",
                "1-5인",
                "리에종",
                "팀 소개서 제목입니다.",
                true,
                "https://image.linkit.im/images/linkit_logo.png",
                Arrays.asList("재택 가능", "Pre-A", "사수 있음", "스톡 제공")
        );

        final TeamMemberAnnouncementResponse teamMemberAnnouncementResponse = new TeamMemberAnnouncementResponse(
                1L,
                "https://image.linkit.im/images/linkit_logo.png",
                "리에종",
                "주요 업무입니다.",
                "지원 절차입니다.",
                true
        );

        final SearchBrowseTeamProfileResponse searchTeamProfileResponse = new SearchBrowseTeamProfileResponse(
                teamMiniProfileResponse,
                teamMemberAnnouncementResponse
        );

        Page<SearchBrowseTeamProfileResponse> page = new PageImpl<>(Collections.singletonList(searchTeamProfileResponse));
        when(searchService.findTeamMemberAnnouncementsWithTeamMiniProfileAfterLogin(
                anyLong(),
                any(),
                eq(null),
                eq(null),
                eq(null)
        )).thenReturn(page);

        // when
        final ResultActions resultActions = performGetTeamAnnouncementAndTeamMiniProfileAfterLogin();

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
                                queryParameters(
                                        parameterWithName("cityName").description("지역 (시/도) 필터").optional(),
                                        parameterWithName("divisionName").description("지역 (시/군/구) 필터").optional(),
                                        parameterWithName("activityTagName").description("활동 방식 필터").optional()
                                ),
                                responseFields(
                                        fieldWithPath("content[].teamMiniProfileResponse.id").description("팀 미니 프로필 ID"),
                                        fieldWithPath("content[].teamMiniProfileResponse.sectorName").description("부문 이름"),
                                        fieldWithPath("content[].teamMiniProfileResponse.sizeType").description("팀 크기 유형"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamName").description("팀 이름"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamProfileTitle").description("팀 소개서 제목"),
                                        fieldWithPath("content[].teamMiniProfileResponse.isTeamActivate").description("팀 소개서 활성화 여부"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamLogoImageUrl").description("팀 로고 이미지 URL"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamKeywordNames").description("팀 키워드").optional(),

                                        fieldWithPath("content[].teamMemberAnnouncementResponse.id").description("팀원 공고 ID"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.teamLogoImageUrl").description("팀 로고 이미지 경로"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.teamName").description("팀 이름"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.mainBusiness").description("주요 업무"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.applicationProcess").description("지원 절차"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.isTeamSaved").type(JsonFieldType.BOOLEAN).description("로그인 사용자가 해당 팀원 공고 찜한 여부"),

                                        // 페이지와 관련된 필드 추가
                                        subsectionWithPath("pageable").ignored(),
                                        subsectionWithPath("sort").ignored(),
                                        fieldWithPath("last").description("마지막 페이지 여부"),
                                        fieldWithPath("totalPages").description("전체 페이지 수"),
                                        fieldWithPath("totalElements").description("전체 요소 수"),
                                        fieldWithPath("first").description("첫 페이지 여부"),
                                        fieldWithPath("size").description("페이지당 요소 수"),
                                        fieldWithPath("number").description("페이지 번호"),
                                        fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                                        fieldWithPath("empty").description("페이지가 비어 있는지 여부")
                                )
                        )
                );
    }


    // ----------------------------------- 팀원 찾기 테스트 코드 - 팀 찾기 테스트 구분 라인 ----------------------------------------
    // 로그인 이전
    // 팀원 찾기 테스트 코드
    private ResultActions performGetPrivateMiniProfileRequest() throws Exception {
         MockHttpServletRequestBuilder requestBuilder = get("/search/private/profile")
                        .contentType(APPLICATION_JSON);

        String cityName = null; // null로 예시 설정
        if (cityName != null) {
            requestBuilder.queryParam("cityName", cityName);
        }

        String divisionName = null; // null로 예시 설정
        if (divisionName != null) {
            requestBuilder.queryParam("divisionName", divisionName);
        }

        return mockMvc.perform(requestBuilder);
    }

    // 로그인 이후
    private ResultActions performGetBrowsePrivateMiniProfileRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/search/private/profile/login")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON);

        String cityName = null; // null로 예시 설정
        if (cityName != null) {
            requestBuilder.queryParam("cityName", cityName);
        }

        String divisionName = null; // null로 예시 설정
        if (divisionName != null) {
            requestBuilder.queryParam("divisionName", divisionName);
        }

        return mockMvc.perform(requestBuilder);
    }


    @Test
    @DisplayName("팀원 찾기를 진행할 수 있다.")
    void getPrivateMiniProfile() throws Exception {
        // given
        // 미니 프로필 응답 객체 생성
        final MiniProfileResponse miniProfileResponse = new MiniProfileResponse(
                1L,
                "시니어 소프트웨어 개발자",
                "https://image.linkit.im/images/linkit_logo.png",
                true,
                Arrays.asList("2024 레드닷 수상", "스타트업 경력", "서울대 디자인", "대기업 경력 3년"),
                "권동민",
                false
        );

        Page<MiniProfileResponse> page = new PageImpl<>(Collections.singletonList(miniProfileResponse));
        when(searchService.findPrivateMiniProfile(
                any(),
                eq(null),
                eq(null)
        )).thenReturn(page);

        // when
        final ResultActions resultActions = performGetPrivateMiniProfileRequest();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("cityName").description("지역 (시/도) 필터").optional(),
                                        parameterWithName("divisionName").description("지역 (시/군/구) 필터").optional()
                                ),
                                responseFields(
                                        fieldWithPath("content[].id").description("개인 미니 프로필 ID"),
                                        fieldWithPath("content[].profileTitle").description("프로필 제목"),
                                        fieldWithPath("content[].miniProfileImg").description("프로필 이미지 URL"),
                                        fieldWithPath("content[].isActivate").description("프로필 활성화 여부"),
                                        fieldWithPath("content[].myKeywordNames").description("키워드 목록"),
                                        fieldWithPath("content[].memberName").description("회원 이름"),
                                        fieldWithPath("content[].isPrivateSaved").type(JsonFieldType.BOOLEAN).description("찜하기 여부"),

                                        fieldWithPath("pageable").description("페이징 처리 객체"),
                                        fieldWithPath("sort.empty").description("정렬 규칙이 비어 있는지 여부"),
                                        fieldWithPath("sort.unsorted").description("정렬이 적용되지 않았는지 여부"),
                                        fieldWithPath("sort.sorted").description("정렬이 적용되었는지 여부"),
                                        fieldWithPath("last").description("마지막 페이지 여부"),
                                        fieldWithPath("totalPages").description("전체 페이지 수"),
                                        fieldWithPath("totalElements").description("전체 요소 수"),
                                        fieldWithPath("first").description("첫 페이지 여부"),
                                        fieldWithPath("size").description("페이지당 요소 수"),
                                        fieldWithPath("number").description("페이지 번호"),
                                        fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                                        fieldWithPath("empty").description("페이지가 비어 있는지 여부")
                                )
                        )
                );
    }

    @Test
    @DisplayName("로그인 이후 팀원 찾기를 진행할 수 있다.")
    void getBrowsePrivateMiniProfile() throws Exception {
        // given
        // 미니 프로필 응답 객체 생성
        final BrowseMiniProfileResponse browseMiniProfileResponse = new BrowseMiniProfileResponse(
                1L,
                "시니어 소프트웨어 개발자",
                "https://image.linkit.im/images/linkit_logo.png",
                true,
                Arrays.asList("2024 레드닷 수상", "스타트업 경력", "서울대 디자인", "대기업 경력 3년"),
                "권동민",
                true
        );

        Page<BrowseMiniProfileResponse> page = new PageImpl<>(Collections.singletonList(browseMiniProfileResponse));
        when(searchService.findPrivateMiniProfileLogin(
                anyLong(),
                any(),
                eq(null),
                eq(null)
        )).thenReturn(page);

        // when
        final ResultActions resultActions = performGetBrowsePrivateMiniProfileRequest();

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
                                queryParameters(
                                        parameterWithName("cityName").description("지역 (시/도) 필터").optional(),
                                        parameterWithName("divisionName").description("지역 (시/군/구) 필터").optional()
                                ),
                                responseFields(
                                        fieldWithPath("content[].id").description("개인 미니 프로필 ID"),
                                        fieldWithPath("content[].profileTitle").description("프로필 제목"),
                                        fieldWithPath("content[].miniProfileImg").description("프로필 이미지 URL"),
                                        fieldWithPath("content[].isActivate").description("프로필 활성화 여부"),
                                        fieldWithPath("content[].myKeywordNames").description("키워드 목록"),
                                        fieldWithPath("content[].memberName").description("회원 이름"),
                                        fieldWithPath("content[].isPrivateSaved").type(JsonFieldType.BOOLEAN).description("로그인 사용자가 해당 프로필 찜한 여부"),

                                        fieldWithPath("pageable").description("페이징 처리 객체"),
                                        fieldWithPath("sort.empty").description("정렬 규칙이 비어 있는지 여부"),
                                        fieldWithPath("sort.unsorted").description("정렬이 적용되지 않았는지 여부"),
                                        fieldWithPath("sort.sorted").description("정렬이 적용되었는지 여부"),
                                        fieldWithPath("last").description("마지막 페이지 여부"),
                                        fieldWithPath("totalPages").description("전체 페이지 수"),
                                        fieldWithPath("totalElements").description("전체 요소 수"),
                                        fieldWithPath("first").description("첫 페이지 여부"),
                                        fieldWithPath("size").description("페이지당 요소 수"),
                                        fieldWithPath("number").description("페이지 번호"),
                                        fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                                        fieldWithPath("empty").description("페이지가 비어 있는지 여부")
                                )
                        )
                );
    }
}
