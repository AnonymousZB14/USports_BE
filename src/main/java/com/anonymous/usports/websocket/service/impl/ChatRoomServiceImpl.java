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
import com.anonymous.usports.websocket.dto.ChatPartakeDto;
import com.anonymous.usports.websocket.dto.httpbody.ChatInviteDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateDMDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateRecruitChat;
import com.anonymous.usports.websocket.entity.ChatPartakeEntity;
import com.anonymous.usports.websocket.entity.ChatRoomEntity;
import com.anonymous.usports.websocket.repository.ChatPartakeRepository;
import com.anonymous.usports.websocket.repository.ChatRoomRepository;
import com.anonymous.usports.websocket.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatPartakeRepository chatPartakeRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final RecruitRepository recruitRepository;


    @Override
    public String enterChatRoom(Long chatRoomId, MemberDto memberDto) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        MemberEntity member = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (!chatPartakeRepository.existsByChatRoomEntityAndMemberEntity(chatRoom, member)) {
            throw new ChatException(ErrorCode.USER_NOT_IN_THE_CHAT);
        }

        return ChatConstant.ENTERED_CHAT_ROOM;
    }

    private List<ChatPartakeDto> findChatRoomListByDto(MemberDto memberDto) {

        MemberEntity member = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new ChatException(ErrorCode.MEMBER_NOT_FOUND));

        List<ChatPartakeEntity> chatPartakeEntityList = chatPartakeRepository.findAllByMemberEntity(member);

        return chatPartakeEntityList.stream().map(chatPartake -> ChatPartakeDto.fromEntity(chatPartake))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatPartakeDto> getChatRoomList(MemberDto memberDto) {
        return findChatRoomListByDto(memberDto);
    }

//    private void chatRoomExist(MemberEntity memberOne, MemberEntity memberTwo){
//
//        // 2 유저가 들어가 있는 채팅방이 존재하는 것 (한번 더 생각해 보기)
//        Optional<ChatRoomEntity> chatRoom = chatPartakeRepository
//                .findChatRoomEntityByMember(memberOne, memberTwo);
//
//        // todo : throw를 하지 말고, 그 방으로 들어갈 수 있도록 하기
//        if (chatRoom.isPresent()) throw new ChatException(ErrorCode.CHAT_ALREADY_EXIST);
//    }

    private void createNewDMWithMember(MemberEntity memberOne, MemberEntity memberTwo){

        StringBuilder chatName = new StringBuilder();
        chatName.append(memberOne.getAccountName()).append(" X ").append(memberTwo.getAccountName());

        chatRoomRepository.save(ChatRoomEntity.builder()
                .chatRoomName(chatName.toString())
                .userCount(2L)
                .build());
    }

    @Override
    @Transactional
    public CreateDMDto.Response createChatRoom(CreateDMDto.Request request, MemberDto memberDto) {

        MemberEntity memberOne = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        MemberEntity memberTwo = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 채팅방이 있는지 없는지 확인
        //chatRoomExist(memberOne, memberTwo);

        // 없을 때 만든다
        createNewDMWithMember(memberOne, memberTwo);

        return new CreateDMDto.Response(ChatConstant.CHAT_ROOM_CREATED);
    }


    private void isRecruitHost(RecruitEntity recruit, MemberDto memberDto) {
        MemberEntity member = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (recruit.getMember() != member)
            throw new ChatException(ErrorCode.NOT_RECRUIT_HOST);
    }

    private void createNewRecruitChatRoom(Long recruitId, MemberDto memberDto) {

        RecruitEntity recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));

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

        // recruit id가 이미 존재하면, 채팅방을 따로 만들 필요가 없다
        if (chatPartakeRepository.existsByRecruitId(request.getRecruitId()))
            throw new ChatException(ErrorCode.CHAT_ALREADY_EXIST);

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
}
