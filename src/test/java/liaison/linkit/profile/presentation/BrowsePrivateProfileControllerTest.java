package liaison.linkit.profile.presentation;

import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.awards.AwardsResponse;
import liaison.linkit.profile.dto.response.browse.BrowsePrivateProfileResponse;
import liaison.linkit.profile.dto.response.completion.CompletionResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import liaison.linkit.profile.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BrowsePrivateProfileController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class BrowsePrivateProfileControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @MockBean
    public ProfileOnBoardingService profileOnBoardingService;
    @MockBean
    public ProfileService profileService;
    @MockBean
    public MiniProfileService miniProfileService;
    @MockBean
    public CompletionService completionService;
    @MockBean
    public AntecedentsService antecedentsService;
    @MockBean
    public EducationService educationService;
    @MockBean
    public AwardsService awardsService;
    @MockBean
    public ProfileRegionService profileRegionService;
    @MockBean
    public BrowsePrivateProfileService browsePrivateProfileService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetBrowsePrivateProfile(
        final int miniProfileId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/browse/private/profile/{miniProfileId}", miniProfileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("타인의 내 이력서를 열람할 수 있다.")
    @Test
    void getBrowsePrivateProfile() throws Exception {
        // given
        final int miniProfileId = 1;
        given(browsePrivateProfileService.getTargetPrivateProfileIdByMiniProfileId(1L)).willReturn(1L);

        final ProfileIsValueResponse profileIsValueResponse = new ProfileIsValueResponse(
                true, true, true, true, true, true, true
        );
        given(browsePrivateProfileService.getProfileIsValue(1L)).willReturn(profileIsValueResponse);

        final MiniProfileResponse miniProfileResponse = new MiniProfileResponse(
                1L, "시니어 소프트웨어 개발자", "https://image.linkit.im/images/linkit_logo.png", true,
                Arrays.asList("2024 레드닷 수상", "스타트업 경력", "서울대 디자인", "대기업 경력 3년"), "권동민", true
        );
        given(miniProfileService.getBrowsePersonalMiniProfile(1L, 1L)).willReturn(miniProfileResponse);

        final CompletionResponse completionResponse = new CompletionResponse(
                "100.0", true, true, true, true, true, true, true
        );
        given(completionService.getCompletion(1L)).willReturn(completionResponse);

        final ProfileIntroductionResponse profileIntroductionResponse = new ProfileIntroductionResponse(
                "안녕하세요, 저는 다양한 프로젝트와 혁신적인 아이디어를 구현하는 데 열정을 가진 기획자입니다. 대학에서 경영학을 전공하고, 여러 기업에서 프로젝트 매니저와 기획자로서의 경험을 쌓아왔습니다."
        );
        given(profileService.getProfileIntroduction(1L)).willReturn(profileIntroductionResponse);

        final ProfileRegionResponse profileRegionResponse = new ProfileRegionResponse("서울특별시", "강남구");
        given(profileRegionService.getPersonalProfileRegion(1L)).willReturn(profileRegionResponse);

        final AntecedentsResponse antecedentsResponse1 = new AntecedentsResponse(
                2L, "linkit", "프로젝트 매니저", "2023.03", "2024.10", false, "경력 설명입니다."
        );
        final AntecedentsResponse antecedentsResponse2 = new AntecedentsResponse(
                3L, "오더이즈", "프로젝트 매니저", "2023.03", "2023.06", false, "경력 설명입니다."
        );
        List<AntecedentsResponse> antecedentsResponses = Arrays.asList(antecedentsResponse1, antecedentsResponse2);
        given(antecedentsService.getAllAntecedents(1L)).willReturn(antecedentsResponses);

        final EducationResponse educationResponse1 = new EducationResponse(
                2L, 2022, 2024, "홍익대학교", "컴퓨터공학과", "재학 중"
        );
        final EducationResponse educationResponse2 = new EducationResponse(
                3L, 2021, 2025, "홍익대학교", "예술학과", "졸업"
        );
        List<EducationResponse> educationResponses = Arrays.asList(educationResponse1, educationResponse2);
        given(educationService.getAllEducations(1L)).willReturn(educationResponses);

        final AwardsResponse firstAwardsResponse = new AwardsResponse(
                2L, "홍익대학교 창업경진대회", "대상", "홍익대학교 창업교육센터", 2024, 5, "홍익대학교 창업경진대회에서 1등이라는 성과를 이뤄냈습니다."
        );
        final AwardsResponse secondAwardsResponse = new AwardsResponse(
                3L, "성균관대학교 캠퍼스타운 입주경진대회", "선정", "성균관대학교 캠퍼스타운", 2024, 3, "성균관대학교 캠퍼스타운에 최종 선정되었습니다."
        );
        final List<AwardsResponse> awardsResponses = Arrays.asList(firstAwardsResponse, secondAwardsResponse);
        given(awardsService.getAllAwards(1L)).willReturn(awardsResponses);

        when(browsePrivateProfileService.getProfileResponse(
                any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(BrowsePrivateProfileResponse.privateProfile(
                1L,
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                profileRegionResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses
        ));

        // when
        final ResultActions resultActions = performGetBrowsePrivateProfile(miniProfileId);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("miniProfileId")
                                        .description("미니 프로필 ID")
                        ),
                        responseFields(
                                fieldWithPath("profileId").type(JsonFieldType.NUMBER).description("타깃 열람 프로필 ID"),

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
                                )));
    }
}
