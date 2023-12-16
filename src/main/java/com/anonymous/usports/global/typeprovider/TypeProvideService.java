package com.anonymous.usports.global.typeprovider;

import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.Region;
import com.anonymous.usports.global.type.SportsGrade;
import com.anonymous.usports.global.typeprovider.dto.TypeList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TypeProvideService {

  private final SportsRepository sportsRepository;

  public TypeList getTypeList(){
    return TypeList.builder()
        .sportsList(this.getSportsList())
        .genderList(this.getGenderList())
        .regionList(this.getRegionList())
        .sportsLevelList(this.getSportsLevelList())
        .build();
  }

  private List<String> getSportsList() {
    return sportsRepository.findAll().stream()
        .map(SportsEntity::getSportsName).
        collect(Collectors.toList());
  }

  private List<String> getGenderList() {
    List<String> list = new ArrayList<>();
    for (Gender gender : Gender.values()) {
      list.add(gender.getDescription());
    }
    return list;
  }

  private List<String> getRegionList() {
    List<String> list = new ArrayList<>();
    for (Region region : Region.values()) {
      list.add(region.getDescription());
    }
    return list;
  }

  private List<String> getSportsLevelList() {
    List<String> list = new ArrayList<>();
    for (SportsGrade sportsGrade : SportsGrade.values()) {
      list.add(sportsGrade.getDescription());
    }
    return list;
  }

}
