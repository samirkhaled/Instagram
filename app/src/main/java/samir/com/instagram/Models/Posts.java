package samir.com.instagram.Models;

public class Posts {

    private  String description;
    private  String image;
    private  String postId;
    private  String publisher;

    public Posts() {
    }

    public Posts(String description, String image, String postId, String publisher) {
        this.description = description;
        this.image = image;
        this.postId = postId;
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
