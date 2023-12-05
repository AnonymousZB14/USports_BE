package com.anonymous.usports.domain.evaluation.dto;


import com.anonymous.usports.domain.evaluation.entity.EvaluationEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.dto.RecruitRegister;
import com.anonymous.usports.domain.recruit.dto.RecruitRegister.Request;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.RecruitStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

public class EvaluationRegister {
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request {

    private Long recruitId; //모집글 Id
    private Long toMemberId; //피평가자 Id

    private int kindness;
    private int passion;
    private int teamwork;

    private int sportsScore;

    public static EvaluationEntity toEntity(
        EvaluationRegister.Request request,
        RecruitEntity recruit, MemberEntity fromMember,
        MemberEntity toMember){

      return EvaluationEntity.builder()
          .recruit(recruit)
          .fromMember(fromMember)
          .toMember(toMember)
          .kindness(request.getKindness())
          .passion(request.getPassion())
          .teamwork(request.getTeamwork())
          .sportsScore(request.getSportsScore())
          .build();
    }

  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {
    private Long evaluationId;
    private Long fromMemberId;
    private Long toMemberId;
    private String message;

    public static Response fromEntity(EvaluationEntity evaluation){
      return Response.builder()
          .evaluationId(evaluation.getEvaluationId())
          .fromMemberId(evaluation.getFromMember().getMemberId())
          .toMemberId(evaluation.getToMember().getMemberId())
          .message(ResponseConstant.EVALUATION_SUCCEED)
          .build();
    }
  }
}
