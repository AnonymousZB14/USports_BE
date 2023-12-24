package com.anonymous.usports.domain.record.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.anonymous.usports.domain.comment.entity.CommentEntity;
import com.anonymous.usports.domain.comment.repository.CommentRepository;
import com.anonymous.usports.domain.follow.entity.FollowEntity;
import com.anonymous.usports.domain.follow.repository.FollowRepository;
import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.InterestedSportsRepository;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordListDto;
import com.anonymous.usports.domain.record.dto.RecordRegister.Request;
import com.anonymous.usports.domain.record.dto.RecordUpdate;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.repository.RecordRepository;
import com.anonymous.usports.domain.record.service.RecordService;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.constant.NumberConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.MyException;
import com.anonymous.usports.global.exception.RecordException;
import com.anonymous.usports.global.exception.SportsException;
import com.anonymous.usports.global.type.FollowStatus;
import com.anonymous.usports.global.type.RecordType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class RecordServiceImpl implements RecordService {

  private final MemberRepository memberRepository;
  private final RecordRepository recordRepository;
  private final SportsRepository sportsRepository;
  private final InterestedSportsRepository interestedSportsRepository;
  private final FollowRepository followRepository;
  private final CommentRepository commentRepository;
  private final AmazonS3 amazonS3;
  @Value("${cloud.aws.s3.bucketName}")
  private String bucketName;
  private final RedisTemplate<String, String> redisTemplate;
  public static final String URLS_TO_DELETE = "urlsToDelete";

  /**
   * 기록 게시글 등록
   */
  @Override
  @Transactional
  public RecordDto saveRecord(Request request, Long loginMemberId,
      List<MultipartFile> images) {
    MemberEntity member = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    SportsEntity sports = sportsRepository.findById(request.getSportsId())
        .orElseThrow(() -> new SportsException(ErrorCode.SPORTS_NOT_FOUND));

    List<String> recordImageList = saveImages(images);

    RecordEntity recordEntity = recordRepository.save(
        Request.toEntity(request, member, sports, recordImageList));

    return RecordDto.fromEntity(recordEntity);
  }

  /**
   * List<MultipartFile>을 List<String>으로 변경
   *
   * @param images 저장할 Images
   * @return 저장한 List<String> 이미지 주소 반환
   */
  private List<String> saveImages(List<MultipartFile> images) {
    // 이미지 수 체크
    validateImageCount(images);

    List<String> recordImages = new ArrayList<>();

    try {
      for (MultipartFile image : images) {
        isValidImageExtension(image.getOriginalFilename());
        String storedImagePath = uploadImageToS3(image);
        recordImages.add(storedImagePath);
      }
      return recordImages;
    } catch (RecordException e) {
      if(!recordImages.isEmpty()){
        for (String imageAddress : recordImages) {
          deleteImageFromS3(imageAddress);
        }
      }
      throw new RecordException(e.getErrorCode(),e.getErrorMessage());
    } catch (Exception e) {
      if(!recordImages.isEmpty()){
        for (String imageAddress : recordImages) {
          deleteImageFromS3(imageAddress);
        }
      }
      throw new MyException(ErrorCode.IMAGE_SAVE_ERROR);
    }
  }

  /**
   * 확장자 체크 메서드
   */
  private void isValidImageExtension(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex == -1) {
      throw new RecordException(ErrorCode.INVALID_IMAGE_EXTENSION); // 파일에 확장자가 없는 경우
    }

    String extension = filename.substring(dotIndex + 1).toLowerCase();
    List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");//img 태그에서 사용할 수 있는 것들
    if (!allowedExtensions.contains(extension)) {
      throw new RecordException(ErrorCode.INVALID_IMAGE_EXTENSION); // 허용되지 않는 확장자인 경우 예외 던지기
    }
  }
  /**
   * S3에 이미지 저장
   *
   * @return 객체 URL을 반환
   */
  private String uploadImageToS3(MultipartFile image) throws IOException {
    // 이미지를 S3에 업로드하고 이미지의 URL을 반환
    String originName = image.getOriginalFilename(); // 원본 이미지 이름
    String ext = null;
    if(originName!=null){
      ext = originName.substring(originName.lastIndexOf(".")); // 확장자
    }
    String changedName = changedImageName(originName); // 새로 생성된 이미지 이름
    ObjectMetadata metadata = new ObjectMetadata();// 메타데이터
    metadata.setContentType("image/" + ext);
    InputStream imageInput = image.getInputStream();
    byte[] bytes = IOUtils.toByteArray(imageInput);
    metadata.setContentLength(bytes.length);
    ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(bytes);

    try {
      amazonS3.putObject(new PutObjectRequest(
          //bucketname, key, inputStream, metadata
          bucketName, changedName, byteArrayIs, metadata
      ).withCannedAcl(CannedAccessControlList.PublicRead));

    } catch (Exception e) {
      throw new RecordException(ErrorCode.IMAGE_SAVE_ERROR);
    } finally {
      byteArrayIs.close();
      imageInput.close();
    }
    return amazonS3.getUrl(bucketName, changedName).toString(); // 데이터베이스에 저장할 이미지가 저장된 주소
  }

  /**
   * 이름 중복 방지를 위한 UUID 생성
   */
  private String changedImageName(String originName) {
    String random = UUID.randomUUID().toString();
    return random + originName;
  }

  /**
   * 저장할 이미지 수 체크
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
   */
  @Override
  public RecordListDto getRecordsPage(RecordType recordType, int page, Long loginMemberId) {
    MemberEntity member = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_SIX,
        Sort.by(Direction.DESC, "updatedAt"));

    //RecordType : RECOMMENDATION
    if (recordType == RecordType.RECOMMENDATION) {
      List<InterestedSportsEntity> interestedSportsEntityList =
          interestedSportsRepository.findAllByMemberEntity(member);

      List<SportsEntity> sportsList = interestedSportsEntityList.stream()
          .map(InterestedSportsEntity::getSports)
          .collect(Collectors.toList());

      Page<RecordEntity> recommendationRecords =
          recordRepository.findAllOpenProfileRecordsBySportsIn(sportsList, pageRequest);
      return new RecordListDto(recommendationRecords);
    }

    //Record Type : FOLLOW
    List<FollowEntity> followings = followRepository.findAllByFromMemberAndFollowStatus(member,
        FollowStatus.ACTIVE);

    List<MemberEntity> followingsMembers = followings.stream()
        .map(FollowEntity::getToMember)
        .collect(Collectors.toList());
    Page<RecordEntity> followRecords = recordRepository.findAllByMemberIn(followingsMembers, pageRequest);

    return new RecordListDto(followRecords);
  }

  /**
   * 기록 수정 페이지 불러오기
   */
  @Override
  public RecordDto getRecordUpdatePage(Long recordId, Long loginMemberId) {
    RecordEntity record = recordRepository.findById(recordId)
        .orElseThrow(() -> new RecordException(ErrorCode.RECORD_NOT_FOUND));
    validateAuthority(record, loginMemberId);
    return RecordDto.fromEntity(record);
  }

  /**
   * 기록 수정 메서드
   */
  @Override
  @Transactional
  public RecordDto updateRecord(Long recordId, RecordUpdate.Request request, Long loginMemberId) {
    RecordEntity record = recordRepository.findById(recordId)
        .orElseThrow(() -> new RecordException(ErrorCode.RECORD_NOT_FOUND));
    validateAuthority(record, loginMemberId);
    List<String> removeImageUrls = new ArrayList<>();
    try{
      if (request.getSportsId() != null) {
        SportsEntity sports = sportsRepository.findById(request.getSportsId())
            .orElseThrow(() -> new SportsException(ErrorCode.SPORTS_NOT_FOUND));
        record.setSports(sports);
      }
      if (request.getRecordContent() != null) {
        record.setRecordContent(request.getRecordContent());
      }
      if (!request.getRemoveImageAddress().isEmpty()) {
        removeImageUrls = request.getRemoveImageAddress();
        if (removeImageUrls.size() >= record.getImageAddress().size()) {
          throw new RecordException(ErrorCode.MINIMUM_IMAGE_RESTRICT); // 기존에 올렸던 모든 이미지를 제거하려 할 경우
        }
        for (String imageUrl : removeImageUrls) {
          if (!record.getImageAddress().contains(imageUrl)) {
            throw new RecordException(ErrorCode.INVALID_IMAGE_URL); //만약 지우려한 Url이 DB에 없는 경우 에러 발생
          }
          record.getImageAddress().remove(imageUrl); //record의 imageAdress 리스트에서 해당 url 제거
          redisTemplate.opsForList().rightPush(URLS_TO_DELETE, imageUrl); //제거한 Url을 redis에 insert
        }
      }
      RecordEntity recordEntity = recordRepository.save(record);
      return RecordDto.fromEntity(recordEntity);
    } catch (RecordException e) {
      rollbackUrls(removeImageUrls); //에러 발생 시 redis에 넣은 값 제거
      throw new RecordException(e.getErrorCode(),e.getErrorMessage());
    } catch (Exception e) {
      log.error("{} is occurred", e.getMessage());
      rollbackUrls(removeImageUrls); //에러 발생 시 redis에 넣은 값 제거
      throw new MyException(ErrorCode.RECORD_UPDATE_ERROR);
    }
  }


  /**
   * 기록 게시글 삭제
   */
  @Override
  @Transactional
  public RecordDto deleteRecord(Long recordId, Long loginMemberId) {
    RecordEntity recordEntity = recordRepository.findById(recordId)
        .orElseThrow(() -> new RecordException(ErrorCode.RECORD_NOT_FOUND));

    validateAuthority(recordEntity, loginMemberId);

    List<String> removeImageAddress = new ArrayList<>(
        recordEntity.getImageAddress()); // Record에 있는 Image들을 저장
    try {
      for (String recordImageAddress : removeImageAddress) {
        redisTemplate.opsForList()
            .rightPush(URLS_TO_DELETE, recordImageAddress); //redis에 제거할 imageUrl 넣기
      }
      recordRepository.delete(recordEntity);// 기록 제거
      return RecordDto.fromEntity(recordEntity);
    } catch (RecordException e) {
      rollbackUrls(removeImageAddress); //redis에 넣은 제거할 url들을 제거
      throw new RecordException(e.getErrorCode(),e.getErrorMessage());
    } catch (Exception e){
      log.error("{} is occurred", e.getMessage());
      rollbackUrls(removeImageAddress); //에러 발생 시 redis에 넣은 값 제거
      throw new MyException(ErrorCode.RECORD_DELETE_ERROR);
    }
  }

  /**
   * 기록 상세 페이지 불러오기
   */
  @Override
  public RecordDto getRecordDetail(Long recordId, int page) {
    RecordEntity record = recordRepository.findById(recordId)
        .orElseThrow(() -> new RecordException(ErrorCode.RECORD_NOT_FOUND));
    PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.COMMENT_PAGE_SIZE_DEFAULT);
    Page<CommentEntity> commentList = commentRepository.findAllCommentsByRecordId(recordId,pageRequest);
    return RecordDto.fromEntityInclueComment(record,commentList);
  }

  /**
   * 기록과 로그인 회원 Id 검증 메서드
   */
  private void validateAuthority(RecordEntity recordEntity, Long loginMemberId) {
    memberRepository.findById(loginMemberId)
        .orElseThrow(()->new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    if (!Objects.equals(recordEntity.getMember().getMemberId(), loginMemberId)) {
      throw new RecordException(ErrorCode.NO_AUTHORITY_ERROR); // 기록 작성자 Id와 로그인한 회원 Id 비교
    }
  }

  /**
   * 모아놓은 제거할 ImageAdress를 redis에서 제거함
   *
   * @param removeImageAddress 제거하려고 했던 ImageAdress
   */
  private void rollbackUrls(List<String> removeImageAddress) {
    for (String address : removeImageAddress) {
      redisTemplate.opsForList().remove(URLS_TO_DELETE, 0, address);
    }
  }

  /**
   * 새벽 3시에 모아놓은 Url에 해당하는 S3 객체들을 제거
   */
  @Scheduled(cron = "0 0 3 * * ?")
  public void deleteRedisUrlFromS3() {
    List<String> urlsToDelete = redisTemplate.opsForList().range(URLS_TO_DELETE, 0, -1);
    if (urlsToDelete != null) {
      for (String url : urlsToDelete) {
        deleteImageFromS3(url);
      }
      redisTemplate.delete(URLS_TO_DELETE); // Redis의 값 삭제
    }
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
      return Decoding.substring(1); // '/'제거
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      throw new RecordException(ErrorCode.INVALID_IMAGE_URL);
    }
  }
}
