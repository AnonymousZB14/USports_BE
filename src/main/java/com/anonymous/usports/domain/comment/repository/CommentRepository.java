package com.anonymous.usports.domain.comment.repository;

import com.anonymous.usports.domain.comment.entity.CommentEntity;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
  List<CommentEntity> findAllByParentId(Long parentId);

  boolean existsByCommentId(Long commentId);

  boolean existsByRecord(RecordEntity recordEntity);




}
