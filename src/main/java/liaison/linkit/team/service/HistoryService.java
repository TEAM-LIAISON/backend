//package liaison.linkit.team.service;
//
//import liaison.linkit.global.exception.AuthException;
//import liaison.linkit.global.exception.BadRequestException;
//import liaison.linkit.team.domain.History;
//import liaison.linkit.team.domain.TeamProfile;
//import liaison.linkit.team.domain.repository.HistoryRepository;
//import liaison.linkit.team.domain.repository.TeamProfileRepository;
//import liaison.linkit.team.dto.request.HistoryCreateRequest;
//import liaison.linkit.team.dto.request.HistoryUpdateRequest;
//import liaison.linkit.team.dto.response.HistoryResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static liaison.linkit.global.exception.ExceptionCode.*;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class HistoryService {
//
//    private final TeamProfileRepository teamProfileRepository;
//    private final HistoryRepository historyRepository;
//
//    // 회원에 대한 팀 소개서 정보를 가져온다. (1개만 저장되어 있음)
//    private TeamProfile getTeamProfileByMember(final Long memberId) {
//        return teamProfileRepository.findByMemberId(memberId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
//    }
//
//    public void validateHistoryByMember(final Long memberId) {
//        if (!historyRepository.existsByTeamProfileId(getTeamProfileByMember(memberId).getId())) {
//            throw new AuthException(INVALID_HISTORY_WITH_TEAM_PROFILE);
//        }
//    }
//
//    @Transactional(readOnly = true)
//    public List<HistoryResponse> getAllHistories(final Long memberId) {
//        final TeamProfile teamProfile = getTeamProfileByMember(memberId);
//        final List<History> histories = historyRepository.findAllByTeamProfileId(teamProfile.getId());
//        return histories.stream()
//                .map(this::getHistoryResponse)
//                .toList();
//    }
//
//    private HistoryResponse getHistoryResponse(final History history) {
//        return HistoryResponse.of(history);
//    }
//
//    public void save(final Long memberId, final HistoryCreateRequest historyCreateRequest) {
//        final TeamProfile teamProfile = getTeamProfileByMember(memberId);
//
//        final History newHistory = History.of(
//                teamProfile,
//                historyCreateRequest.getHistoryOneLineIntroduction(),
//                historyCreateRequest.getStartYear(),
//                historyCreateRequest.getStartMonth(),
//                historyCreateRequest.getEndYear(),
//                historyCreateRequest.getEndMonth(),
//                historyCreateRequest.getHistoryIntroduction(),
//                historyCreateRequest.isInProgress()
//        );
//
//        historyRepository.save(newHistory);
//
//
//    }
//
//    @Transactional(readOnly = true)
//    public HistoryResponse getHistoryDetail(final Long memberId) {
//        final History history = historyRepository.findById(historyId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_HISTORY_ID));
//        return HistoryResponse.personalHistory(history);
//    }
//
//
////    public void update(final Long memberId, final HistoryUpdateRequest historyUpdateRequest) {
////        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);
////        final Long historyId = validateHistoryByMember(memberId);
////
////        final History history = historyRepository.findById(historyId)
////                .orElseThrow(() -> new BadRequestException(NOT_FOUND_HISTORY_ID));
////
////        history.update(historyUpdateRequest);
////        historyRepository.save(history);
////    }
////
////    public void delete(final Long memberId) {
////        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);
////        final Long historyId = validateHistoryByMember(memberId);
////
////        if (!historyRepository.existsById(historyId)) {
////            throw new BadRequestException(NOT_FOUND_HISTORY_ID);
////        }
////
////        historyRepository.deleteById(historyId);
////
////        // 프로그레스바 업데이트 로직 구현 필요
//    }
//
//
//}
