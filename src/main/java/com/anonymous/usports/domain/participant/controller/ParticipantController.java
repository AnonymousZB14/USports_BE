package com.anonymous.usports.domain.participant.controller;

import com.anonymous.usports.domain.participant.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ParticipantController {

  private final ParticipantService participantService;


}
