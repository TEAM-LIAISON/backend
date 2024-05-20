package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileCreateRequest;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileUpdateRequest;
import liaison.linkit.profile.dto.response.MiniProfileResponse;
import liaison.linkit.profile.service.MiniProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mini-profile")
public class MiniProfileController {

    private final MiniProfileService miniProfileService;

    // 해당 회원이 가지고 있는 미니 프로필 정보를 가져옴
    @GetMapping
    @MemberOnly
    public ResponseEntity<MiniProfileResponse> getMiniProfile(
            @Auth final Accessor accessor
    ) {
        Long miniProfileId = miniProfileService.validateMiniProfileByMember(accessor.getMemberId());
        final MiniProfileResponse miniProfileResponse = miniProfileService.getMiniProfileDetail(miniProfileId);
        return ResponseEntity.ok().body(miniProfileResponse);
    }

    // 미니 프로필 생성 요청
    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createMiniProfile(
            @Auth final Accessor accessor,
            @RequestPart @Valid MiniProfileCreateRequest miniProfileCreateRequest,
            @RequestPart MultipartFile miniProfileImage
    ){
        miniProfileService.save(accessor.getMemberId(), miniProfileCreateRequest, miniProfileImage);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 미니 프로필 항목 수정
    @PatchMapping
    @MemberOnly
    public ResponseEntity<Void> updateMiniProfile(
            @Auth final Accessor accessor,
            @RequestBody @Valid final MiniProfileUpdateRequest miniProfileUpdateRequest
    ){
        Long miniProfileId = miniProfileService.validateMiniProfileByMember(accessor.getMemberId());
        miniProfileService.update(miniProfileId, miniProfileUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    // 미니 프로필 항목 삭제
    @DeleteMapping
    @MemberOnly
    public ResponseEntity<Void> deleteMiniProfile(@Auth final Accessor accessor) {
        Long miniProfileId = miniProfileService.validateMiniProfileByMember(accessor.getMemberId());
        miniProfileService.delete(miniProfileId);
        return ResponseEntity.noContent().build();
    }
}
