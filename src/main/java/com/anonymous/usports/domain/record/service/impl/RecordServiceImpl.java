package com.anonymous.usports.domain.record.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

  private final MemberRepository memberRepository;
  private final RecordRepository recordRepository;
  private final SportsRepository sportsRepository;
  private final RecordImageRepository recordImageRepository;
  private final AmazonS3 amazonS3;
  @Value("${cloud.aws.s3.bucketName}")
  private String bucketName;

  /**
   * 기록 게시글 등록
   */
  @Override
  @Transactional
  public RecordDto saveRecord(RecordRegister.Request request, Long memberId, List<MultipartFile> images) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));
    SportsEntity sports = sportsRepository.findById(request.getSportsId())
        .orElseThrow(() -> new MyException(ErrorCode.SPORTS_NOT_FOUND));

    //테스트용
//    MemberEntity member = MemberEntity.builder()
//        .memberId(memberId)
//        .accountName("aaa")
//        .name("aaa")
//        .email("aaa@gmail.com")
//        .password("1234")
//        .phoneNumber("01011112222")
//        .birthDate(LocalDate.now())
//        .gender(Gender.MALE)
//        .registeredAt(LocalDateTime.now())
//        .updatedAt(LocalDateTime.now())
//        .emailAuthAt(LocalDateTime.now())
//        .profileOpen(true)
//        .role(Role.USER)
//        .build();
//    memberRepository.save(member);

//    SportsEntity sports = SportsEntity.builder()
//        .sportsId(request.getSportsId())
//        .sportsName("축구")
//        .build();
//    sportsRepository.save(sports);

    RecordEntity recordEntity = recordRepository.save(
        RecordRegister.Request.toEntity(request, member, sports));

    List<RecordImageEntity> recordImages = saveImages(recordEntity, images);

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
        String storedImagePath = uploadImageToS3(image);

        RecordImageEntity recordImage = RecordImageDto.toEntity(recordEntity, storedImagePath );

        RecordImageEntity savedImage = recordImageRepository.save(recordImage);
        recordImages.add(savedImage);
      }
    } catch (Exception e) {
      throw new RecordException(ErrorCode.IMAGE_SAVE_ERROR);
    }
    return recordImages;
  }

  /**
   * S3에 이미지 저장하는 메서드
   */
  private String uploadImageToS3(MultipartFile image) {
    // 이미지를 S3에 업로드하고 이미지의 URL을 반환
    String originName = image.getOriginalFilename(); // 원본 이미지 이름
    String ext = originName.substring(originName.lastIndexOf(".")); // 확장자
    String changedName = changedImageName(originName); // 새로 생성된 이미지 이름
    ObjectMetadata metadata = new ObjectMetadata(); // 메타데이터
    metadata.setContentType("image/" + ext);
    try {
      PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
          //bucketname, key, inputStream, metadata
          bucketName, changedName, image.getInputStream(), metadata
      ).withCannedAcl(CannedAccessControlList.PublicRead));

    } catch (Exception e) {
      throw new RecordException(ErrorCode.IMAGE_SAVE_ERROR);
    }
    return amazonS3.getUrl(bucketName, changedName).toString(); // 데이터베이스에 저장할 이미지가 저장된 주소
  }

  /**
   * 이름 중복 방지를 위해 random 생성
   */
  private String changedImageName(String originName) {
    String random = UUID.randomUUID().toString();
    return random + originName;
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
