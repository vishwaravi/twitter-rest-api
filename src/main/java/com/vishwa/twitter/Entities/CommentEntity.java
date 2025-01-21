package com.vishwa.twitter.Entities;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments_table")
public class CommentEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "comment_content" , nullable = false)
    private String commentContent;

    @Column(name = "tweet_id",nullable = false)
    private Long tweetId;

    @Column(name = "user_id",nullable = false)
    private String userId;

    @Column(name = "time_stamp",nullable = false)
    @CreationTimestamp
    private String timeStamp;

    @Transient
    private String userProfile;
}
