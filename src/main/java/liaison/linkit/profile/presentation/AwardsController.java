package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.AwardsCreateRequest;
import liaison.linkit.profile.dto.response.AwardsResponse;
import liaison.linkit.profile.service.AwardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/awards")
public class AwardsController {
    private final AwardsService awardsService;

    // 이력서 수상 항목 전체 조회
    @GetMapping("/list")
    @MemberOnly
    public ResponseEntity<List<AwardsResponse>> getAwardsList(@Auth final Accessor accessor) {
        final List<AwardsResponse> awardsResponses = awardsService.getAllAwards(accessor.getMemberId());
        return ResponseEntity.ok().body(awardsResponses);
    }

    // 이력서 수상 항목 1개 생성
    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createAwards(
            @Auth final Accessor accessor,
            @RequestBody @Valid AwardsCreateRequest awardsCreateRequest
    ){
        awardsService.save(accessor.getMemberId(), awardsCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 이력서 수상 항목 1개 조회
    @GetMapping
    @MemberOnly
    public ResponseEntity<AwardsResponse> getAwards(
            @Auth final Accessor accessor
    ) {
        awardsService.validateAwardsByMember(accessor.getMemberId());
        final AwardsResponse awardsResponse = awardsService.getAwardsDetail(accessor.getMemberId());

        return ResponseEntity.ok().body(awardsResponse);
    }

//    // 이력서 수상 항목 1개 수정
//    @PatchMapping
//    @MemberOnly
//    public ResponseEntity<Void> updateAwards(
//            @Auth final Accessor accessor,
//            @RequestBody @Valid final AwardsUpdateRequest awardsUpdateRequest
//    ) {
//        awardsService.update(accessor.getMemberId(), awardsUpdateRequest);
//        return ResponseEntity.noContent().build();
//    }
//
//    // 이력서 수상 항목 1개 삭제
//    @DeleteMapping
//    @MemberOnly
//    public ResponseEntity<Void> deleteAwards(@Auth final Accessor accessor){
//        awardsService.delete(accessor.getMemberId());
//        return ResponseEntity.noContent().build();
//    }

}
