package com.anonymous.usports.global.type;

import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.SportsException;
import com.anonymous.usports.global.exception.TypeException;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Error;

@Slf4j
@AllArgsConstructor
@Getter
public enum SportsGrade {
  ROOKIE(1, "루키"),
  BEGINNER_1(2, "비기너 1"),
  BEGINNER_2(3, "비기너 2"),
  BEGINNER_3(4, "비기너 3"),
  AMATEUR_1(5, "아마추어 1"),
  AMATEUR_2(6, "아마추어 2"),
  AMATEUR_3(7, "아마추어 3"),
  SEMI_PRO_1(8, "세미프로 1"),
  SEMI_PRO_2(9, "세미프로 2"),
  PRO(10, "프로");

  private final int score;
  private final String description;

  public static SportsGrade doubleToGrade(double score) {

    if (score >= 0 && score <= 1) {
      return ROOKIE;
    } else if (score > 1 && score <= 2) {
      return BEGINNER_1;
    } else if (score > 2 && score <= 3) {
      return BEGINNER_2;
    } else if (score > 3 && score <= 4) {
      return BEGINNER_3;
    } else if (score > 4 && score <= 5) {
      return AMATEUR_1;
    } else if (score > 5 && score <= 6) {
      return AMATEUR_2;
    } else if (score > 6 && score <= 7) {
      return AMATEUR_3;
    } else if (score > 7 && score <= 8) {
      return SEMI_PRO_1;
    } else if (score > 8 && score <= 9) {
      return SEMI_PRO_2;
    } else if (score > 9 && score <= 10) {
      return PRO;
    }

    if (score > 10) {
      throw new TypeException(ErrorCode.SPORTS_GRADE_INVALID, "score가 10보다 클 수 없습니다.");
    }
    return ROOKIE;
  }

  public static SportsGrade intToGrade(int score) {
    switch (score) {
      case 1:
        return ROOKIE;
      case 2:
        return BEGINNER_1;
      case 3:
        return BEGINNER_2;
      case 4:
        return BEGINNER_3;
      case 5:
        return AMATEUR_1;
      case 6:
        return AMATEUR_2;
      case 7:
        return AMATEUR_3;
      case 8:
        return SEMI_PRO_1;
      case 9:
        return SEMI_PRO_2;
      case 10:
        return PRO;
      default:
        break;
    }

    throw new SportsException(ErrorCode.SPORTS_GRADE_INVALID);
  }

  public static int stringToInt(String grade) {
    return Stream.of(SportsGrade.values())
        .filter(sg -> sg.getDescription().equals(grade))
        .map(sg -> sg.getScore())
        .findFirst()
        .orElseThrow(() -> new TypeException(ErrorCode.TYPE_INVALID_ERROR));
  }

  public static String intToString(int score){
    return Stream.of(SportsGrade.values())
        .filter(sg -> sg.getScore() == score)
        .map(sg -> sg.getDescription())
        .findFirst()
        .orElseThrow(() -> new TypeException(ErrorCode.TYPE_INVALID_ERROR));
  }
}