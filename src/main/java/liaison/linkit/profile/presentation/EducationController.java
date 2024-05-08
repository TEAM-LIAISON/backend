package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.EducationCreateRequest;
import liaison.linkit.profile.dto.request.EducationUpdateRequest;
import liaison.linkit.profile.dto.response.EducationResponse;
import liaison.linkit.profile.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/education")
public class EducationController {
    private final EducationService educationService;

    // 교육 항목 전체 조회
    @GetMapping("/list")
    @MemberOnly
    public ResponseEntity<List<EducationResponse>> getEducationList(@Auth final Accessor accessor) {
        final List<EducationResponse> educationResponses = educationService.getAllEducations(accessor.getMemberId());
        return ResponseEntity.ok().body(educationResponses);
    }

    // 교육 항목 1개 생성
    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createEducation(
            @Auth final Accessor accessor,
            @RequestBody @Valid EducationCreateRequest educationCreateRequest
    ) {
        educationService.save(accessor.getMemberId(), educationCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 교육 항목 1개 조회
    @GetMapping
    @MemberOnly
    public ResponseEntity<EducationResponse> getEducation(@Auth final Accessor accessor) {
        Long educationId = educationService.validateEducationByMember(accessor.getMemberId());
        final EducationResponse educationResponse = educationService.getEducationDetail(educationId);
        return ResponseEntity.ok().body(educationResponse);
    }

    // 교육 항목 1개 수정
    @PutMapping
    @MemberOnly
    public ResponseEntity<Void> updateEducation(
            @Auth final Accessor accessor,
            @RequestBody @Valid final EducationUpdateRequest educationUpdateRequest
    ){
        Long educationId = educationService.validateEducationByMember(accessor.getMemberId());
        educationService.update(educationId, educationUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    // 교육 항목 1개 삭제
    @DeleteMapping
    @MemberOnly
    public ResponseEntity<Void> deleteEducation(@Auth final Accessor accessor) {
        Long educationId = educationService.validateEducationByMember(accessor.getMemberId());
        educationService.delete(educationId);
        return ResponseEntity.noContent().build();
    }
}
