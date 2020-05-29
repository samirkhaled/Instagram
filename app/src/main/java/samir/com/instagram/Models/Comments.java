package samir.com.instagram.Models;

public class Comments {

   String Publisher;
   String Comment;

    public Comments(String publisher, String comment) {
        Publisher = publisher;
        Comment = comment;
    }

    public Comments() {
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }
}
