package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.member.service.MemberService;
import liaison.linkit.profile.dto.request.IntroductionRequest;
import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
import liaison.linkit.profile.dto.response.ProfileResponse;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.awards.AwardsResponse;
import liaison.linkit.profile.dto.response.completion.CompletionResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import liaison.linkit.profile.service.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class ProfileControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;
    @MockBean
    private ProfileService profileService;
    @MockBean
    private ProfileOnBoardingService profileOnBoardingService;
    @MockBean
    private MiniProfileService miniProfileService;
    @MockBean
    private CompletionService completionService;
    @MockBean
    private AntecedentsService antecedentsService;
    @MockBean
    private EducationService educationService;
    @MockBean
    private AwardsService awardsService;
    @MockBean
    private ProfileRegionService profileRegionService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");

        // 서비스 계층 -> validate -> 리턴하는 값 (1L) 지정.
        doNothing().when(profileService).validateProfileByMember(1L);

        // 미니 프로필 리턴 Value 1L
        doNothing().when(miniProfileService).validateMiniProfileByMember(1L);
    }


    private void makeIntroduction() throws Exception{
        final IntroductionRequest introductionRequest = new IntroductionRequest(
                "자기소개 정보를 생성합니다."
        );
        final ResultActions resultActions = performCreateRequest(introductionRequest);
    }


    private ResultActions performGetProfileRequest() throws Exception {
        return mockMvc.perform(
                get("/private/profile")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performCreateRequest(final IntroductionRequest introductionRequest) throws Exception {
        return mockMvc.perform(
                post("/private/introduction")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(introductionRequest))
        );
    }

    private ResultActions performGetRequest() throws Exception {
        return mockMvc.perform(
                get("/profile/introduction")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performDeleteRequest() throws Exception {
        return mockMvc.perform(
                delete("/profile/introduction")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("내 이력서를 조회할 수 있다.")
    @Test
    void getProfile() throws Exception {
        // given
        final ProfileIsValueResponse profileIsValueResponse = new ProfileIsValueResponse(
                true,
                true,
                true,
                true,
                true,
                true,
                true
        );

        final boolean isPrivateProfileEssential = (
                        profileIsValueResponse.isProfileRegion() &&
                        profileIsValueResponse.isMiniProfile()
        );

        given(profileService.getProfileIsValue(1L)).willReturn(profileIsValueResponse);

        // 1. 미니 프로필 (V)
        final MiniProfileResponse miniProfileResponse = new MiniProfileResponse(
                1L,
                "시니어 소프트웨어 개발자",
                "https://image.linkit.im/images/linkit_logo.png",
                true,
                Arrays.asList("2024 레드닷 수상", "스타트업 경력", "서울대 디자인", "대기업 경력 3년"),
                "권동민",
                false
        );

        given(miniProfileService.getPersonalMiniProfile(1L)).willReturn(miniProfileResponse);

        // 2. 완성도 & 존재 여부 (V)
        final CompletionResponse completionResponse = new CompletionResponse(
                "100.0",
                true,
                true,
                true,
                true,
                true,
                true,
                true
        );

        given(completionService.getCompletion(1L)).willReturn(completionResponse);

        // 3. 자기소개
        final ProfileIntroductionResponse profileIntroductionResponse = new ProfileIntroductionResponse(
                "안녕하세요, 저는 다양한 프로젝트와 혁신적인 아이디어를 구현하는 데 열정을 가진 기획자입니다. 대학에서 경영학을 전공하고, 여러 기업에서 프로젝트 매니저와 기획자로서의 경험을 쌓아왔습니다.."
        );
        given(profileService.getProfileIntroduction(1L)).willReturn(profileIntroductionResponse);

        // 6. 활동 지역 및 위치 (V)
        final ProfileRegionResponse profileRegionResponse = new ProfileRegionResponse(
                "서울특별시",
                "강남구"
        );

        given(profileRegionService.getPersonalProfileRegion(1L)).willReturn(profileRegionResponse);

        // 7. 이력 (V)
        final AntecedentsResponse antecedentsResponse1 = new AntecedentsResponse(
                1L,
                "linkit",
                "프로젝트 매니저",
                "2023.03",
                "2024.10",
                false,
                "경력 설명입니다."
        );

        final AntecedentsResponse antecedentsResponse2 = new AntecedentsResponse(
                2L,
                "오더이즈",
                "프로젝트 매니저",
                "2023.03",
                "2023.06",
                false,
                "경력 설명입니다."
        );

        List<AntecedentsResponse> antecedentsResponses = Arrays.asList(antecedentsResponse1, antecedentsResponse2);

        given(antecedentsService.getAllAntecedents(1L)).willReturn(antecedentsResponses);

        // 8. 학력
        final EducationResponse educationResponse1 = new EducationResponse(
                1L,
                2022,
                2024,
                "홍익대학교",
                "컴퓨터공학과",
                "재학 중"
        );

        final EducationResponse educationResponse2 = new EducationResponse(
                2L,
                2021,
                2025,
                "홍익대학교",
                "예술학과",
                "졸업"
        );

        List<EducationResponse> educationResponses = Arrays.asList(educationResponse1, educationResponse2);
        given(educationService.getAllEducations(1L))
                .willReturn(educationResponses);

        // 9. 수상
        final AwardsResponse firstAwardsResponse = new AwardsResponse(
                1L,
                "홍익대학교 창업경진대회",
                "대상",
                "홍익대학교 창업교육센터",
                2024,
                5,
                "홍익대학교 창업경진대회에서 1등이라는 성과를 이뤄냈습니다."
        );

        final AwardsResponse secondAwardsResponse = new AwardsResponse(
                2L,
                "성균관대학교 캠퍼스타운 입주경진대회",
                "선정",
                "성균관대학교 캠퍼스타운",
                2024,
                3,
                "성균관대학교 캠퍼스타운에 최종 선정되었습니다."
        );
        final List<AwardsResponse> awardsResponses = Arrays.asList(firstAwardsResponse, secondAwardsResponse);
        given(awardsService.getAllAwards(1L)).willReturn(awardsResponses);

        final ProfileResponse profileResponse = new ProfileResponse(
                isPrivateProfileEssential,
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                profileRegionResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses
        );

        given(profileService.getProfileResponse(
                isPrivateProfileEssential,
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                profileRegionResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses
        )).willReturn(profileResponse);

        // when
        final ResultActions resultActions = performGetProfileRequest();

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
                                        fieldWithPath("privateProfileEssential").type(JsonFieldType.BOOLEAN).description("내 이력서 필수 입력 항목 존재 여부"),
                                        // miniProfileResponse
                                        subsectionWithPath("miniProfileResponse").description("사용자의 미니 프로필 정보"),
                                        fieldWithPath("miniProfileResponse.profileTitle").type(JsonFieldType.STRING).description("프로필의 제목"),
                                        fieldWithPath("miniProfileResponse.miniProfileImg").type(JsonFieldType.STRING).description("미니 프로필 이미지 URL"),
                                        fieldWithPath("miniProfileResponse.myKeywordNames").type(JsonFieldType.ARRAY).description("나를 소개하는 키워드 목록"),
                                        fieldWithPath("miniProfileResponse.isActivate").type(JsonFieldType.BOOLEAN).description("미니 프로필 활성화 여부"),
                                        fieldWithPath("miniProfileResponse.memberName").type(JsonFieldType.STRING).description("회원 이름"),

                                        // completionResponse
                                        subsectionWithPath("completionResponse").description("프로필의 완성도 정보"),
                                        fieldWithPath("completionResponse.completion").type(JsonFieldType.STRING).description("프로필 완성도 (백분율)"),
                                        fieldWithPath("completionResponse.introduction").type(JsonFieldType.BOOLEAN).description("소개의 완성 여부"),
                                        fieldWithPath("completionResponse.profileRegion").type(JsonFieldType.BOOLEAN).description("지역 정보의 완성 여부"),
                                        fieldWithPath("completionResponse.antecedents").type(JsonFieldType.BOOLEAN).description("이력 사항의 완성 여부"),
                                        fieldWithPath("completionResponse.education").type(JsonFieldType.BOOLEAN).description("교육 이력의 완성 여부"),
                                        fieldWithPath("completionResponse.awards").type(JsonFieldType.BOOLEAN).description("수상 이력의 완성 여부"),
                                        fieldWithPath("completionResponse.attach").type(JsonFieldType.BOOLEAN).description("첨부 파일의 유무"),

                                        // profileIntroductionResponse
                                        subsectionWithPath("profileIntroductionResponse").description("프로필 소개"),
                                        fieldWithPath("profileIntroductionResponse.introduction").type(JsonFieldType.STRING).description("소개 내용"),

                                        // profileRegionResponse
                                        subsectionWithPath("profileRegionResponse").description("활동 지역 및 위치 응답"),
                                        fieldWithPath("profileRegionResponse.cityName").type(JsonFieldType.STRING).description("시/도 이름"),
                                        fieldWithPath("profileRegionResponse.divisionName").type(JsonFieldType.STRING).description("시/군/구 이름"),

                                        // antecedentsResponse
                                        subsectionWithPath("antecedentsResponse").description("과거 경력 정보"),
                                        fieldWithPath("antecedentsResponse[].id").type(JsonFieldType.NUMBER).description("경력 ID"),
                                        fieldWithPath("antecedentsResponse[].projectName").type(JsonFieldType.STRING).description("프로젝트 이름"),
                                        fieldWithPath("antecedentsResponse[].projectRole").type(JsonFieldType.STRING).description("프로젝트 역할"),
                                        fieldWithPath("antecedentsResponse[].startDate").type(JsonFieldType.STRING).description("시작 연도/월"),
                                        fieldWithPath("antecedentsResponse[].endDate").type(JsonFieldType.STRING).description("종료 연도/월"),
                                        fieldWithPath("antecedentsResponse[].retirement").type(JsonFieldType.BOOLEAN).description("퇴직 여부"),

                                        // educationResponse
                                        subsectionWithPath("educationResponse").description("교육 이력 정보"),
                                        fieldWithPath("educationResponse[].id").type(JsonFieldType.NUMBER).description("교육 이력 ID"),
                                        fieldWithPath("educationResponse[].admissionYear").type(JsonFieldType.NUMBER).description("입학 연도"),
                                        fieldWithPath("educationResponse[].graduationYear").type(JsonFieldType.NUMBER).description("졸업 연도"),
                                        fieldWithPath("educationResponse[].universityName").type(JsonFieldType.STRING).description("대학교 이름"),
                                        fieldWithPath("educationResponse[].majorName").type(JsonFieldType.STRING).description("전공 이름"),
                                        fieldWithPath("educationResponse[].degreeName").type(JsonFieldType.STRING).description("학위명"),

                                        // awardsResponse
                                        subsectionWithPath("awardsResponse").description("수상 이력 정보"),
                                        fieldWithPath("awardsResponse[].id").type(JsonFieldType.NUMBER).description("수상 ID"),
                                        fieldWithPath("awardsResponse[].awardsName").type(JsonFieldType.STRING).description("수상 이름"),
                                        fieldWithPath("awardsResponse[].ranking").type(JsonFieldType.STRING).description("수상 순위"),
                                        fieldWithPath("awardsResponse[].organizer").type(JsonFieldType.STRING).description("주최자"),
                                        fieldWithPath("awardsResponse[].awardsYear").type(JsonFieldType.NUMBER).description("수상 연도"),
                                        fieldWithPath("awardsResponse[].awardsMonth").type(JsonFieldType.NUMBER).description("수상 월"),
                                        fieldWithPath("awardsResponse[].awardsDescription").type(JsonFieldType.STRING).description("수상 내용")
                                )
                        ));

    }

    @DisplayName("프로필 자기소개 항목을 생성할 수 있다.")
    @Test
    void createProfileIntroduction() throws Exception {
        // given
        final IntroductionRequest introductionRequest = new IntroductionRequest(
                "프로필 자기소개 항목을 입력합니다."
        );

        // when
        final ResultActions resultActions = performCreateRequest(introductionRequest);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("introduction")
                                                .type(JsonFieldType.STRING)
                                                .description("자기소개")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }
}
