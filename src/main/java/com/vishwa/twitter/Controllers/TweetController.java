package com.vishwa.twitter.Controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vishwa.twitter.Config.ResObj;
import com.vishwa.twitter.Dto.TweetDto;
import com.vishwa.twitter.Entities.CommentEntity;
import com.vishwa.twitter.Entities.TweetEntity;
import com.vishwa.twitter.Services.TweetService;

@RestController
@RequestMapping("/home")
public class TweetController {

    @Autowired
    private TweetService tweetService;

    @Autowired
    private ResObj resObj;
   
    @GetMapping
    List<TweetEntity> getTweets(){
        return tweetService.getTweets();
    }

    @GetMapping("/myposts")
    List<TweetEntity> myTweets(){
        return tweetService.getMyTweets();
    }

    @GetMapping("/{tweetId}")
    TweetEntity getTweets(@PathVariable int tweetId){
        return tweetService.getTweet(tweetId).get();
    }

    @SuppressWarnings("null")
    @PostMapping
    ResponseEntity<?> postTweet(@ModelAttribute TweetDto tweet) throws IllegalStateException, IOException{
        String fileType = tweet.getFile().getContentType();
        System.out.println(fileType);
        if(fileType.equals("image/jpeg") || fileType.equals("image/png")){
            return new ResponseEntity<>(tweetService.postTweet(tweet),HttpStatus.OK);
        }
        else{
            resObj.setStatus("Unsupported File Type.");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        }
            
    }

    @DeleteMapping("/{tweetId}")
    ResponseEntity<?> deleteTweet(@PathVariable int tweetId){
        if(tweetService.deleteTweet(tweetId)){
            resObj.setStatus("Tweet Deleted.");
            return new ResponseEntity<>(resObj,HttpStatus.OK);
        }
        else{
            resObj.setStatus("Something went Wrong.");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/{tweetId}/comment")
    ResponseEntity<?> postComment(@ModelAttribute CommentEntity comment,@PathVariable int tweetId){
        if(tweetService.postComment(comment,tweetId)!=null)
        return new ResponseEntity<>(tweetService.postComment(comment,tweetId),HttpStatus.OK);
        else{
            resObj.setStatus("Something went Wrong. Comment not posted");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/{tweetId}/comment")
    ResponseEntity<?> deleteComment(@RequestBody CommentEntity comment,@PathVariable long tweetId){
        if(tweetService.deleteComment(comment.getId(), tweetId)){
            resObj.setStatus("comment deleted.");
            return new ResponseEntity<>(resObj,HttpStatus.OK);
        }
        else{
            resObj.setStatus("Something went Wrong.");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{tweetId}/like")
    ResponseEntity<?> postLike(@PathVariable long tweetId){
        if(tweetService.postLike(tweetId)!=null){
            resObj.setStatus("post Liked");
            return new ResponseEntity<>(resObj,HttpStatus.OK);
        }
        else{
            resObj.setStatus("Something went Wrong.");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        } 
    }
    @PutMapping("/{tweetId}/dislike")
    ResponseEntity<?> dislike(@PathVariable long tweetId){
        if(tweetService.dislikePost(tweetId)){
            resObj.setStatus("post Disliked");
            return new ResponseEntity<>(resObj,HttpStatus.OK);
        }
        else{
            resObj.setStatus("Something went Wrong.");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        } 
    }
}
