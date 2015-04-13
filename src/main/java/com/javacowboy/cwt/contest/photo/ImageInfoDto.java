package com.javacowboy.cwt.contest.photo;

/**
 * Created by IntelliJ IDEA.
 * User: MatthewRYoung
 */
public class ImageInfoDto {

    protected String category;//the category the picture is being submitted to in the contest
    protected String origFilename;//fullsize image if hosted by cwt.  the image src if hosted elsewhere.
    protected String thumbFilename;//if img is hosted by cwt, the image in the thread is a thumbnail.  this should be empty if img hosted elsewhere.
    protected String localFilename;//what we name the file when we download it locally

    //helper methods
    public String getFileExtension(){
        if(hasThumb()){
            return thumbFilename.substring(thumbFilename.lastIndexOf("."));
        }else{
            return origFilename.substring(origFilename.lastIndexOf("."));
        }
    }

    public boolean hasThumb() {
        return thumbFilename == null ? false : true;
    }

    public boolean hasOrig() {
        return origFilename == null ? false : true;
    }

    public String getOrigFileName(){
        //download the thumb
        //return thumbFilename != null ? thumbFilename : origFilename;

        //download the orig
        return origFilename;
    }

    //getters and setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrigFilename() {
        return origFilename;
    }

    public void setOrigFilename(String origFilename) {
        this.origFilename = origFilename;
    }

    public String getThumbFilename() {
        return thumbFilename;
    }

    public void setThumbFilename(String thumbFilename) {
        this.thumbFilename = thumbFilename;
    }

    public String getLocalFilename() {
        return localFilename;
    }

    public void setLocalFilename(String localFilename) {
        this.localFilename = localFilename;
    }
}
