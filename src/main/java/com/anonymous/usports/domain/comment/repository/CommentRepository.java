package com.anonymous.usports.domain.comment.repository;

import com.anonymous.usports.domain.comment.entity.CommentEntity;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
  List<CommentEntity> findAllByParentId(Long parentId);

  boolean existsByCommentId(Long commentId);

  @Query("SELECT c FROM comment c " +
      "WHERE c.record.recordId = :recordId " +
      "ORDER BY COALESCE(c.parentId, c.commentId), c.registeredAt ASC")
  Page<CommentEntity> findAllCommentsByRecordId(@Param("recordId") Long recordId, Pageable pageable);




}
