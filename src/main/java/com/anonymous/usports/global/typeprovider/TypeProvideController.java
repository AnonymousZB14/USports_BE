package com.anonymous.usports.global.typeprovider;

import com.anonymous.usports.global.typeprovider.dto.TypeList;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("Type 제공 API")
@RequiredArgsConstructor
@RestController
public class TypeProvideController {

  private final TypeProvideService typeProvideService;


  @GetMapping("/api/types")
  public ResponseEntity<TypeList> getTypes(){
    return ResponseEntity.ok(typeProvideService.getTypeList());
  }

}
