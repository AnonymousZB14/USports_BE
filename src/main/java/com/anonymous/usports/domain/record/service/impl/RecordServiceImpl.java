package com.anonymous.usports.domain.record.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.anonymous.usports.domain.follow.entity.FollowEntity;
import com.anonymous.usports.domain.follow.repository.FollowRepository;
import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.InterestedSportsRepository;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordImageDto;
import com.anonymous.usports.domain.record.dto.RecordListDto;
import com.anonymous.usports.domain.record.dto.RecordRegister.Request;
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
import com.anonymous.usports.global.type.FollowStatus;
import com.anonymous.usports.global.type.RecordType;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
  private final InterestedSportsRepository interestedSportsRepository;
  private final FollowRepository followRepository;
  private final AmazonS3 amazonS3;
  @Value("${cloud.aws.s3.bucketName}")
  private String bucketName;

  /**
   * 기록 게시글 등록
   *
   * @param request       게시글 등록 Dto
   * @param loginMemberId 로그인한 회원 Id
   * @param images        등록할 images
   * @return
   */
  @Override
  @Transactional
  public RecordDto saveRecord(Request request, Long loginMemberId,
      List<MultipartFile> images) {
    MemberEntity member = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));
    SportsEntity sports = sportsRepository.findById(request.getSportsId())
        .orElseThrow(() -> new MyException(ErrorCode.SPORTS_NOT_FOUND));

    RecordEntity recordEntity = recordRepository.save(
        Request.toEntity(request, member, sports));

    List<RecordImageEntity> recordImageEntities = saveImages(recordEntity, images);

    return RecordDto.fromEntity(recordEntity, recordImageEntities);
  }

  /**
   * RecordImageEntity에 이미지 정보 저장
   *
   * @param recordEntity 연관 Record
   * @param images       저장할 Images
   * @return 저장한 List<RecordImageEntity>
   */
  private List<RecordImageEntity> saveImages(RecordEntity recordEntity,
      List<MultipartFile> images) {
    // 이미지 수 체크
    validateImageCount(images);

    List<RecordImageEntity> recordImages = new ArrayList<>();

    try {
      for (MultipartFile image : images) {
        String storedImagePath = uploadImageToS3(image);

        RecordImageEntity recordImage = RecordImageDto.toEntity(recordEntity, storedImagePath);

        RecordImageEntity savedImage = recordImageRepository.save(recordImage);
        recordImages.add(savedImage);
      }
    } catch (Exception e) {
      throw new RecordException(ErrorCode.IMAGE_SAVE_ERROR);
    }
    return recordImages;
  }

  /**
   * S3에 이미지 저장
   *
   * @param image 저장할 image
   * @return 객체 URL을 반환
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
   * 이름 중복 방지를 위한 UUID 생성
   *
   * @param originName 기존 파일명
   * @return UUID+originName
   */
  private String changedImageName(String originName) {
    String random = UUID.randomUUID().toString();
    return random + originName;
  }

  /**
   * 저장할 이미지 수 체크
   *
   * @param images 저장할 이미지 리스트
   */
  private void validateImageCount(List<MultipartFile> images) {
    if (images.size() > NumberConstant.MAX_IMAGE_COUNT) {
      throw new RecordException(ErrorCode.TOO_MANY_IMAGES);
    }
  }

  /**
   * 기록 리스트 불러오기
   *
   * @param recordType    불러올 리스트 타입 (RECOMMENDATION or FOLLOW)
   * @param page          불러올 페이지 정보
   * @param loginMemberId 로그인한 회원 Id
   * @return RecordListDto 형태로 반환
   */
  @Override
  public RecordListDto getRecordsPage(RecordType recordType, int page, Long loginMemberId) {
    MemberEntity member = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));

    Page<RecordEntity> recordEntityPage;
    PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT,
        Sort.by(Sort.Direction.DESC, "updatedAt"));

    if (recordType == RecordType.RECOMMENDATION) {
      List<InterestedSportsEntity> interestedSportsEntityList = interestedSportsRepository.findAllByMemberEntity(
          member);
      List<SportsEntity> sportsList = interestedSportsEntityList.stream()
          .map(InterestedSportsEntity::getSports)
          .collect(Collectors.toList());
      recordEntityPage = recordRepository.findAllOpenProfileRecordsBySportsIn(sportsList,
          pageRequest);
    } else {
      List<FollowEntity> followings = followRepository.findAllByFromMemberAndFollowStatus(member,
          FollowStatus.ACTIVE);

      List<MemberEntity> followingsMembers = followings.stream()
          .map(FollowEntity::getToMember)
          .collect(Collectors.toList());
      List<RecordEntity> followRecords = recordRepository.findAllByMemberIn(followingsMembers);
      recordEntityPage = new PageImpl<>(followRecords, pageRequest, followRecords.size());
    }

    RecordListDto recordListDto = new RecordListDto(recordEntityPage);
    List<RecordDto> recordDtos = new ArrayList<>();
    for (RecordEntity r : recordEntityPage.getContent()) {
      recordDtos.add(RecordDto.fromEntity(r, recordImageRepository.findAllByRecord(r)));
    }
    recordListDto.setList(recordDtos);
    return recordListDto;
  }

  @Override
  public void updateRecord() {

  }

  /**
   * 기록 게시글 삭제
   *
   * @param recordId      기록 게시글 번호
   * @param loginMemberId 로그인한 회원 ID
   * @return RecordDto 형태로 반환
   */
  @Override
  public RecordDto deleteRecord(Long recordId, Long loginMemberId) {
    RecordEntity recordEntity = recordRepository.findById(recordId)
        .orElseThrow(() -> new RecordException(ErrorCode.RECORD_NOT_FOUND));
    if (recordEntity.getMember().getMemberId() != loginMemberId) {
      throw new RecordException(ErrorCode.NO_AUTHORITY_ERROR);
    }

    List<RecordImageEntity> recordImageEntities = deleteRecordImages(recordEntity);

    recordRepository.delete(recordEntity);

    return RecordDto.fromEntity(recordEntity, recordImageEntities);
  }

  /**
   * 기록 게시글에 연관된 이미지 삭제
   *
   * @param recordEntity 기록 게시글 엔티티
   * @return List<RecordImageEntity> 해당 기록 게시글의 이미지 리스트를 반환
   */
  private List<RecordImageEntity> deleteRecordImages(RecordEntity recordEntity) {
    List<RecordImageEntity> recordImages = recordImageRepository.findAllByRecord(recordEntity);

    for (RecordImageEntity recordImage : recordImages) {
      deleteImageFromS3(recordImage.getImageAddress());
      recordImageRepository.delete(recordImage);
    }
    return recordImages;
  }

  /**
   * S3에서 저장된 객체를 제거하는 메서드
   *
   * @param imageAddress DB에 저장되어 있던 이미지 URL
   */
  private void deleteImageFromS3(String imageAddress) {
    String key = extractKeyFromImageUrl(imageAddress);
    try {
      amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
    } catch (Exception e) {
      throw new RecordException(ErrorCode.IMAGE_DELETE_ERROR);
    }
  }

  /**
   * 이미지 URL에서 key 추출하는 메서드
   *
   * @param imageUrl 삭제해야할 이미지 URL
   * @return 추출된 key 값을 반환
   */
  private String extractKeyFromImageUrl(String imageUrl) {
    try {
      URL url = new URL(imageUrl);
      String Decoding = URLDecoder.decode(url.getPath(), "UTF-8"); // 파일명에 한글이 있을 경우 Decode 필요
      return Decoding.substring(1);
    } catch (MalformedURLException e) {
      throw new RecordException(ErrorCode.INVALID_IMAGE_URL);
    } catch (UnsupportedEncodingException e) {
      throw new RecordException(ErrorCode.INVALID_IMAGE_URL);
    }
  }
}
