package com.anonymous.usports.websocket.service.impl;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.global.constant.ChatConstant;
import com.anonymous.usports.global.exception.ChatException;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.RecruitException;
import com.anonymous.usports.global.type.ParticipantStatus;
import com.anonymous.usports.websocket.dto.ChatMessageDto;
import com.anonymous.usports.websocket.dto.ChatPartakeDto;
import com.anonymous.usports.websocket.dto.httpbody.ChatInviteDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateDMDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateRecruitChat;
import com.anonymous.usports.websocket.entity.ChatPartakeEntity;
import com.anonymous.usports.websocket.entity.ChatRoomEntity;
import com.anonymous.usports.websocket.entity.ChattingEntity;
import com.anonymous.usports.websocket.repository.ChatPartakeRepository;
import com.anonymous.usports.websocket.repository.ChatRoomRepository;
import com.anonymous.usports.websocket.repository.ChattingRepository;
import com.anonymous.usports.websocket.service.ChatRoomService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatPartakeRepository chatPartakeRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final RecruitRepository recruitRepository;
    private final ChattingRepository chattingRepository;


    @Override
    public ChatMessageDto enterChatRoom(Long chatRoomId, MemberDto memberDto) {


        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        MemberEntity member = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (!chatPartakeRepository.existsByChatRoomEntityAndMemberEntity(chatRoom, member)) {
            throw new ChatException(ErrorCode.USER_NOT_IN_THE_CHAT);
        }

      return ChatMessageDto.builder()
          .chatRoomId(chatRoomId)
          .chatRoomName(chatRoom.getChatRoomName())
          .user(member.getAccountName())
          .userId(member.getMemberId())
          .imageAddress(member.getProfileImage())
          .build();
    }

    private List<ChatPartakeDto> findChatRoomListByDto(MemberDto memberDto) {

        MemberEntity member = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new ChatException(ErrorCode.MEMBER_NOT_FOUND));

        List<ChatPartakeEntity> chatPartakeEntityList = chatPartakeRepository.findAllByMemberEntity(member);

        return chatPartakeEntityList.stream().
            map(chatPartakeEntity -> {
                long unreadChatCount
                    = getUnreadChatCount(chatPartakeEntity.getChatRoomEntity().getChatRoomId(), chatPartakeEntity.getLastReadChatId());
                return ChatPartakeDto.fromEntityWithUnreadCount(chatPartakeEntity, unreadChatCount);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<ChatPartakeDto> getChatRoomList(MemberDto memberDto) {
        return findChatRoomListByDto(memberDto);
    }

    // 방 별로 읽지 않은 채팅 수 체크
    public long getUnreadChatCount(Long chatRoomId, String lastReadChatId) {
        ObjectId objectId = (lastReadChatId != null) ? new ObjectId(lastReadChatId) : null;
        return chattingRepository.countAllByChatRoomIdAndIdGreaterThan(chatRoomId, objectId);
    }

    private void chatRoomExist(MemberEntity memberOne, MemberEntity memberTwo){

        List<ChatPartakeEntity> existingChatRooms = chatPartakeRepository
                .findAllByMemberEntityInAndRecruitIdIsNull(Arrays.asList(memberOne, memberTwo));

        Set<ChatRoomEntity> chatRooms = new HashSet<>();

        // todo : throw를 하지 말고, 그 방으로 들어갈 수 있도록 하기
        for (ChatPartakeEntity chatPartake : existingChatRooms) {
            if (!chatRooms.add(chatPartake.getChatRoomEntity())) {
                throw new ChatException(ErrorCode.CHAT_ALREADY_EXIST);
            }
        }
    }

    private void createNewDMWithMember(MemberEntity memberOne, MemberEntity memberTwo){

        StringBuilder chatName = new StringBuilder();
        chatName.append(memberOne.getAccountName()).append(" X ").append(memberTwo.getAccountName());

        ChatRoomEntity newChatRoom = chatRoomRepository.save(ChatRoomEntity.builder()
                .chatRoomName(chatName.toString())
                .userCount(2L)
                .build());

        List<ChatPartakeEntity> chatPartakeList = new ArrayList<>();

        chatPartakeList.add(ChatPartakeEntity.builder()
                .recruitId(null)
                .chatRoomEntity(newChatRoom)
                .memberEntity(memberOne)
                .build());

        chatPartakeList.add(ChatPartakeEntity.builder()
                .recruitId(null)
                .chatRoomEntity(newChatRoom)
                .memberEntity(memberTwo)
                .build());

        chatPartakeRepository.saveAll(chatPartakeList);
    }

    @Override
    @Transactional
    public CreateDMDto.Response createChatRoom(CreateDMDto.Request request, MemberDto memberDto) {

        if (Objects.equals(memberDto.getMemberId(), request.getMemberId()))
            throw new ChatException(ErrorCode.CANNOT_CREATE_CHAT_WITH_SAME_USER);

        MemberEntity memberOne = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        MemberEntity memberTwo = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        //채팅방이 있는지 없는지 확인
        chatRoomExist(memberOne, memberTwo);

        // 없을 때 만든다
        createNewDMWithMember(memberOne, memberTwo);

        return new CreateDMDto.Response(ChatConstant.CHAT_ROOM_CREATED);
    }


    private void isRecruitHost(RecruitEntity recruit, MemberDto memberDto) {
        MemberEntity member = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (!Objects.equals(recruit.getMember().getMemberId(), member.getMemberId()))
            throw new ChatException(ErrorCode.NOT_RECRUIT_HOST);
    }

    private void createNewRecruitChatRoom(Long recruitId, MemberDto memberDto) {

        RecruitEntity recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));

        if (recruit.getChatRoomId() != null) throw new ChatException(ErrorCode.CHAT_ALREADY_EXIST);

        // 운동 모집 작성자인지 확인
        isRecruitHost(recruit, memberDto);

        // 운동 모집에 참여하는 인원 정보가 필요하다
        // 참여 하는 사람 중에, 신청이 받아진 사람만
        List<ParticipantEntity> participants = participantRepository.findAllByRecruitAndStatus(recruit, ParticipantStatus.ACCEPTED);

        // 찾은 인원 정보를
        ChatRoomEntity chatRoom = chatRoomRepository.save(
                ChatRoomEntity.builder()
                        .chatRoomName(recruit.getTitle())
                        .userCount((long) participants.size())
                        .build()
        );


        chatPartakeRepository.saveAll(participants.stream()
                .map(participant -> ChatPartakeEntity.builder()
                        .chatRoomEntity(chatRoom)
                        .memberEntity(participant.getMember())
                        .recruitId(recruitId)
                        .build())
                .collect(Collectors.toList()));

        // 운동 모집에 ChatRoom ID 저장
        recruit.setChatRoomId(chatRoom.getChatRoomId());
        recruitRepository.save(recruit);
    }

    @Override
    @Transactional
    public CreateRecruitChat.Response createRecruitChat(CreateRecruitChat.Request request, MemberDto memberDto) {

        // 채팅방이 없다는 것을 이미 검증 한 후
        createNewRecruitChatRoom(request.getRecruitId(), memberDto);

        return new CreateRecruitChat.Response(ChatConstant.CHAT_ROOM_CREATED);
    }


    private void inviteNewMemberToRecruitChat(Long inviteMemberId, Long recruitId, MemberDto memberDto){

        RecruitEntity recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));

        // 작성자만 초대할 수 있다
        isRecruitHost(recruit, memberDto);

        MemberEntity member = memberRepository.findById(inviteMemberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        ChatRoomEntity chatRoom = chatRoomRepository.findById(recruit.getChatRoomId())
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // 이미 채팅에 들어와 있으면 저장 안 해도 됨
        if (chatPartakeRepository.existsByRecruitIdAndChatRoomEntityAndMemberEntity(
                recruitId, chatRoom, member
        )) {
            throw new ChatException(ErrorCode.CHAT_ALREADY_EXIST);
        }

        // 모집에 등록이 되어 있고, 승락이 되어 있는 상태
        if (!participantRepository.existsByStatusAndMemberAndRecruit(
                ParticipantStatus.ACCEPTED, member, recruit
        )) {
            throw new RecruitException(ErrorCode.CHAT_INVITE_UNAUTHORIZED);
        }

        chatPartakeRepository.save(ChatPartakeEntity.builder()
                        .memberEntity(member)
                        .chatRoomEntity(chatRoom)
                        .recruitId(recruitId)
                .build());

        Long userCount = chatRoom.getUserCount() + 1;
        chatRoom.setUserCount(userCount);

        chatRoomRepository.save(chatRoom);
    }

    @Override
    @Transactional
    public ChatInviteDto.Response inviteMemberToRecruitChat(ChatInviteDto.Request request, MemberDto memberDto) {

        inviteNewMemberToRecruitChat(request.getMemberId(), request.getRecruitId(), memberDto);

        return new ChatInviteDto.Response(ChatConstant.CHAT_INVITE);
    }


    private Long userExitChat(Long chatRoomId, MemberDto memberDto) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        MemberEntity member = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        ChatPartakeEntity chatPartake = chatPartakeRepository.findByChatRoomEntityAndMemberEntity(chatRoom, member)
                .orElseThrow(() -> new ChatException(ErrorCode.USER_NOT_IN_THE_CHAT));

        chatPartakeRepository.delete(chatPartake);

        Long userCount = chatRoom.getUserCount() - 1;

        if (userCount <= 0) {
            chatRoomRepository.delete(chatRoom);
        } else {
            chatRoom.setUserCount(userCount);
            chatRoomRepository.save(chatRoom);
        }

        return userCount;
    }

    @Override
    @Transactional
    public String exitChat(Long chatRoomId, MemberDto memberDto) {

        Long userCount = userExitChat(chatRoomId, memberDto);

        if (userCount <= 0) {
            return ChatConstant.EXIT_AND_DELETE_CHAT;
        }

        return ChatConstant.EXIT_CHAT;
    }

  @Override
  public List<ChatMessageDto> getMessageList(Long chatRoomId, MemberDto memberDto) {
    ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));

    MemberEntity member = memberRepository.findById(memberDto.getMemberId())
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    /** 개인 입장 시간 기록 후 그 이후 채팅만 불러올 경우 필요. ChatPartake에 입장시간이 있으니
     ChatPartakeEntity chatPartake
     = chatPartakeRepository.findByChatRoomEntityAndMemberEntity(chatRoom,member)
     .orElseThrow(()->new ChatException(ErrorCode.USER_NOT_IN_THE_CHAT));
     */

    List<ChattingEntity> chats = chattingRepository.findAllByChatRoomId(chatRoomId);
    return chats.stream()
        .map(chattingEntity -> toChatMessageDto(chattingEntity))
        .collect(Collectors.toList());
  }

  private ChatMessageDto toChatMessageDto(ChattingEntity chattingEntity) {
    MemberEntity senderMember = memberRepository.findById(chattingEntity.getMemberId())
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    return ChatMessageDto.builder()
        .chatRoomId(chattingEntity.getChatRoomId())
        .userId(chattingEntity.getMemberId())
        .user(senderMember.getName())
        .time(chattingEntity.getCreatedAt())
        .imageAddress(senderMember.getProfileImage())
        .content(chattingEntity.getContent())
        .type(chattingEntity.getType())
        .build();
  }
}
