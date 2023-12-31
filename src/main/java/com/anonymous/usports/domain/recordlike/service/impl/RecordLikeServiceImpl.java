package com.anonymous.usports.domain.recordlike.service.impl;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.repository.RecordRepository;
import com.anonymous.usports.domain.recordlike.dto.RecordLikeDto;
import com.anonymous.usports.domain.recordlike.entity.RecordLikeEntity;
import com.anonymous.usports.domain.recordlike.repository.RecordLikeRepository;
import com.anonymous.usports.domain.recordlike.service.RecordLikeService;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.RecordException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordLikeServiceImpl implements RecordLikeService {

  private final RecordRepository recordRepository;
  private final MemberRepository memberRepository;
  private final RecordLikeRepository recordLikeRepository;

  /**
   * 좋아요 신청/취소
   *
   * @param recordId      좋아요 신청/취소 할 기록 Id
   * @param loginMemberId 로그인 한 회원 Id
   * @return LikeDto 형태로 반환
   */
  @Override
  public RecordLikeDto switchLikeOrCancel(Long recordId, Long loginMemberId) {
    MemberEntity loginMember = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    RecordEntity record = recordRepository.findById(recordId)
        .orElseThrow(() -> new RecordException(ErrorCode.RECORD_NOT_FOUND));
    if (record.getMember().getMemberId().equals(loginMemberId)) {
      throw new RecordException(ErrorCode.SELF_LIKE_NOT_ALLOWED);
    }
    Optional<RecordLikeEntity> like = recordLikeRepository.findByRecordAndMember(record,
        loginMember);
    if (!like.isPresent()) {
      record.setCountRecordLikes(record.getCountRecordLikes() + 1);
      recordRepository.save(record);
      RecordLikeEntity likeEntity = RecordLikeEntity.builder()
          .record(record)
          .member(loginMember)
          .build();
      RecordLikeEntity recordLike = recordLikeRepository.save(likeEntity);
      return RecordLikeDto.fromEntity(recordLike, ResponseConstant.LIKE_RECORD);
    }
    record.setCountRecordLikes(record.getCountRecordLikes() - 1);
    recordRepository.save(record);
    recordLikeRepository.delete(like.get());
    return RecordLikeDto.builder()
        .recordLikeId(like.get().getRecordLikeId())
        .recordId(recordId)
        .memberId(loginMemberId)
        .message(ResponseConstant.CANCEL_LIKE_RECORD)
        .build();

  }
}
