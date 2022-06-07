package com.mammb.code.jpa.fluent.test;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class Comment extends BaseEntity {

    private String commentedBy;
    private LocalDateTime commentedOn;
    private String content;

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public LocalDateTime getCommentedOn() {
        return commentedOn;
    }

    public void setCommentedOn(LocalDateTime commentedOn) {
        this.commentedOn = commentedOn;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

