package com.anonymous.usports.domain.recruit.api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {

  private Meta meta;
  private List<Document> documents;

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("AddressResponse(meta=" + this.getMeta()).append("\n");
    for(Document d : this.documents){
      sb.append(d).append("\n");
    }
    return sb.toString();
  }
}
