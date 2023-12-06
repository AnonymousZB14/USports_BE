package com.anonymous.usports.domain.notification.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Emitter를 저장하기위한 Repository
 * 동시성 이슈로 ConcurrentHashMap 사용
 */
@RequiredArgsConstructor
@Repository
public class EmitterRepository {

  private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

  public void save(Long id, SseEmitter emitter){
    emitterMap.put(id, emitter);
  }

  public void deleteById(Long id) {
    emitterMap.remove(id);
  }

  public SseEmitter get(Long id) {
    return emitterMap.get(id);
  }
}
