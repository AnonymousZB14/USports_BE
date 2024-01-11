package com.anonymous.usports.domain.record.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.anonymous.usports.domain.comment.entity.CommentEntity;
import com.anonymous.usports.domain.comment.repository.CommentRepository;
import com.anonymous.usports.domain.follow.entity.FollowEntity;
import com.anonymous.usports.domain.follow.repository.FollowRepository;
import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.InterestedSportsRepository;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.record.dto.RecordDetail;
import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordListDto;
import com.anonymous.usports.domain.record.dto.RecordRegister.Request;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.repository.RecordRepository;
import com.anonymous.usports.domain.recordlike.repository.RecordLikeRepository;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.constant.NumberConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.RecordException;
import com.anonymous.usports.global.exception.SportsException;
import com.anonymous.usports.global.type.FollowStatus;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.RecordType;
import com.anonymous.usports.global.type.Role;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@Slf4j
class RecordServiceImplTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private RecordRepository recordRepository;
  @Mock
  private SportsRepository sportsRepository;
  @Mock
  private InterestedSportsRepository interestedSportsRepository;
  @Mock
  private FollowRepository followRepository;
  @Mock
  private CommentRepository commentRepository;
  @Mock
  private RecordLikeRepository recordLikeRepository;
  @Mock
  private AmazonS3 amazonS3;
  @Mock
  private RedisTemplate<String, String> redisTemplate;
  @InjectMocks
  private RecordServiceImpl recordService;

  private MemberEntity createMember(Long id) {
    return MemberEntity.builder()
        .memberId(id)
        .accountName("testAccountName" + id)
        .name("testName" + id)
        .email("test@test" + id + ".com")
        .password("password" + id)
        .gender(Gender.MALE)
        .role(Role.USER)
        .profileOpen(true)
        .build();
  }

  private FollowEntity createFollow(Long id, MemberEntity fromMember, MemberEntity toMember) {
    return FollowEntity.builder()
        .followId(id)
        .fromMember(fromMember)
        .toMember(toMember)
        .followDate(LocalDateTime.now())
        .followStatus(FollowStatus.ACTIVE)
        .build();
  }

  private SportsEntity createSports(Long id) {
    return SportsEntity.builder()
        .sportsId(id)
        .sportsName("TestSports" + id)
        .build();
  }

  private InterestedSportsEntity createInterestedSports(Long id, MemberEntity member,
      SportsEntity sports) {
    return InterestedSportsEntity.builder()
        .interestedSportsId(id)
        .memberEntity(member)
        .sports(sports)
        .build();
  }

  private RecordEntity createRecord(Long id, MemberEntity member,
      SportsEntity sports, List<String> imageAddress) {
    return RecordEntity.builder()
        .recordId(id)
        .member(member)
        .sports(sports)
        .recordContent("recordContent" + id)
        .registeredAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .countComment(0L)
        .imageAddress(imageAddress)
        .build();
  }

  private List<MultipartFile> createMockMultipartFiles(Long count) {
    List<MultipartFile> files = new ArrayList<>();
    for (int i = 1; i <= count; i++) {
      String fileName = "file" + i + ".jpg";
      String content = "test data" + i;
      MockMultipartFile file = new MockMultipartFile("file", fileName, "image/jpeg",
          content.getBytes());
      files.add(file);
    }
    return files;
  }

  private CommentEntity createComment(Long id, MemberEntity member, RecordEntity record,
      Long parentId) {
    return CommentEntity.builder()
        .commentId(id)
        .member(member)
        .record(record)
        .registeredAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .parentId(parentId)
        .build();
  }

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(recordService, "bucketName", "test-bucket");
  }

  @Nested
  @DisplayName("Record 등록")
  class saveRecord {

    @Test
    @DisplayName("성공 - Record 등록")
    void success_RegisterRecord() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L);
      List<MultipartFile> files = createMockMultipartFiles(3L);
      Request request = new Request(10L, "recordContent" + 100L);
      RecordEntity record = createRecord(100L, member, sports, Arrays.asList(
          "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file1.jpg",
          "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file2.jpg",
          "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file3.jpg"
      ));

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(sportsRepository.findById(10L))
          .thenReturn(Optional.of(sports));
      when(amazonS3.getUrl(eq("test-bucket"), anyString()))
          .thenAnswer(invocation -> {
            String fileName = invocation.getArgument(1); // 파일명 가져오기
            return new URL("https://test-bucket.s3.ap-northeast-2.amazonaws.com/" + fileName);
          });
      when(recordRepository.save(new RecordEntity())).thenReturn(record);

      RecordDto result = recordService.saveRecord(request, 1L, files);

      verify(recordRepository, times(1)).save(any(RecordEntity.class));

      assertNotNull(result);
    }

    @Test
    @DisplayName("실패 - MemberNotFound")
    void fail_MemberNotFound() {
      MemberEntity member = createMember(1L);
      Request request = new Request(10L, "recordContent" + 100L);
      List<MultipartFile> files = createMockMultipartFiles(3L);
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      MemberException exception =
          catchThrowableOfType(() ->
                  recordService.saveRecord(request, member.getMemberId(), files),
              MemberException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - SportsNotFound")
    void fail_SportsNotFound() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L);
      Request request = new Request(10L, "recordContent" + 100L);
      List<MultipartFile> files = createMockMultipartFiles(3L);
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(sportsRepository.findById(10L))
          .thenReturn(Optional.empty());
      SportsException exception =
          catchThrowableOfType(() ->
                  recordService.saveRecord(request, member.getMemberId(), files),
              SportsException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.SPORTS_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - 이미지 수 초과")
    void fail_TOO_MANY_IMAGES() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L);
      Request request = new Request(10L, "recordContent" + 100L);
      List<MultipartFile> files = createMockMultipartFiles(6L);
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(sportsRepository.findById(10L))
          .thenReturn(Optional.of(sports));
      RecordException exception =
          catchThrowableOfType(() ->
                  recordService.saveRecord(request, member.getMemberId(), files),
              RecordException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.TOO_MANY_IMAGES);
    }

    @Test
    @DisplayName("실패 - 업로드할 수 없는 확장자")
    void fail_Invalid_Image_Extension() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L);
      Request request = new Request(10L, "recordContent" + 100L);
      List<MultipartFile> files = createMockMultipartFiles(3L);
      String fileName = "file" + 4 + ".pdf";
      String content = "test data" + 4;
      MockMultipartFile file = new MockMultipartFile("file", fileName, "application/pdf",
          content.getBytes());
      files.add(file);
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(sportsRepository.findById(10L))
          .thenReturn(Optional.of(sports));
      when(amazonS3.getUrl(eq("test-bucket"), anyString()))
          .thenAnswer(invocation -> {
            String whenFileName = invocation.getArgument(1); // 파일명 가져오기
            return new URL("https://test-bucket.s3.ap-northeast-2.amazonaws.com/" + whenFileName);
          });
      RecordException exception =
          catchThrowableOfType(() ->
                  recordService.saveRecord(request, member.getMemberId(), files),
              RecordException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.INVALID_IMAGE_EXTENSION);
    }
  }

  @Nested
  @DisplayName("Record 리스트 가져오기")
  class getRecordsPage {

    @Test
    @DisplayName("성공 - Recommendation record 리스트")
    void success_RecommendationRecordList() {
      MemberEntity member = createMember(1L);
      MemberEntity otherMember = createMember(2L);
      List<SportsEntity> sportsList = new ArrayList<>();
      List<InterestedSportsEntity> interestedSportsList = new ArrayList<>();
      List<RecordEntity> recordEntityList = new ArrayList<>();
      SportsEntity soccer = createSports(30L);
      createRecord(70000L, otherMember, soccer,
          Arrays.asList("https://test-bucket.s3.ap-northeast-2.amazonaws.com/file1.jpg"));
      for (int i = 0; i < 2; i++) {
        SportsEntity sports = createSports((i + 1) * 10L);
        sportsList.add(sports);

        InterestedSportsEntity interestedSports = createInterestedSports((i + 1) * 100L, member,
            sports);
        interestedSportsList.add(interestedSports);

        RecordEntity record = createRecord((i + 1) * 10000L, otherMember, sports,
            Arrays.asList(
                "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file" + (i + 1) + "_1.jpg",
                "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file" + (i + 1) + "_2.jpg",
                "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file" + (i + 1) + "_3.jpg"
            ));
        recordEntityList.add(record);
      }
      int page = 1;
      PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_SIX,
          Sort.by(Direction.DESC, "registeredAt"));
      int start = (int) pageRequest.getOffset();
      int end = Math.min((start + pageRequest.getPageSize()), recordEntityList.size());
      List<RecordEntity> paginatedRecordEntities = recordEntityList.subList(start,end);
      when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
      when(interestedSportsRepository.findAllByMemberEntity(member)).thenReturn(
          interestedSportsList);
      when(recordRepository.findAllOpenProfileRecordsBySportsIn(sportsList,pageRequest)).thenReturn(
          new PageImpl<>(paginatedRecordEntities,pageRequest,recordEntityList.size()));

      RecordListDto result = recordService.getRecordsPage(RecordType.RECOMMENDATION, 1,
          member.getMemberId());

      verify(memberRepository, times(1)).findById(1L);
      verify(interestedSportsRepository, times(1)).findAllByMemberEntity(member);
      verify(recordRepository, times(1)).findAllOpenProfileRecordsBySportsIn(sportsList,pageRequest);

      assertEquals(result.getCurrentElements(), 2);
    }

    @Test
    @DisplayName("성공 - 팔로우 회원 record 리스트")
    void success_FollowRecordList() {
      MemberEntity member = createMember(1L);
      MemberEntity otherMember = createMember(2L);
      MemberEntity theOtherMember = createMember(3L);
      FollowEntity follow = createFollow(10L, member, otherMember);
      FollowEntity otherFollow = createFollow(11L, member, theOtherMember);
      List<FollowEntity> followEntityList = new ArrayList<>();
      followEntityList.add(follow);
      followEntityList.add(otherFollow);
      RecordEntity record1 = createRecord(100L, otherMember, createSports(20L),
          Collections.emptyList());
      RecordEntity record2 = createRecord(101L, theOtherMember, createSports(30L),
          Collections.emptyList());
      List<RecordEntity> recordEntityList = new ArrayList<>();
      recordEntityList.add(record1);
      recordEntityList.add(record2);
      int page = 1;
      PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_SIX,
          Sort.by(Direction.DESC, "registeredAt"));
      int start = (int) pageRequest.getOffset();
      int end = Math.min((start + pageRequest.getPageSize()), recordEntityList.size());
      List<RecordEntity> paginatedRecordEntities = recordEntityList.subList(start,end);
      when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
      when(followRepository.findAllByFromMemberAndFollowStatus(member, FollowStatus.ACTIVE))
          .thenReturn(followEntityList);
      when(recordRepository.findAllByMemberIn(Arrays.asList(otherMember, theOtherMember),pageRequest))
          .thenReturn(new PageImpl<>(paginatedRecordEntities,pageRequest,recordEntityList.size()));
      RecordListDto result = recordService.getRecordsPage(RecordType.FOLLOW, 1,
          member.getMemberId());

      verify(memberRepository, times(1)).findById(1L);
      verify(followRepository, times(1)).findAllByFromMemberAndFollowStatus(member,
          FollowStatus.ACTIVE);
      verify(recordRepository, times(1)).findAllByMemberIn(
          Arrays.asList(otherMember,theOtherMember),pageRequest);

      assertEquals(result.getCurrentElements(), 2);

    }

    @Test
    @DisplayName("실패 - MemberNotFound")
    void fail_MemberNotFound() {
      MemberEntity member = createMember(1L);

      when(memberRepository.findById(1L)).thenReturn(Optional.empty());

      MemberException exception =
          catchThrowableOfType(() ->
                  recordService.getRecordsPage(RecordType.FOLLOW, 1, member.getMemberId()),
              MemberException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("getRecordUpdatePage")
  class getRecordUpdatePage {

    @Test
    @DisplayName("성공 - 기록 업데이트 페이지")
    void success_GetRecordUpdatePage() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L);
      RecordEntity record = createRecord(100L, member, sports, Collections.emptyList());

      when(recordRepository.findById(100L)).thenReturn(Optional.of(record));
      when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

      RecordDto result = recordService.getRecordUpdatePage(100L, 1L);

      verify(recordRepository, times(1)).findById(100L);
      verify(memberRepository, times(1)).findById(1L);

      assertEquals(result.getRecordId(), 100L);
    }

    @Test
    @DisplayName("실패 - RecordNotFound")
    void fail_RecordNotFound() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L);
      RecordEntity record = createRecord(100L, member, sports, Collections.emptyList());

      when(recordRepository.findById(100L)).thenReturn(Optional.empty());
      RecordException exception = assertThrows(RecordException.class,
          () -> recordService.getRecordUpdatePage(100L, 1L));

      assertEquals(exception.getErrorCode(), ErrorCode.RECORD_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - MemberNotFound")
    void fail_MemberNotFound() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L);
      RecordEntity record = createRecord(100L, member, sports, Collections.emptyList());

      when(recordRepository.findById(100L)).thenReturn(Optional.of(record));
      when(memberRepository.findById(1L)).thenReturn(Optional.empty());
      MemberException exception = assertThrows(MemberException.class,
          () -> recordService.getRecordUpdatePage(100L, 1L));

      assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - NO_AUTHORITY_ERROR")
    void fail_NO_AUTHORITY_ERROR() {
      MemberEntity member = createMember(1L);
      MemberEntity otherMember = createMember(2L);
      SportsEntity sports = createSports(10L);
      RecordEntity record = createRecord(100L, otherMember, sports, Collections.emptyList());

      when(recordRepository.findById(100L)).thenReturn(Optional.of(record));
      when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

      RecordException exception = assertThrows(RecordException.class,
          () -> recordService.getRecordUpdatePage(100L, 1L));

      assertEquals(exception.getErrorCode(), ErrorCode.NO_AUTHORITY_ERROR);
    }
  }

  @Nested
  @DisplayName("기록 상세 보기")
  class getRecordDetail {

    @Test
    @DisplayName("성공 - ")
    void success_GetRecordDetail() {
      MemberEntity member = createMember(1L);
      MemberEntity otherMember = createMember(2L);
      SportsEntity sports = createSports(10L);
      RecordEntity record = createRecord(100L, member, sports, Arrays.asList(
          "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file1.jpg",
          "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file2.jpg",
          "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file3.jpg"
      ));
      List<CommentEntity> comments = new ArrayList<>();
      for (long i = 1; i <= 5; i++) {
        CommentEntity comment = createComment(i, member, record, null);
        comments.add(comment);
      }
      for (long i = 10; i <= 13; i++) {
        CommentEntity comment = createComment(i, otherMember, record, 2L);
        comments.add(comment);
      }

      int page = 1;
      PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.COMMENT_PAGE_SIZE_DEFAULT);
      int start = (int) pageRequest.getOffset();
      int end = Math.min((start + pageRequest.getPageSize()), comments.size());
      List<CommentEntity> paginatedCommentEntities = comments.subList(start, end);
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(commentRepository.findAllCommentsByRecordId(100L, pageRequest))
          .thenReturn(new PageImpl<>(paginatedCommentEntities, pageRequest, comments.size()));
      when(recordLikeRepository.existsByRecordAndMember(record, member))
          .thenReturn(false);
      RecordDetail result = recordService.getRecordDetail(100L, 1, 1L);

      assertEquals(record.getRecordId(), result.getRecordId());
      assertEquals(result.getCommentList().size(), 9);

      verify(recordRepository, times(1)).findById(100L);
      verify(commentRepository, times(1)).findAllCommentsByRecordId(100L, pageRequest);
    }

    @Test
    @DisplayName("실패 - RecordNotFound")
    void fail_RecordNotFound() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L);
      RecordEntity record = createRecord(100L, member, sports, Arrays.asList(
          "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file1.jpg",
          "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file2.jpg",
          "https://test-bucket.s3.ap-northeast-2.amazonaws.com/file3.jpg"
      ));
      int page = 1;
      when(recordRepository.findById(100L)).thenReturn(Optional.empty());
      RecordException exception = assertThrows(RecordException.class,
          () -> recordService.getRecordDetail(100L, 1, 1L));

      assertEquals(exception.getErrorCode(), ErrorCode.RECORD_NOT_FOUND);
    }
  }

}