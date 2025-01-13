package com.vishwa.twitter.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vishwa.twitter.Dto.TweetDto;
import com.vishwa.twitter.Entities.CommentEntity;
import com.vishwa.twitter.Entities.LikeEntity;
import com.vishwa.twitter.Entities.TweetEntity;
import com.vishwa.twitter.Repositories.CommentRepo;
import com.vishwa.twitter.Repositories.LikeRepo;
import com.vishwa.twitter.Repositories.TweetRepo;
import com.vishwa.twitter.utils.TimeStamp;

@Service
public class TweetService{
    @Autowired
    private TweetRepo tweetRepo;
    @Autowired
    private CommentRepo commentRepo;
    @Autowired
    private LikeRepo likeRepo;
    @Autowired
    private FileService fileService;

    //For post the tweet
    public TweetEntity postTweet(TweetDto tweetDto){
        return saveTweet(tweetDto);
    }

    @Transactional
    TweetEntity saveTweet(TweetDto tweetDto){
        TweetEntity savedTweet = TweetEntity.builder()
            .tweetContent(tweetDto.getTweetContent())
            .tweetFilePath(tweetDto.getFilePath())
            .userId(auth().getName())
            .hashtags(tweetDto.getHashtags())
            .timeStamp(TimeStamp.getTStamp())
            .build();
        return tweetRepo.save(savedTweet);
    }

    //for get the tweet using tweet id
    public Optional<TweetEntity> getTweet(long tweetId){
        Optional<TweetEntity>  tweet = tweetRepo.findById(tweetId);
        try{
            if(tweet.isPresent()){
                Path path = Paths.get(tweet.get().getTweetFilePath());
                String imgBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(path));
                tweet.get().setTweetFilePath(imgBase64);
                return tweet;
            }
            else return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //for fetch all tweets
    public List<TweetEntity> getTweets(){
        try{
            List<TweetEntity> tweets  = tweetRepo.findAll();
            for (TweetEntity i : tweets){
                if (likeRepo.existsByTweetIdAndLikedBy(i.getId(), auth().getName()))
                    i.setIsLiked(true);
                if (i.getTweetFilePath() == null) continue;
                Path tweetImgPath = Paths.get(i.getTweetFilePath());
                String imgBase64 = "";

                if(Files.exists(tweetImgPath))
                    imgBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(tweetImgPath));
                i.setTweetFilePath(imgBase64);
            }
            return tweets;
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    //for get the tweets by the user
    public List<TweetEntity> getMyTweets(){
        return tweetRepo.findByUserId(auth().getName());
    }

    //for delete the tweet
    @Transactional
    public boolean deleteTweet(long tweetId){
        Optional<TweetEntity> tweet = tweetRepo.findById(tweetId);
        if (tweet.isPresent() && tweet.get().getUserId().equals(auth().getName())) {

            fileService.deleteFile(tweet.get().getTweetFilePath());
            likeRepo.deleteByTweetId(tweetId);
            tweetRepo.delete(tweet.get());
            return true;
        }
        else return false;
    }

    //For post the comment
    public CommentEntity postComment(CommentEntity comment, long tweetId){
        String userName = auth().getName();
        if(getTweet(tweetId).isPresent()){
            comment.setTweetId(tweetId);
            comment.setUserId(userName);
            comment.setTimeStamp(TimeStamp.getTStamp());
            return commentRepo.save(comment);
        }
        else return null;
    }

    //For delete the comment
    @Transactional
    public boolean deleteComment(long commentId,long tweetId){
        Optional<CommentEntity> comment = commentRepo.findByIdAndTweetId(commentId, tweetId);
        if(comment.isPresent()&&comment.get().getUserId().equals(auth().getName())){
            commentRepo.delete(comment.get());
            return true;
        }
        else return false;
    }

    //Function for Like
    @Modifying
    @Transactional
    public LikeEntity postLike(long tweetId){
        Optional<TweetEntity> tweet = tweetRepo.findById(tweetId);
        if(tweet.isPresent() && !likeRepo.existsByTweetIdAndLikedBy(tweetId,auth().getName())){
            LikeEntity like = LikeEntity.builder()
                            .likedBy(auth().getName()).tweetId(tweetId).timeStamp(TimeStamp.getTStamp())
                            .build();

            tweet.get().setLikesCount((long)likeRepo.countByTweetId(tweetId)+1);
            tweetRepo.save(tweet.get());
            return likeRepo.save(like);
        }
        else return null;
    }

    //Function for Dislike
    @Modifying
    @Transactional
    public boolean dislikePost(long tweetId){
        if (likeRepo.existsByTweetIdAndLikedBy(tweetId, auth().getName())) {
            likeRepo.deleteByTweetIdAndLikedBy(tweetId, auth().getName());
            return true;
        }
        else return false;
    }

    //Function for get the user name from the Security Context
    static public Authentication auth(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
