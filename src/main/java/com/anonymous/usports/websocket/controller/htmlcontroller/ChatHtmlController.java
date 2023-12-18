package com.anonymous.usports.websocket.controller.htmlcontroller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.websocket.service.ChatRoomService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Api(tags = "채팅 HTML controller ")
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/chat")
@Slf4j
public class ChatHtmlController {

    private final ChatRoomService chatRoomService;
    private final MemberRepository memberRepository;

    // todo : 나중에는 로그인한 유저의 데이터를 가지고 와야 한다
    @GetMapping("/rooms")
    public ModelAndView getChatRoomList(){

        log.info("All Chat Rooms");

        MemberDto memberDto = MemberDto.fromEntity(memberRepository.findById(5L)
                .orElseThrow(() -> new RuntimeException("no member")));

        ModelAndView mv = new ModelAndView("chat/rooms");

        mv.addObject("list", chatRoomService.getChatRoomList(memberDto));

        return mv;
    }

    //채팅방 조회
    @GetMapping("/room")
    public void getRoom(Long roomId, Model model){

        log.info("# get Chat Room, roomID : " + roomId);
        log.info("{}", roomId.getClass());

        Long[] memberNum = new Long[]{4L, 5L, 6L, 7L, 8L, 9L};
        log.info("{}", memberNum);

        MemberDto memberDto = MemberDto.fromEntity(memberRepository.findById(memberNum[(int) Math.round(Math.random() * (memberNum.length - 1))])
                .orElseThrow(() -> new RuntimeException("no member")));

        model.addAttribute("room", chatRoomService.enterChatRoom(roomId, memberDto));
    }
}
