package com.anonymous.usports.domain.record.service.impl;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordImageDto;
import com.anonymous.usports.domain.record.dto.RecordRegister;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.entity.RecordImageEntity;
import com.anonymous.usports.domain.record.repository.RecordImageRepository;
import com.anonymous.usports.domain.record.repository.RecordRepository;
import com.anonymous.usports.domain.record.service.RecordService;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.constant.NumberConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MyException;
import com.anonymous.usports.global.exception.RecordException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

  private final MemberRepository memberRepository;
  private final RecordRepository recordRepository;
  private final SportsRepository sportsRepository;
  private final RecordImageRepository recordImageRepository;

  /**
   * 기록 게시글 등록
   */
  @Override
  @Transactional
  public RecordDto saveRecord(RecordRegister.Request request, Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));
    SportsEntity sports = sportsRepository.findById(request.getSportsId())
        .orElseThrow(() -> new MyException(ErrorCode.SPORTS_NOT_FOUND));

    RecordEntity recordEntity = recordRepository.save(
        RecordRegister.Request.toEntity(request, member, sports));

    List<RecordImageEntity> recordImages = saveImages(recordEntity, request.getImages());

    return RecordDto.fromEntity(recordEntity, recordImages);
  }

  /**
   * 기록 게시글 이미지 저장 메서드
   */
  public List<RecordImageEntity> saveImages(RecordEntity recordEntity, List<MultipartFile> images) {
    // 이미지 수 체크
    validateImageCount(images);

    List<RecordImageEntity> recordImages = new ArrayList<>();

    try {
      for (MultipartFile image : images) {
        String uniqueFilename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        //FIXME S3에 파일 저장 로직 구현
        String imageAddress = "파일 저장 경로" + uniqueFilename;

        RecordImageEntity recordImage = RecordImageDto.toEntity(recordEntity, imageAddress);

        RecordImageEntity savedImage = recordImageRepository.save(recordImage);
        recordImages.add(savedImage);
      }
    } catch (Exception e) {
      throw new RecordException(ErrorCode.IMAGE_SAVE_ERROR);
    }
    return recordImages;
  }

  /**
   * 저장할 이미지 수 체크
   */
  public void validateImageCount(List<MultipartFile> images) {
    if (images.size() > NumberConstant.MAX_IMAGE_COUNT) {
      throw new RecordException(ErrorCode.TOO_MANY_IMAGES);
    }
  }


  @Override
  public List<RecordDto> getRecordsList() {
    return null;
  }

  @Override
  public void updateRecord() {

  }

  @Override
  public void deleteRecord() {

  }
}
