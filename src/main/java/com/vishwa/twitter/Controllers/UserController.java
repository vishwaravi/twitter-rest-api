package com.vishwa.twitter.Controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vishwa.twitter.Dto.RegisterDto;
import com.vishwa.twitter.Dto.UserDto;
import com.vishwa.twitter.Entities.FollowingEntity;
import com.vishwa.twitter.Entities.UserEntity;
import com.vishwa.twitter.Services.AuthService;
import com.vishwa.twitter.Services.FileService;
import com.vishwa.twitter.Services.FollowService;
import com.vishwa.twitter.Services.UserService;
import com.vishwa.twitter.utils.JwtRes;
import com.vishwa.twitter.utils.ResObj;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FollowService followService;
    @Autowired
    FileService fileService;
    @Autowired
    AuthService authService;

    @Autowired
    private ResObj resObj;
    @Autowired
    private JwtRes jwtRes;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegisterDto user){
        System.out.println(user.toString());
        jwtRes.setToken(authService.verify(user));
        return new ResponseEntity<>(jwtRes,HttpStatus.OK);
    }   

    @PostMapping("/register")
    ResponseEntity<?> registerUser(@ModelAttribute RegisterDto user){
        String profilePath = fileService.uploadFile(user.getProfile(),"profile");
        String bannerPath = fileService.uploadFile(user.getBanner(),"profile");
        
        if(profilePath == "u"){
            resObj.setStatus("profile : unkown file");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        }
        else if(bannerPath == "u"){
            resObj.setStatus("banner : unkown file");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        }
        else {
            try{
                user.setUserPasswd(passwordEncoder.encode(user.getUserPasswd()));
                user.setProfilePath(profilePath);
                user.setBannerPath(bannerPath);
                UserEntity newUser = userService.saveUserData(user);
                newUser.setUserPasswd("[protected]");
                return new ResponseEntity<>(newUser,HttpStatus.CREATED);
            }
            catch(DataIntegrityViolationException e){
                resObj.setStatus("User Name Already Taken");
                return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
            }
        }
    }

    @GetMapping("/{userid}")
    ResponseEntity<?> getUserDetails(@PathVariable String userid){
        UserEntity user = userService.getUserData(userid);
        if(user!=null){
            if(user.getUserId().equals(auth().getName())){
                user.setUserPasswd("[protected]");
                return new ResponseEntity<>(user,HttpStatus.OK);
            }
            else{
                UserDto userDto = UserDto.builder()
                    .userId(user.getUserId()).userName(user.getUserName())
                    .profileUrl(user.getProfileUrl()).bannerUrl(user.getBannerUrl())
                    .followers(user.getFollowers()).following(user.getFollowing())
                    .build();
                return new ResponseEntity<>(userDto,HttpStatus.OK);
            }
        }
        else{
            resObj.setStatus("User Not Found");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        } 
    }

    @PatchMapping("/{userid}")
    ResponseEntity<?> updateUser(@ModelAttribute RegisterDto user,@PathVariable String userid){
        String profilePath = fileService.uploadFile(user.getProfile(),"profile");
        String bannerPath = fileService.uploadFile(user.getBanner(),"profile");
        
        if(profilePath == "u"){
            resObj.setStatus("profile : unkown file");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        }
        else if(bannerPath == "u"){
            resObj.setStatus("banner : unkown file");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        }
        else {
            if(userid.equals(auth().getName())){
                user.setProfilePath(profilePath);
                user.setBannerPath(bannerPath);
                return new ResponseEntity<>(userService.updateUser(user),HttpStatus.OK);
            }
            else {
                resObj.setStatus("Something Went Wrong");
                return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
            }
        }
        
    }

    @DeleteMapping("/{userid}")
    ResponseEntity<?> deleteUser(@PathVariable String userid,@RequestParam("userPasswd") String passwd) throws IOException{
        if(passwordEncoder.matches(passwd,userService.getUserData(userid).getUserPasswd())){
            if(userService.deleteUser(userid)){
                resObj.setStatus("User Deleted Successfully.");
                return new ResponseEntity<>(resObj,HttpStatus.OK);
            }
            else{
                resObj.setStatus("Something Went Wrong");
                return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
            }
        }
        else{
            resObj.setStatus("Invalid Password");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST); 
        } 
    }

    @PutMapping("/{userId}/follow")
    ResponseEntity<?> followUser(@PathVariable String userId){
        FollowingEntity follow = followService.followUser(userId);
        if(follow != null){
            return new ResponseEntity<>(follow,HttpStatus.OK);
        }
        else{
            resObj.setStatus("Something Went Wrong");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{userId}/unfollow")
    ResponseEntity<?> unfollowUser(@PathVariable String userId){
        boolean status = followService.unfollowUser(userId);
        if (status){
            resObj.setStatus("Unfollowed.");
            return new ResponseEntity<>(resObj,HttpStatus.OK);
        }
        else{
            resObj.setStatus("Something Went Wrong");
            return new ResponseEntity<>(resObj,HttpStatus.BAD_REQUEST);
        }
    }

    static public Authentication auth(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}