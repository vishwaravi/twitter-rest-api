package com.vishwa.twitter.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFileToMedia(MultipartFile file, String where) {
        byte[] data;
        try{
            data = file.getBytes();
            String fileName = genrateFileName(file);
            Path filePath;
    
            if (where.equals("media"))
                filePath = Paths.get(uploadDir, "/", "media", "/", fileName);
            else
                filePath = Paths.get(uploadDir, "/", "profile", "/", fileName);
    
            Files.write(filePath, data);
            return filePath.toAbsolutePath().toString();
        }
        catch(IOException e){
            e.printStackTrace();
            return "u"; // u for unsupported file format
        }
    }

    public boolean deleteFile(String path){
        if(path == null) return true;
        
        Path filePath = Paths.get(path);
        try{
            Files.deleteIfExists(filePath);
            return true;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return false;         
    }

    private String genrateFileName(MultipartFile file){
        return auth().getName()+System.currentTimeMillis()+file.getOriginalFilename();
    }

    public boolean checkFileType(MultipartFile file){
        if(file.isEmpty()) return false;
        if(file.getContentType().contains("image")) return true;
        else return false;
    }


    public String uploadFile(MultipartFile file,String where){
        if(file == null) return null;
        else if(file.isEmpty()) return null;
        else {
            if(!checkFileType(file)) return "u"; // u for unsupported file format
            else return saveFileToMedia(file,where);
        }
    }

    //Function for get the user name from the Security Context
    private static Authentication auth(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
