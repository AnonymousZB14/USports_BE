package com.anonymous.usports.domain.chatroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
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
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.RecruitStatus;
import com.anonymous.usports.global.type.Role;
import com.anonymous.usports.websocket.dto.httpbody.CreateDMDto;
import com.anonymous.usports.websocket.entity.ChatPartakeEntity;
import com.anonymous.usports.websocket.entity.ChatRoomEntity;
import com.anonymous.usports.websocket.repository.ChatPartakeRepository;
import com.anonymous.usports.websocket.repository.ChatRoomRepository;
import com.anonymous.usports.websocket.service.impl.ChatRoomServiceImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    private RecruitEntity createRecruit(Long id, MemberEntity member) {
        return RecruitEntity.builder()
                .recruitId(id)
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

    private ParticipantEntity createParticipant(Long id, MemberEntity member, RecruitEntity recruit) {
        return ParticipantEntity.builder()
                .participantId(id)
                .member(member)
                .recruit(recruit)
                .registeredAt(LocalDateTime.now())
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

    }
    @Nested
    @DisplayName("운동 모집 채팅 초대")
    class recruitChatInvite{

    }

    @Nested
    @DisplayName("채팅방 나가기")
    class exitChat{

    }

    @Nested
    @DisplayName("채팅방 들어가기")
    class enterChat{

    }

    @Nested
    @DisplayName("채팅방 목록 보기")
    class getChatRoomList{

    }

}
