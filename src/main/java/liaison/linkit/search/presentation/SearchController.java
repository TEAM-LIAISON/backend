package liaison.linkit.search.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.search.dto.response.SearchTeamProfileResponse;
import liaison.linkit.search.dto.response.browseAfterLogin.BrowseMiniProfileResponse;
import liaison.linkit.search.dto.response.browseAfterLogin.SearchBrowseTeamProfileResponse;
import liaison.linkit.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
// 팀 찾기 및 팀원 찾기 컨트롤러
public class SearchController {

    public final SearchService searchService;

    // 팀원 찾기 구현부
    @GetMapping("/search/private/profile")
    public ResponseEntity<Page<MiniProfileResponse>> findPrivateMiniProfile(
            @PageableDefault(size = 1000, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            // 필터링 항목 4. 활동 지역 (시/도)
            @RequestParam(required = false) final String cityName,
            // 필터링 항목 4. 활동 지역 (시/군/구)
            @RequestParam(required = false) final String divisionName
    ) {
        log.info("팀원 찾기 요청이 들어왔습니다.");
        final Page<MiniProfileResponse> privateMiniProfiles = searchService.findPrivateMiniProfile(
                pageable,
                cityName,
                divisionName
        );
        log.info("privateMiniProfiles={}",privateMiniProfiles);
        return ResponseEntity.ok(privateMiniProfiles);
    }

    // 로그인 이후 팀원 찾기 컨트롤러
    @GetMapping("/search/private/profile/login")
    @MemberOnly
    public ResponseEntity<Page<BrowseMiniProfileResponse>> findPrivateMiniProfileLogin(
            @Auth final Accessor accessor,
            @PageableDefault(size = 1000, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) final String cityName,
            @RequestParam(required = false) final String divisionName
    ) {
        log.info("로그인 상태에서 팀원 찾기 요청이 들어왔습니다.");
        final Page<BrowseMiniProfileResponse> browseMiniProfileResponses = searchService.findPrivateMiniProfileLogin(
                accessor.getMemberId(),
                pageable,
                cityName,
                divisionName
        );

        return ResponseEntity.ok(browseMiniProfileResponses);
    }

    // 팀 찾기 컨트롤러
    @GetMapping("/search/team/profile")
    public ResponseEntity<Page<SearchTeamProfileResponse>> findSearchTeamProfile(
            @PageableDefault(size = 1000) final Pageable pageable,
            // 필터링 항목 4. 활동 지역 (시/도)
            @RequestParam(required = false) final String cityName,
            // 필터링 항목 4. 활동 지역 (시/군/구)
            @RequestParam(required = false) final String divisionName,
            // 필터링 항목 5. 활동 방식
            @RequestParam(required = false) final List<String> activityTagName
    ) {
        log.info("팀 찾기(팀원 공고) 요청이 들어왔습니다.");
        final Page<SearchTeamProfileResponse> searchTeamProfileResponsePage = searchService.findTeamMemberAnnouncementsWithTeamMiniProfile(
                pageable,
                cityName,
                divisionName,
                activityTagName
        );
        return ResponseEntity.ok(searchTeamProfileResponsePage);
    }

    // 로그인 이후 팀 찾기 컨트롤러
    @GetMapping("/search/team/profile/login")
    @MemberOnly
    public ResponseEntity<Page<SearchBrowseTeamProfileResponse>> findSearchTeamProfileAfterLogin(
            @Auth final Accessor accessor,
            @PageableDefault(size = 1000) final Pageable pageable,
            @RequestParam(required = false) final String cityName,
            @RequestParam(required = false) final String divisionName,
            @RequestParam(required = false) final List<String> activityTagName
    ) {
        log.info("팀 찾기(팀원 공고) 요청이 들어왔습니다.");
        final Page<SearchBrowseTeamProfileResponse> searchTeamProfileResponsePage = searchService.findTeamMemberAnnouncementsWithTeamMiniProfileAfterLogin(
                accessor.getMemberId(),
                pageable,
                cityName,
                divisionName,
                activityTagName
        );
        return ResponseEntity.ok(searchTeamProfileResponsePage);
    }

    @GetMapping("/search/private/profile/v2")
    @MemberOnly
    public ResponseEntity<?> searchPrivateMiniProfileLogin(
            @RequestParam(value = "lastIndex", required = false) final Long lastIndex,
            @RequestParam(required = false) final String cityName,
            @RequestParam(required = false) final String divisionName,
            final Pageable pageable
    ) {
        final Slice<MiniProfileResponse> miniProfileResponses = searchService.searchPrivateMiniProfile(
                lastIndex,
                pageable,
                cityName,
                divisionName
        );
        return ResponseEntity.ok(miniProfileResponses);
    }

}
