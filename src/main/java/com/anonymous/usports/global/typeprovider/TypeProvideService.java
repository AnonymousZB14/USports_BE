package com.anonymous.usports.global.typeprovider;

import com.anonymous.usports.domain.sports.dto.SportsDto;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.Region;
import com.anonymous.usports.global.type.SportsGrade;
import com.anonymous.usports.global.typeprovider.dto.SportsLevelDto;
import com.anonymous.usports.global.typeprovider.dto.TypeList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TypeProvideService {

  private final SportsRepository sportsRepository;

  public TypeList getTypeList() {
    return TypeList.builder()
        .genderList(this.getGenderList())
        .regionList(this.getRegionList())
        .sportsList(this.getSportsList())
        .sportsLevelList(this.getSportsLevelList())
        .build();
  }

  private List<SportsDto> getSportsList() {
    return sportsRepository.findAll().stream()
        .map(SportsDto::new).
        collect(Collectors.toList());
  }

  private List<String> getGenderList() {
    return Stream.of(Gender.values())
        .map(Gender::getDescription)
        .collect(Collectors.toList());
  }

  private List<String> getRegionList() {
    return Stream.of(Region.values())
        .map(Region::getDescription)
        .collect(Collectors.toList());
  }

  private List<SportsLevelDto> getSportsLevelList() {
    return Stream.of(SportsGrade.values())
        .map(SportsLevelDto::new)
        .collect(Collectors.toList());
  }

}
