package com.anonymous.usports.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Getter
public enum SportsGrade {
  ROOKIE(1, "Rookie", "루키"),
  BEGINNER_1(2, "Beginner 1", "비기너 1"),
  BEGINNER_2(3, "Beginner 2", "비기너 2"),
  BEGINNER_3(4, "Beginner 3", "비기너 3"),
  AMATEUR_1(5, "Amateur 1", "아마추어 1"),
  AMATEUR_2(6, "Amateur 2", "아마추어 2"),
  AMATEUR_3(7, "Amateur 3", "아마추어 3"),
  SEMI_PRO_1(8, "Semi-pro 1", "세미프로 1"),
  SEMI_PRO_2(9, "Semi-pro 2", "세미프로 2"),
  PRO(10, "Professional", "프로");

  private final int score;
  private final String en;
  private final String kr;

  public static SportsGrade doubleToGrade(double score){

    if(score >= 0 && score <= 1){
      return ROOKIE;
    }else if(score > 1 && score <= 2){
      return BEGINNER_1;
    }else if(score > 2 && score <= 3){
      return BEGINNER_2;
    }else if(score > 3 && score <= 4){
      return BEGINNER_3;
    }else if(score > 4 && score <= 5){
      return AMATEUR_1;
    }else if(score > 5 && score <= 6){
      return AMATEUR_2;
    }else if(score > 6 && score <= 7){
      return AMATEUR_3;
    }else if(score > 7 && score <= 8){
      return SEMI_PRO_1;
    }else if(score > 8 && score <= 9){
      return SEMI_PRO_2;
    }else if(score > 9 && score <= 10){
      return PRO;
    }

    if(score > 10){
      log.error("SportsGrade over 10!!!");
      return PRO;
    }
    return ROOKIE;
  }
}
