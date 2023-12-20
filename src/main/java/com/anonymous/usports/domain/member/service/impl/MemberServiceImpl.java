package com.anonymous.usports.domain.member.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.util.IOUtils;
import com.anonymous.usports.domain.member.dto.MailResponse;
import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.MemberLogin;
import com.anonymous.usports.domain.member.dto.MemberRegister;
import com.anonymous.usports.domain.member.dto.MemberUpdate;
import com.anonymous.usports.domain.member.dto.MemberWithdraw;
import com.anonymous.usports.domain.member.dto.PasswordLostResponse;
import com.anonymous.usports.domain.member.dto.PasswordUpdate;
import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.InterestedSportsRepository;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.member.service.MailService;
import com.anonymous.usports.domain.member.service.MemberService;
import com.anonymous.usports.domain.sports.dto.SportsDto;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.constant.MailConstant;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.constant.TokenConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.MyException;
import com.anonymous.usports.global.exception.RecordException;
import com.anonymous.usports.global.redis.auth.repository.AuthRedisRepository;
import com.anonymous.usports.global.redis.token.repository.TokenRepository;
import com.anonymous.usports.global.type.LoginBy;
import com.anonymous.usports.global.type.Role;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final InterestedSportsRepository interestedSportsRepository;
    private final SportsRepository sportsRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final AuthRedisRepository authRedisRepository;
    private final MailService mailService;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;


    private void checkDuplication(String accountName, String email){
        if (memberRepository.existsByAccountName(accountName)) {
            throw new MemberException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
        }

        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

    }

    private MemberRegister.Response saveMember(MemberRegister.Request request) {

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        memberRepository.save(MemberRegister.Request.toEntity(request, LoginBy.USPORTS));

        mailService.sendEmailAuthMail(request.getEmail());

        return MemberRegister.Response.fromEntity(
                MemberRegister.Request.toEntity(request, LoginBy.USPORTS), MailConstant.AUTH_EMAIL_SEND
        );
    }

    @Override
    public MemberRegister.Response registerMember(MemberRegister.Request request) {

        checkDuplication(request.getAccountName(), request.getEmail());

        return saveMember(request);
    }

    @Override
    public MemberDto loginMember(MemberLogin.Request request) {

        MemberDto memberDto = (MemberDto) loadUserByUsername(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), memberDto.getPassword())) {
            throw new MemberException(ErrorCode.PASSWORD_UNMATCH);
        }

        return memberDto;
    }

    @Override
    public String logoutMember(String accessToken, String email) {

        boolean result = tokenRepository.deleteToken(email);

        if(!result) return TokenConstant.LOGOUT_NOT_SUCCESSFUL;

        tokenRepository.addBlackListAccessToken(accessToken);

        return TokenConstant.LOGOUT_SUCCESSFUL;
    }

    private MemberEntity passwordCheckAndGetMember(MemberDto memberDto, Long memberId, String password) {

        if (!Role.ADMIN.equals(memberDto.getRole()) && !memberId.equals(memberDto.getMemberId())) {
            throw new MemberException(ErrorCode.MEMBER_ID_UNMATCH);
        }

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(password, memberEntity.getPassword()))
            throw new MemberException(ErrorCode.PASSWORD_UNMATCH);

        return memberEntity;
    }

    @Override
    public MemberWithdraw.Response deleteMember(MemberDto memberDto, MemberWithdraw.Request request, Long memberId) {

        memberRepository.delete(passwordCheckAndGetMember(memberDto, memberId, request.getPassword()));

        return new MemberWithdraw.Response(ResponseConstant.MEMBER_DELETE_SUCCESS);
    }


    private MemberEntity checkDuplicationUpdate(MemberDto memberDto, MemberUpdate.Request request){

        MemberEntity memberEntity = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (!memberEntity.getAccountName().equals(request.getAccountName())) {
            if (memberRepository.existsByAccountName(request.getAccountName())) {
                throw new MemberException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
            }
        }

        if (!memberEntity.getEmail().equals(memberDto.getEmail())) {
            if (memberRepository.existsByEmail(memberDto.getEmail())) {
                throw new MemberException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }

        return memberEntity;
    }

  // 관심 운동이 있을 경우, 수정을 했을 상황을 대비해, 이미 저장되어 있는 데이터는 다 삭제하고 다시 저장하기
  private List<InterestedSportsEntity> saveInterestedSportsEntities(List<Long> allSelectedSports,
      MemberEntity memberEntity) {

    interestedSportsRepository.deleteAllByMemberEntity(memberEntity);

    return interestedSportsRepository.saveAll(
        allSelectedSports.stream()
            .map(id ->
                InterestedSportsEntity.builder()
                    .sports(sportsRepository.findById(id)
                        .orElseThrow(() -> new MyException(ErrorCode.SPORTS_NOT_FOUND)))
                    .memberEntity(memberEntity)
                    .build())
            .collect(Collectors.toList()));
  }

  @Override
  @Transactional
  public MemberUpdate.Response updateMember(MemberUpdate.Request request, MemberDto memberDto,
      Long memberId) {

    if (!Role.ADMIN.equals(memberDto.getRole()) && !memberId.equals(memberDto.getMemberId())) {
      throw new MemberException(ErrorCode.MEMBER_ID_UNMATCH);
    }

    // 닉네임, 이메일를 수정 할 때, 겹치지 않게
    // 하는 김에 MemberEntity 가지고 오기
    MemberEntity memberEntity = checkDuplicationUpdate(memberDto, request);

    if (Role.UNAUTH.equals(memberDto.getRole()) && memberDto.getEmailAuthAt() == null) {
      int redisEmailAuthNumber = authRedisRepository.getEmailAuthNumber(memberDto.getEmail());

      if (redisEmailAuthNumber != request.getEmailAuthNumber()) {
        throw new MemberException(ErrorCode.EMAIL_AUTH_NUMBER_UNMATCH);
      }

      memberEntity.setEmailAuthAt(LocalDateTime.now());
    }

    // 관심 운동이 아예 없으면 안 된다
    if (request.getInterestedSportsList().isEmpty()) {
      throw new MemberException(ErrorCode.NEED_AT_LEAST_ONE_SPORTS);
    }

    // 맴버 entity 수정
    memberEntity.updateMember(request);

    // 관심 운동 삭제 후 저장
    List<InterestedSportsEntity> savedInterestedSportsList =
        saveInterestedSportsEntities(request.getInterestedSportsList(), memberEntity);

    MemberUpdate.Response response = MemberUpdate.Response.fromEntity(memberEntity);
    response.setInterestedSportsList(
        savedInterestedSportsList.stream()
            .map(InterestedSportsEntity::getSports)
            .map(SportsDto::new)
            .collect(Collectors.toList())
    );

    return response;
  }

  // 프로필 이미지만 등록 / 변경 / 삭제
  @Override
  @Transactional
  public MemberUpdate.Response updateMemberProfileImage(MultipartFile profileImage, MemberDto memberDto,
      Long memberId) {
    if (memberDto.getRole() != Role.ADMIN && memberDto.getMemberId() != memberId) {
      throw new MemberException(ErrorCode.MEMBER_ID_UNMATCH);
    }
    MemberEntity memberEntity = memberRepository.findById(memberDto.getMemberId())
        .orElseThrow(()->new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    if(!profileImage.isEmpty()) { // 파일 업로드 있을 때 -> 프로필 이미지 등록 및 변경
      String profileImageAddress = saveImage(profileImage); // 프로필 이미지 S3에 저장
      if(memberEntity.getProfileImage()!=null){
        deleteImageFromS3(memberEntity.getProfileImage()); // S3에 저장된 기존 프로필 이미지 제거
      }
      memberEntity.updateMemberProfileImage(profileImageAddress); //DB에 새로운 프로필 이미지 업데이트
    } else { //profileImage에 null 값 들어올 때 -> 프로필 이미지 제거
      if(memberEntity.getProfileImage()!=null){
        deleteImageFromS3(memberEntity.getProfileImage()); // S3에 저장된 기존 프로필 이미지 제거
        memberEntity.updateMemberProfileImage(null);
      }
    }
    MemberUpdate.Response response = MemberUpdate.Response.fromEntity(memberEntity);
    return response;
  }

  private String saveImage(MultipartFile profileImage) {
    isValidImageExtension(profileImage.getOriginalFilename());
    try {
      String storedImagePath = uploadImageToS3(profileImage);
      return storedImagePath;
    } catch (IOException e) {
      throw new MemberException(ErrorCode.IMAGE_SAVE_ERROR);
    }
  }

  // 파일 S3 업로드
  private String uploadImageToS3(MultipartFile profileImage) throws IOException {
    // 이미지를 S3에 업로드하고 이미지의 URL을 반환
    String originName = profileImage.getOriginalFilename(); // 원본 이미지 이름
    String ext = originName.substring(originName.lastIndexOf(".")); // 확장자
    String changedName = changedImageName(originName); // 새로 생성된 이미지 이름
    ObjectMetadata metadata = new ObjectMetadata();// 메타데이터
    metadata.setContentType("image/" + ext);
    InputStream imageInput = profileImage.getInputStream();
    byte[] bytes = IOUtils.toByteArray(imageInput);
    metadata.setContentLength(bytes.length);
    ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(bytes);

    try {
      PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
          //bucketname, key, inputStream, metadata
          bucketName, changedName, byteArrayIs, metadata
      ).withCannedAcl(CannedAccessControlList.PublicRead));

    } catch (Exception e) {
      throw new MemberException(ErrorCode.IMAGE_SAVE_ERROR);
    } finally {
      byteArrayIs.close();
      imageInput.close();
    }
    return amazonS3.getUrl(bucketName, changedName).toString(); // 데이터베이스에 저장할 이미지가 저장된 주소
  }

  // 파일명에 붙일 UUID 생성
  private String changedImageName(String originName) {
    String random = UUID.randomUUID().toString();
    return random + originName;
  }

  // 파일 확장자 체크
  private void isValidImageExtension(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex == -1) {
      throw new MemberException(ErrorCode.INVALID_IMAGE_EXTENSION); // 파일에 확장자가 없는 경우
    }

    String extension = filename.substring(dotIndex + 1).toLowerCase();
    List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");//img 태그에서 사용할 수 있는 것들
    if (!allowedExtensions.contains(extension)) {
      throw new MemberException(ErrorCode.INVALID_IMAGE_EXTENSION); // 허용되지 않는 확장자인 경우 예외 던지기
    }
  }

  // S3에서 객체 제거 메서드
  private void deleteImageFromS3(String imageAddress) {
    String key = extractKeyFromImageUrl(imageAddress);
    try {
      amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
    } catch (Exception e) {
      throw new RecordException(ErrorCode.IMAGE_DELETE_ERROR);
    }
  }

  // 객체에서 Key 추출하는 메서드
  private String extractKeyFromImageUrl(String imageUrl) {
    try {
      URL url = new URL(imageUrl);
      String Decoding = URLDecoder.decode(url.getPath(), "UTF-8"); // 파일명에 한글이 있을 경우 Decode 필요
      return Decoding.substring(1); // '/'제거
    } catch (MalformedURLException e) {
      throw new RecordException(ErrorCode.INVALID_IMAGE_URL);
    } catch (UnsupportedEncodingException e) {
      throw new RecordException(ErrorCode.INVALID_IMAGE_URL);
    }
  }

  @Override
    public PasswordUpdate.Response updatePassword(PasswordUpdate.Request request, Long id, MemberDto memberDto) {

        // 기존 비밀번호와 일치하는지 확인
        MemberEntity memberEntity = passwordCheckAndGetMember(memberDto, id, request.getCurrentPassword());

        if (!request.getNewPassword().equals(request.getNewPasswordCheck())) {
            throw new MemberException(ErrorCode.NEW_PASSWORD_UNMATCH);
        }

        memberEntity.setPassword(passwordEncoder.encode(request.getNewPassword()));

        memberRepository.save(memberEntity);

        return new PasswordUpdate.Response(ResponseConstant.PASSWORD_CHANGE_SUCCESS);
    }

    @Override
    public PasswordLostResponse.Response lostPassword(PasswordLostResponse.Request request) {
        MemberEntity memberEntity = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (!memberEntity.getPhoneNumber().equals(request.getPhoneNumber())) {
            throw new MemberException(ErrorCode.PHONE_NUMBER_UNMATCH);
        }

        if (!memberEntity.getName().equals(request.getName())) {
            throw new MemberException(ErrorCode.NAME_UNMATCH);
        }

        String tempPassword = mailService.sendTempPassword(request.getEmail());


        memberEntity.setPassword(passwordEncoder.encode(tempPassword));

        memberRepository.save(memberEntity);

        return new PasswordLostResponse.Response(request.getEmail() + MailConstant.TEMP_PASSWORD_SUCCESSFULLY_SENT);
    }

    @Override
    public MailResponse resendEmailAuth(MemberDto memberDto, Long memberId) {

        if (!memberId.equals(memberDto.getMemberId()))
            throw new MemberException(ErrorCode.MEMBER_ID_UNMATCH);

        if (!memberRepository.existsById(memberId))
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);

        mailService.sendEmailAuthMail(memberDto.getEmail());

        return new MailResponse(MailConstant.AUTH_EMAIL_SEND);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return MemberDto.fromEntity(memberRepository.findByEmail(username)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND)));
    }
}
