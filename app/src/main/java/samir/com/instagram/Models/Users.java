package samir.com.instagram.Models;

public class Users {

   private String Bio;
    private String ID ;
    private String gender;
    private String imageUri;
    private String userName ;

   private String FullName;

    public Users(String bio, String ID, String gender, String imageUri, String userName, String FullName) {
        Bio = bio;
        this.ID = ID;
        this.gender = gender;
        this.imageUri = imageUri;
        this.userName = userName;

        this.FullName=FullName;
    }

    public Users() {
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }
}
