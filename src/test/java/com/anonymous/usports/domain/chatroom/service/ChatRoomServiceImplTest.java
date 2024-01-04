package com.anonymous.usports.domain.chatroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.constant.ChatConstant;
import com.anonymous.usports.global.exception.ChatException;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.RecruitException;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.ParticipantStatus;
import com.anonymous.usports.global.type.RecruitStatus;
import com.anonymous.usports.global.type.Role;
import com.anonymous.usports.websocket.dto.ChatMessageDto;
import com.anonymous.usports.websocket.dto.ChatPartakeDto;
import com.anonymous.usports.websocket.dto.httpbody.ChatInviteDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateDMDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateRecruitChat;
import com.anonymous.usports.websocket.entity.ChatPartakeEntity;
import com.anonymous.usports.websocket.entity.ChatRoomEntity;
import com.anonymous.usports.websocket.repository.ChatPartakeRepository;
import com.anonymous.usports.websocket.repository.ChatRoomRepository;
import com.anonymous.usports.websocket.repository.ChattingRepository;
import com.anonymous.usports.websocket.service.impl.ChatRoomServiceImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class ChatRoomServiceImplTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatPartakeRepository chatPartakeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private RecruitRepository recruitRepository;

    @Mock
    private ChattingRepository chattingRepository;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    private MemberEntity createMember(Long id) {
        return MemberEntity.builder()
                .memberId(id)
                .accountName("accountName" + id)
                .name("name" + id)
                .email("test@test.com")
                .password("password" + id)
                .phoneNumber("010-1111-2222")
                .birthDate(LocalDate.now())
                .gender(Gender.MALE)
                .role(Role.USER)
                .profileOpen(true)
                .build();
    }

    private RecruitEntity createRecruit(Long id, MemberEntity member, Long chatRoomId) {
        return RecruitEntity.builder()
                .recruitId(id)
                .chatRoomId(chatRoomId)
                .sports(new SportsEntity(1000L, "sportsName"))
                .member(member)
                .title("title" + id)
                .content("content" + id)
                .placeName("placeName" + id)
                .lat("111")
                .lnt("100")
                .cost(10000)
                .gender(Gender.MALE)
                .currentCount(1)
                .recruitCount(10)
                .meetingDate(LocalDateTime.now())
                .recruitStatus(RecruitStatus.RECRUITING)
                .gradeFrom(1)
                .gradeTo(10)
                .build();
    }

    private ParticipantEntity createParticipant(Long id, MemberEntity member, RecruitEntity recruit, ParticipantStatus status) {
        return ParticipantEntity.builder()
                .participantId(id)
                .member(member)
                .recruit(recruit)
                .registeredAt(LocalDateTime.now())
                .status(status)
                .build();
    }

    private ChatRoomEntity createChatRoom(Long id, Long userCount) {
        return ChatRoomEntity.builder()
                .chatRoomId(id)
                .chatRoomName("chatRoomName" + id)
                .userCount(userCount)
                .build();
    }

    private ChatPartakeEntity createChatPartake(Long partakeId, ChatRoomEntity chatRoom, MemberEntity member, Long recruitId) {
        return ChatPartakeEntity.builder()
                .partakeId(partakeId)
                .chatRoomEntity(chatRoom)
                .memberEntity(member)
                .recruitId(recruitId)
                .build();
    }


    @Nested
    @DisplayName("1대1 채팅방")
    class oneOnOneChat{

        @Test
        @DisplayName("1대1 채팅방 생성성공")
        void sucessChatCreate() {
        //given
            MemberEntity loggedInMember = createMember(7L);
            MemberEntity otherMember = createMember(555L);

            ChatRoomEntity chatRoom = createChatRoom(24L,2L);

            List<ChatPartakeEntity> noChatRecord = new ArrayList<>();
            List<ChatPartakeEntity> partakeList = new ArrayList<>();

            partakeList.add(ChatPartakeEntity.builder()
                            .chatRoomEntity(chatRoom)
                            .memberEntity(loggedInMember)
                            .recruitId(null)
                    .build());

            partakeList.add(ChatPartakeEntity.builder()
                    .chatRoomEntity(chatRoom)
                    .memberEntity(otherMember)
                    .recruitId(null)
                    .build());

            List<ChatPartakeEntity> partakeListResult = new ArrayList<>();
            partakeListResult.add(createChatPartake(26L, chatRoom, loggedInMember, null));
            partakeListResult.add(createChatPartake(36L, chatRoom, otherMember, null));

        //when
            when(memberRepository.findById(loggedInMember.getMemberId()))
                    .thenReturn(Optional.of(loggedInMember));
            when(memberRepository.findById(otherMember.getMemberId()))
                    .thenReturn(Optional.of(otherMember));

            when(chatPartakeRepository.findAllByMemberEntityInAndRecruitIdIsNull(
               Arrays.asList(loggedInMember, otherMember)
            )).thenReturn(noChatRecord);

            when(chatRoomRepository.save(ChatRoomEntity.builder()
                            .chatRoomName("chatRoomName" + 24L)
                            .userCount(2L)
                    .build()))
                    .thenReturn(chatRoom);

            when(chatPartakeRepository.saveAll(partakeList))
                    .thenReturn(partakeListResult);

            CreateDMDto.Response response = chatRoomService.createChatRoom(
                    CreateDMDto.Request.builder().memberId(otherMember.getMemberId()).build(),
                    MemberDto.fromEntity(loggedInMember)
            );

        //then
            assertThat(response.getMessage()).isEqualTo(ChatConstant.CHAT_ROOM_CREATED);
        }

        @Test
        @DisplayName("테스트 실패 - 1대1 이미 존재함")
        void failCreateDMChatRoomAlreadyExist() {
            //given
            MemberEntity loggedInMember = createMember(7L);
            MemberEntity otherMember = createMember(555L);

            ChatRoomEntity chatRoom = createChatRoom(24L,2L);

            List<ChatPartakeEntity> partakeList = new ArrayList<>();

            partakeList.add(ChatPartakeEntity.builder()
                .chatRoomEntity(chatRoom)
                .memberEntity(loggedInMember)
                .recruitId(null)
                .build());

            partakeList.add(ChatPartakeEntity.builder()
                .chatRoomEntity(chatRoom)
                .memberEntity(otherMember)
                .recruitId(null)
                .build());

            //when
            when(memberRepository.findById(loggedInMember.getMemberId()))
                .thenReturn(Optional.of(loggedInMember));
            when(memberRepository.findById(otherMember.getMemberId()))
                .thenReturn(Optional.of(otherMember));

            when(chatPartakeRepository.findAllByMemberEntityInAndRecruitIdIsNull(
                Arrays.asList(loggedInMember, otherMember)
            )).thenReturn(partakeList);

            ChatException exception =
                catchThrowableOfType(() ->
                    chatRoomService.createChatRoom(CreateDMDto.Request.builder()
                            .memberId(otherMember.getMemberId()).build(),
                        MemberDto.fromEntity(loggedInMember)), ChatException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHAT_ALREADY_EXIST);
        }

        @Test
        @DisplayName("테스트 실패 - 로그인한 사람 자신을 DM에다 초대")
        void failCreateDMChatMyself() {
            //given
            MemberEntity loggedInMember = createMember(55L);
            MemberEntity otherMember = createMember(55L);

            //when
            ChatException exception =
                catchThrowableOfType(() ->
                    chatRoomService.createChatRoom(CreateDMDto.Request.builder()
                            .memberId(otherMember.getMemberId()).build(),
                        MemberDto.fromEntity(loggedInMember)), ChatException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CANNOT_CREATE_CHAT_WITH_SAME_USER);
        }
    }

    @Nested
    @DisplayName("운동 모집 채팅")
    class recruitChat{

        @Test
        @DisplayName("성공 - 운동 모임 그룹 챗 생성")
        void successRecruitChat() {
        //given
            MemberEntity memberHost = createMember(1L);
            MemberEntity memberGuest = createMember(11L);
            MemberEntity memberGuest2 = createMember(111L);

            RecruitEntity recruit = createRecruit(27L, memberHost, null);

            List<ParticipantEntity> participants = new ArrayList<>();

            participants.add(createParticipant(2L, memberHost, recruit, ParticipantStatus.ACCEPTED));
            participants.add(createParticipant(22L, memberGuest, recruit, ParticipantStatus.ACCEPTED));
            participants.add(createParticipant(222L, memberGuest2, recruit, ParticipantStatus.ACCEPTED));

            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomId(3L)
                .chatRoomName(recruit.getTitle())
                .userCount((long) participants.size())
                .build();

            List<ChatPartakeEntity> chatPartakeList = new ArrayList<>();

            chatPartakeList.add(createChatPartake(5L, chatRoom, memberHost, 27L));
            chatPartakeList.add(createChatPartake(5L, chatRoom, memberGuest, 27L));
            chatPartakeList.add(createChatPartake(5L, chatRoom, memberGuest2, 27L));

            RecruitEntity recruitWithChat = createRecruit(27L, memberHost, chatRoom.getChatRoomId());

        //when
            when(recruitRepository.findById(27L))
                .thenReturn(Optional.ofNullable(recruit));

            when(memberRepository.findById(1L))
                .thenReturn(Optional.ofNullable(memberHost));

            when(participantRepository.findAllByRecruitAndStatus(recruit, ParticipantStatus.ACCEPTED))
                .thenReturn(participants);

            when(chatRoomRepository.save(ChatRoomEntity.builder()
                    .chatRoomName(recruit.getTitle())
                    .userCount((long) participants.size())
                .build()))
                .thenReturn(chatRoom);

            when(chatPartakeRepository.saveAll(participants.stream().map(participant ->
                ChatPartakeEntity.builder()
                    .chatRoomEntity(chatRoom)
                    .memberEntity(participant.getMember())
                    .recruitId(27L)
                    .build()).collect(Collectors.toList())))
                .thenReturn(chatPartakeList);

            when(recruitRepository.save(recruit)).thenReturn(recruitWithChat);

            CreateRecruitChat.Response response = chatRoomService.createRecruitChat(
                new CreateRecruitChat.Request(27L), MemberDto.fromEntity(memberHost)
            );

            //then
            assertThat(response.getMessage()).isEqualTo(ChatConstant.CHAT_ROOM_CREATED);
        }

        @Test
        @DisplayName("실패 - 운동 모집의 작성자가 아님")
        void failRecruitChatNotHost() {
            //given
            MemberEntity memberHost = createMember(1L);
            MemberEntity memberGuest = createMember(11L);
            MemberEntity memberGuest2 = createMember(111L);

            RecruitEntity recruit = createRecruit(27L, memberHost, null);

            List<ParticipantEntity> participants = new ArrayList<>();

            participants.add(createParticipant(2L, memberHost, recruit, ParticipantStatus.ACCEPTED));
            participants.add(createParticipant(22L, memberGuest, recruit, ParticipantStatus.ACCEPTED));
            participants.add(createParticipant(222L, memberGuest2, recruit, ParticipantStatus.ACCEPTED));

            //when
            when(recruitRepository.findById(27L))
                .thenReturn(Optional.ofNullable(recruit));

            when(memberRepository.findById(11L))
                .thenReturn(Optional.ofNullable(memberGuest));

            ChatException exception = catchThrowableOfType(() ->
                chatRoomService.createRecruitChat(
                new CreateRecruitChat.Request(27L), MemberDto.fromEntity(memberGuest)
            ), ChatException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_RECRUIT_HOST);
        }

        @Test
        @DisplayName("실패 - 이미 모임 채팅방이 있음")
        void failRecruitChatAlreadyExist() {
            //given
            MemberEntity memberHost = createMember(1L);

            RecruitEntity recruit = createRecruit(27L, memberHost, 3L);

            //when
            when(recruitRepository.findById(27L))
                .thenReturn(Optional.ofNullable(recruit));

            ChatException exception = catchThrowableOfType(() ->
                chatRoomService.createRecruitChat(
                    new CreateRecruitChat.Request(27L), MemberDto.fromEntity(memberHost)
                ), ChatException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHAT_ALREADY_EXIST);
        }

        @Test
        @DisplayName("실패 - 모임방을 찾을 수 없습니다")
        void failRecruitChatRecruitNotFound() {
            //given
            MemberEntity memberHost = createMember(1L);

            //when
            when(recruitRepository.findById(27L))
                .thenReturn(Optional.empty());

            RecruitException exception = catchThrowableOfType(() ->
                chatRoomService.createRecruitChat(
                    new CreateRecruitChat.Request(27L), MemberDto.fromEntity(memberHost)
                ), RecruitException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_NOT_FOUND);
        }
    }
    @Nested
    @DisplayName("운동 모임 채팅 초대")
    class recruitChatInvite{

        @Test
        @DisplayName("운동 모임 채팅 초대")
        void successRecruitChatInvited() {
            //given
            MemberEntity memberHost = createMember(33L);
            MemberEntity memberGuest = createMember(3L);

            ChatInviteDto.Request request = new ChatInviteDto.Request(1L, 2L, 3L);

            RecruitEntity recruit = createRecruit(2L, memberHost, 1L);

            ChatRoomEntity chatRoom = createChatRoom(1L, 5L);

            ChatPartakeEntity chatPartake = createChatPartake(100L, chatRoom, memberGuest, 2L);

            ChatRoomEntity chatRoomAfterInvite = createChatRoom(1L,6L);

            //when
            when(recruitRepository.findById(2L)).thenReturn(Optional.of(recruit));

            when(memberRepository.findById(33L)).thenReturn(Optional.of(memberHost));

            when(memberRepository.findById(3L)).thenReturn(Optional.of(memberGuest));

            when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));

            when(chatPartakeRepository.existsByRecruitIdAndChatRoomEntityAndMemberEntity(
                2L, chatRoom, memberGuest
            )).thenReturn(false);

            when(participantRepository.existsByStatusAndMemberAndRecruit(
                ParticipantStatus.ACCEPTED, memberGuest, recruit
            )).thenReturn(true);

            when(chatPartakeRepository.save(ChatPartakeEntity.builder()
                    .memberEntity(memberGuest)
                    .chatRoomEntity(chatRoom)
                    .recruitId(2L)
                .build())).thenReturn(chatPartake);

            when(chatRoomRepository.save(chatRoom))
                .thenReturn(chatRoomAfterInvite);


            ChatInviteDto.Response response =
            chatRoomService.inviteMemberToRecruitChat(request,MemberDto.fromEntity(memberHost));

            //then

            assertThat(response.getMessage()).isEqualTo(ChatConstant.CHAT_INVITE);
        }

        @Test
        @DisplayName("실패 운동 모임 채팅 초대 - 모집 등록이 안 됨")
        void failRecruitChatInvitedNotRegistered() {
            //given
            MemberEntity memberHost = createMember(33L);
            MemberEntity memberGuest = createMember(3L);

            ChatInviteDto.Request request = new ChatInviteDto.Request(1L, 2L, 3L);

            RecruitEntity recruit = createRecruit(2L, memberHost, 1L);

            ChatRoomEntity chatRoom = createChatRoom(1L, 5L);

            //when
            when(recruitRepository.findById(2L)).thenReturn(Optional.of(recruit));

            when(memberRepository.findById(33L)).thenReturn(Optional.of(memberHost));

            when(memberRepository.findById(3L)).thenReturn(Optional.of(memberGuest));

            when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));

            when(chatPartakeRepository.existsByRecruitIdAndChatRoomEntityAndMemberEntity(
                2L, chatRoom, memberGuest
            )).thenReturn(false);

            when(participantRepository.existsByStatusAndMemberAndRecruit(
                ParticipantStatus.ACCEPTED, memberGuest, recruit
            )).thenReturn(false);

            RecruitException exception = catchThrowableOfType(() ->
                chatRoomService.inviteMemberToRecruitChat(request,MemberDto.fromEntity(memberHost)),
                RecruitException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHAT_INVITE_UNAUTHORIZED);
        }

        @Test
        @DisplayName("실패 운동 모임 채팅 초대 - 이미 채팅방에 있음")
        void failRecruitChatInvitedChatAlreadyExist() {
            //given
            MemberEntity memberHost = createMember(33L);
            MemberEntity memberGuest = createMember(3L);

            ChatInviteDto.Request request = new ChatInviteDto.Request(1L, 2L, 3L);

            RecruitEntity recruit = createRecruit(2L, memberHost, 1L);

            ChatRoomEntity chatRoom = createChatRoom(1L, 5L);

            //when
            when(recruitRepository.findById(2L)).thenReturn(Optional.of(recruit));

            when(memberRepository.findById(33L)).thenReturn(Optional.of(memberHost));

            when(memberRepository.findById(3L)).thenReturn(Optional.of(memberGuest));

            when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));

            when(chatPartakeRepository.existsByRecruitIdAndChatRoomEntityAndMemberEntity(
                2L, chatRoom, memberGuest
            )).thenReturn(true);

            ChatException exception = catchThrowableOfType(() ->
                    chatRoomService.inviteMemberToRecruitChat(request,MemberDto.fromEntity(memberHost)),
                ChatException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHAT_ALREADY_EXIST);
        }

        @Test
        @DisplayName("실패 운동 모임 채팅 초대 - 채팅방이 없음")
        void failRecruitChatInvitedChatNotExist() {
            //given
            MemberEntity memberHost = createMember(33L);
            MemberEntity memberGuest = createMember(3L);

            ChatInviteDto.Request request = new ChatInviteDto.Request(1L, 2L, 3L);

            RecruitEntity recruit = createRecruit(2L, memberHost, 1L);

            //when
            when(recruitRepository.findById(2L)).thenReturn(Optional.of(recruit));

            when(memberRepository.findById(33L)).thenReturn(Optional.of(memberHost));

            when(memberRepository.findById(3L)).thenReturn(Optional.of(memberGuest));

            when(chatRoomRepository.findById(1L)).thenReturn(Optional.empty());

            ChatException exception = catchThrowableOfType(() ->
                    chatRoomService.inviteMemberToRecruitChat(request,MemberDto.fromEntity(memberHost)),
                ChatException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        @Test
        @DisplayName("실패 운동 모임 채팅 초대 - 초대할 맴버 정보가 없음")
        void failRecruitChatInvitedMemberNotFound() {
            //given
            MemberEntity memberHost = createMember(33L);
            MemberEntity memberGuest = createMember(3L);

            ChatInviteDto.Request request = new ChatInviteDto.Request(1L, 2L, 3L);

            RecruitEntity recruit = createRecruit(2L, memberHost, 1L);

            //when
            when(recruitRepository.findById(2L)).thenReturn(Optional.of(recruit));

            when(memberRepository.findById(33L)).thenReturn(Optional.of(memberHost));

            when(memberRepository.findById(3L)).thenReturn(Optional.empty());

            MemberException exception = catchThrowableOfType(() ->
                    chatRoomService.inviteMemberToRecruitChat(request,MemberDto.fromEntity(memberHost)),
                MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("실패 운동 모임 채팅 초대 - 모임 작성자만 초대할 수 있음")
        void failRecruitChatInvitedNotRecruitHost() {
            //given
            MemberEntity memberHost = createMember(33L);
            MemberEntity memberGuest = createMember(3L);

            ChatInviteDto.Request request = new ChatInviteDto.Request(1L, 2L, 3L);

            RecruitEntity recruit = createRecruit(2L, memberHost, 1L);

            //when
            when(recruitRepository.findById(2L)).thenReturn(Optional.of(recruit));

            when(memberRepository.findById(3L)).thenReturn(Optional.of(memberGuest));

            ChatException exception = catchThrowableOfType(() ->
                    chatRoomService.inviteMemberToRecruitChat(request,MemberDto.fromEntity(memberGuest)),
                ChatException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_RECRUIT_HOST);
        }

        @Test
        @DisplayName("실패 운동 모임 채팅 초대 - 로그인한 사람의 정보를 찾을 수 없음")
        void failRecruitChatInvitedLoggedInMemberNotFound() {
            //given
            MemberEntity memberGuest = createMember(33L);

            ChatInviteDto.Request request = new ChatInviteDto.Request(1L, 2L, 3L);

            RecruitEntity recruit = createRecruit(2L, memberGuest, 1L);

            //when
            when(recruitRepository.findById(2L)).thenReturn(Optional.of(recruit));

            when(memberRepository.findById(33L)).thenReturn(Optional.empty());

            MemberException exception = catchThrowableOfType(() ->
                    chatRoomService.inviteMemberToRecruitChat(request,MemberDto.fromEntity(memberGuest)),
                MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("실패 운동 모임 채팅 초대 - 요청한 운동 모임을 찾을 수 없음")
        void failRecruitChatInvitedRecruitNotFound() {
            //given
            MemberEntity memberHost = createMember(33L);

            ChatInviteDto.Request request = new ChatInviteDto.Request(1L, 2L, 3L);

            RecruitEntity recruit = createRecruit(2L, memberHost, 1L);

            //when
            when(recruitRepository.findById(2L)).thenReturn(Optional.empty());

            RecruitException exception = catchThrowableOfType(() ->
                    chatRoomService.inviteMemberToRecruitChat(request,MemberDto.fromEntity(memberHost)),
                RecruitException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("채팅방 나가기")
    class exitChat{

        @Test
        @DisplayName("채팅방 나가기 성공 - 나가고 채팅방 삭제 (아무도 없을 떄)")
        void successExitChatAndDeleteChat() {
            //given
            MemberEntity member = createMember(1L);

            ChatRoomEntity chatRoom = createChatRoom(11L, 1L);

            ChatPartakeEntity chatPartake = createChatPartake(111L, chatRoom, member, 2L);

            //when
            when(chatRoomRepository.findById(11L)).thenReturn(Optional.of(chatRoom));

            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

            when(chatPartakeRepository.findByChatRoomEntityAndMemberEntity(chatRoom, member))
                .thenReturn(Optional.of(chatPartake));

            String response = chatRoomService.exitChat(11L, MemberDto.fromEntity(member));

            //then
            verify(chatPartakeRepository, times(1)).delete(chatPartake);

            assertThat(response).isEqualTo(ChatConstant.EXIT_AND_DELETE_CHAT);
        }

        @Test
        @DisplayName("채팅방 나가기 성공 - 채팅방 나가기")
        void successExitChat() {
            //given
            MemberEntity member = createMember(1L);

            ChatRoomEntity chatRoom = createChatRoom(11L, 6L);

            ChatPartakeEntity chatPartake = createChatPartake(111L, chatRoom, member, 2L);

            //when
            when(chatRoomRepository.findById(11L)).thenReturn(Optional.of(chatRoom));

            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

            when(chatPartakeRepository.findByChatRoomEntityAndMemberEntity(chatRoom, member))
                .thenReturn(Optional.of(chatPartake));

            String response = chatRoomService.exitChat(11L, MemberDto.fromEntity(member));

            //then
            verify(chatPartakeRepository, times(1)).delete(chatPartake);

            assertThat(response).isEqualTo(ChatConstant.EXIT_CHAT);
        }

        @Test
        @DisplayName("채팅방 나가기 실패 - 유저가 채팅방에 없음")
        void failExitChatUserNotInChat() {
            //given
            MemberEntity member = createMember(1L);

            ChatRoomEntity chatRoom = createChatRoom(11L, 6L);

            //when
            when(chatRoomRepository.findById(11L)).thenReturn(Optional.of(chatRoom));

            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

            when(chatPartakeRepository.findByChatRoomEntityAndMemberEntity(chatRoom, member))
                .thenReturn(Optional.empty());

            ChatException exception =
                catchThrowableOfType(() ->
                chatRoomService.exitChat(11L, MemberDto.fromEntity(member)),
                ChatException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_IN_THE_CHAT);
        }

        @Test
        @DisplayName("채팅방 나가기 실패 - 유저를 찾을 수 없음")
        void failExitChatMemberNotFound() {
            //given
            MemberEntity member = createMember(1L);

            ChatRoomEntity chatRoom = createChatRoom(11L, 6L);

            //when
            when(chatRoomRepository.findById(11L)).thenReturn(Optional.of(chatRoom));

            when(memberRepository.findById(1L)).thenReturn(Optional.empty());

            MemberException exception =
                catchThrowableOfType(() ->
                        chatRoomService.exitChat(11L, MemberDto.fromEntity(member)),
                    MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("채팅방 나가기 실패 - 채팅방을 찾을 수 없음")
        void failExitChatChatNotFound() {
            //given
            MemberEntity member = createMember(1L);


            //when
            when(chatRoomRepository.findById(11L)).thenReturn(Optional.empty());

            ChatException exception =
                catchThrowableOfType(() ->
                        chatRoomService.exitChat(11L, MemberDto.fromEntity(member)),
                    ChatException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("채팅방 들어가기")
    class enterChat{

        @Test
        @DisplayName("채팅방 들어가기 성공")
        void successEnterChat() {
            //given
            ChatRoomEntity chatRoom = createChatRoom(11L,  6L);
            MemberEntity member = createMember(1L);

            //when
            when(chatRoomRepository.findById(11L)).thenReturn(Optional.ofNullable(chatRoom));
            when(memberRepository.findById(1L)).thenReturn(Optional.ofNullable(member));
            when(chatPartakeRepository.existsByChatRoomEntityAndMemberEntity(chatRoom, member))
                .thenReturn(true);

            ChatMessageDto chatEnter = chatRoomService.enterChatRoom(11L, MemberDto.fromEntity(member));

            //then
            assertThat(chatEnter.getChatRoomId()).isEqualTo(chatRoom.getChatRoomId());
            assertThat(chatEnter.getChatRoomName()).isEqualTo(chatRoom.getChatRoomName());
            assertThat(chatEnter.getUser()).isEqualTo(member.getAccountName());
        }

        @Test
        @DisplayName("채팅방 들어가기 실패 - 채팅방이 등록이 안 되어 있는 사람")
        void failEnterChatUserNotInChat() {
            //given
            ChatRoomEntity chatRoom = createChatRoom(11L,  6L);
            MemberEntity member = createMember(1L);

            //when
            when(chatRoomRepository.findById(11L)).thenReturn(Optional.ofNullable(chatRoom));
            when(memberRepository.findById(1L)).thenReturn(Optional.ofNullable(member));
            when(chatPartakeRepository.existsByChatRoomEntityAndMemberEntity(chatRoom, member))
                .thenReturn(false);

            ChatException exception =
                catchThrowableOfType(() ->
                        chatRoomService.enterChatRoom(11L, MemberDto.fromEntity(member)),
                    ChatException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_IN_THE_CHAT);
        }
        @Test
        @DisplayName("채팅방 들어가기 실패 - 로그인된 사람의 정보가 없음")
        void failEnterChatMemberNotFound() {
            //given
            ChatRoomEntity chatRoom = createChatRoom(11L,  6L);
            MemberEntity member = createMember(1L);

            //when
            when(chatRoomRepository.findById(11L)).thenReturn(Optional.ofNullable(chatRoom));
            when(memberRepository.findById(1L)).thenReturn(Optional.empty());

            MemberException exception =
                catchThrowableOfType(() ->
                        chatRoomService.enterChatRoom(11L, MemberDto.fromEntity(member)),
                    MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("채팅방 들어가기 실패 - 요청한 채팅방 정보가 없음")
        void failEnterChatRoomNotFound() {
            //given
            MemberEntity member = createMember(1L);

            //when
            when(chatRoomRepository.findById(11L)).thenReturn(Optional.empty());

            ChatException exception =
                catchThrowableOfType(() ->
                        chatRoomService.enterChatRoom(11L, MemberDto.fromEntity(member)),
                    ChatException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("채팅방 목록 보기")
    class getChatRoomList{

        @Test
        @DisplayName("채팅방 목록 찾기 성공")
        void successGetChatRoomList() {
            //given
            MemberEntity member = createMember(1L);
            List<ChatPartakeEntity> chatPartakeList = new ArrayList<>();

            chatPartakeList.add(createChatPartake(11L,createChatRoom(3L, 2L), member,2L));
            chatPartakeList.add(createChatPartake(111L,createChatRoom(33L, 1L), member,22L));
            chatPartakeList.add(createChatPartake(1111L,createChatRoom(333L, 3L), member,222L));
            chatPartakeList.add(createChatPartake(11111L,createChatRoom(3333L, 11L), member,2222L));

            //when
            when(memberRepository.findById(1L)).thenReturn(Optional.ofNullable(member));
            when(chatPartakeRepository.findAllByMemberEntity(member)).thenReturn(chatPartakeList);
            when(chattingRepository.countAllByChatRoomIdAndIdGreaterThan(3L,null)).thenReturn(0L);
            when(chattingRepository.countAllByChatRoomIdAndIdGreaterThan(33L,null)).thenReturn(0L);
            when(chattingRepository.countAllByChatRoomIdAndIdGreaterThan(333L,null)).thenReturn(0L);
            when(chattingRepository.countAllByChatRoomIdAndIdGreaterThan(3333L,null)).thenReturn(0L);


            List<ChatPartakeDto> chatPartakes = chatRoomService.getChatRoomList(MemberDto.fromEntity(member));

            //then
            assertThat(chatPartakes.size()).isEqualTo(4);
            assertThat(chatPartakes.get(0).getChatRoomId()).isEqualTo(3L);
            assertThat(chatPartakes.get(1).getChatRoomId()).isEqualTo(33L);
            assertThat(chatPartakes.get(2).getChatRoomId()).isEqualTo(333L);
            assertThat(chatPartakes.get(3).getChatRoomId()).isEqualTo(3333L);
        }
    }

}
