package samir.com.instagram.Models;

public class Notification {

    String postId;
    String userId;
    String comment;
    boolean isPost;

    public Notification() {
    }

    public Notification(String postId, String userId, String comment, boolean isPost) {
        this.postId = postId;
        this.userId = userId;
        this.comment = comment;
        this.isPost =isPost;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isISPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
