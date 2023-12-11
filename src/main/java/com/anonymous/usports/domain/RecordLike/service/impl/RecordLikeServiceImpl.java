package com.anonymous.usports.domain.RecordLike.service.impl;

import com.anonymous.usports.domain.RecordLike.dto.RecordLikeDto;
import com.anonymous.usports.domain.RecordLike.entity.RecordLikeEntity;
import com.anonymous.usports.domain.RecordLike.repository.RecordLikeRepository;
import com.anonymous.usports.domain.RecordLike.service.RecordLikeService;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.repository.RecordRepository;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.RecordException;
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
   * @param recordId 좋아요 신청/취소 할 기록 Id
   * @param loginMemberId 로그인 한 회원 Id
   * @return LikeDto 형태로 반환
   */
  @Override
  public RecordLikeDto switchLike(Long recordId, Long loginMemberId) {
    MemberEntity loginMember = memberRepository.findById(loginMemberId)
        .orElseThrow(()->new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    RecordEntity record = recordRepository.findById(recordId)
        .orElseThrow(()->new RecordException(ErrorCode.RECORD_NOT_FOUND));
    if(record.getMember().getMemberId().equals(loginMemberId)){
      throw new RecordException(ErrorCode.SELF_LIKE_NOT_ALLOWED);
    }
    RecordLikeEntity like = recordLikeRepository.findByRecordAndMember(record, loginMember);
    if(like==null) {
      like = RecordLikeEntity.builder()
          .record(record)
          .member(loginMember)
          .build();
      record.setCountRecordLikes(record.getCountRecordLikes()+1);
      recordRepository.save(record);
      RecordLikeEntity recordLike = recordLikeRepository.save(like);
      RecordLikeDto recordLikeDto = RecordLikeDto.fromEntity(recordLike);
      recordLikeDto.setMessage(ResponseConstant.LIKE_RECORD);

      return recordLikeDto;
    }
    record.setCountRecordLikes(record.getCountRecordLikes()-1);
    recordRepository.save(record);
    RecordLikeDto recordLikeDto = RecordLikeDto.builder()
        .recordLikeId(like.getRecordLikeId())
        .recordId(recordId)
        .memberId(loginMemberId)
        .message(ResponseConstant.CANCEL_LIKE_RECORD)
        .build();
    recordLikeRepository.delete(like);

    return recordLikeDto;
  }
}
