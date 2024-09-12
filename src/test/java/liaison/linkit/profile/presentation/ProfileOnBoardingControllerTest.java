package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.member.service.MemberService;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileOnBoardingIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.onBoarding.OnBoardingProfileResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProfileOnBoardingController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class ProfileOnBoardingControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProfileOnBoardingService profileOnBoardingService;
    @MockBean
    private ProfileService profileService;
    @MockBean
    public MiniProfileService miniProfileService;
    @MockBean
    public ProfileRegionService profileRegionService;
    @MockBean
    public EducationService educationService;
    @MockBean
    public AntecedentsService antecedentsService;
    @MockBean
    public MemberService memberService;

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

    private ResultActions performGetOnBoardingProfileRequest() throws Exception {
        return mockMvc.perform(
                get("/private/onBoarding")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    @DisplayName("내 이력서 온보딩 과정의 모든 정보를 조회할 수 있다.")
    @Test
    void getOnBoardingProfile() throws Exception {
        // given
        final ProfileOnBoardingIsValueResponse profileOnBoardingIsValueResponse = new ProfileOnBoardingIsValueResponse(
                true,
                true,
                true,
                true,
                true
        );

        given(profileOnBoardingService.getProfileOnBoardingIsValue(1L)).willReturn(profileOnBoardingIsValueResponse);

        // 3. 지역 및 위치 정보
        final ProfileRegionResponse profileRegionResponse = new ProfileRegionResponse(
                "서울특별시",
                "강남구"
        );

        given(profileRegionService.getPersonalProfileRegion(1L)).willReturn(profileRegionResponse);

        // 3. 학교 정보
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

        // 4. 이력 정보
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

        // 5. 미니 프로필 정보
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

        // 온보딩 응답
        final OnBoardingProfileResponse onBoardingProfileResponse = new OnBoardingProfileResponse(
                profileRegionResponse,
                educationResponses,
                antecedentsResponses,
                miniProfileResponse
        );

        given(profileOnBoardingService.getOnBoardingProfile(
                profileRegionResponse,
                educationResponses,
                antecedentsResponses,
                miniProfileResponse))
                .willReturn(onBoardingProfileResponse);

        // when
        final ResultActions resultActions = performGetOnBoardingProfileRequest();

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
                                        subsectionWithPath("profileRegionResponse").description("지역 및 위치 항목").attributes(field("constraint", "객체")),
                                        fieldWithPath("profileRegionResponse.cityName").description("시/구 이름").attributes(field("constraint", "문자열")),
                                        fieldWithPath("profileRegionResponse.divisionName").description("시/군/구 이름").attributes(field("constraint", "문자열")),

                                        subsectionWithPath("educationResponses").description("학력 항목").attributes(field("constraint", "객체 (배열)")),
                                        fieldWithPath("educationResponses[].id").description("학력 ID").attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("educationResponses[].admissionYear").description("입학 연도").attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("educationResponses[].graduationYear").description("졸업 연도").attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("educationResponses[].universityName").description("학교 이름").attributes(field("constraint", "문자열")),
                                        fieldWithPath("educationResponses[].majorName").description("전공 이름").attributes(field("constraint", "문자열")),
                                        fieldWithPath("educationResponses[].degreeName").description("학위 이름").attributes(field("constraint", "문자열")),

                                        subsectionWithPath("antecedentsResponses").description("이력 항목").attributes(field("constraint", "객체(배열)")),
                                        fieldWithPath("antecedentsResponses[].projectName").description("회사 이름").attributes(field("constraint", "문자열")),
                                        fieldWithPath("antecedentsResponses[].projectRole").description("포지션").attributes(field("constraint", "문자열")),
                                        fieldWithPath("antecedentsResponses[].startDate").type(JsonFieldType.STRING).description("시작 연도/월"),
                                        fieldWithPath("antecedentsResponses[].endDate").type(JsonFieldType.STRING).description("종료 연도/월"),
                                        fieldWithPath("antecedentsResponses[].retirement").description("재직 여부").attributes(field("constraint", "boolean")),

                                        subsectionWithPath("miniProfileResponse").description("미니 프로필(내 이력서) 항목").attributes(field("constraint", "객체 (배열)")),
                                        fieldWithPath("miniProfileResponse.profileTitle").type(JsonFieldType.STRING).description("프로필의 제목"),
                                        fieldWithPath("miniProfileResponse.miniProfileImg").type(JsonFieldType.STRING).description("미니 프로필 이미지 URL"),
                                        fieldWithPath("miniProfileResponse.myKeywordNames").type(JsonFieldType.ARRAY).description("나를 소개하는 키워드 목록"),
                                        fieldWithPath("miniProfileResponse.isActivate").type(JsonFieldType.BOOLEAN).description("미니 프로필 활성화 여부"),
                                        fieldWithPath("miniProfileResponse.memberName").type(JsonFieldType.STRING).description("회원 이름")
                                )
                        )
                );

    }


}
