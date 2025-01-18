package com.vishwa.twitter.Entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tweets_table")
public class TweetEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="tweet_id")
    private Long Id;

    @Column(name = "tweet_filepath")
    private String tweetFilePath;

    @Column(name = "tweet_content" ,nullable=false)
    private String tweetContent;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "hashtags")
    private String hashtags;

    @Column(name = "time_stamp", nullable = false)
    private String timeStamp;

    @Transient
    @JsonIgnore
    @OneToMany(mappedBy = "tweetId", orphanRemoval = true)
    private List<CommentEntity> comments;

    @Column(name = "likes_count")
    @Builder.Default
    private Long likesCount = 0L;

    @Builder.Default
    @Transient
    private Boolean isLiked = false;
}
