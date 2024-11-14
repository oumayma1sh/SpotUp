package tn.esprit.spotup.entities;


public class Comment {
    private int id;
    private String texte;
    private String date;
    private int likeCount;
    private int dislikeCount;
    private boolean liked;
    private boolean disliked;


    public Comment(String texte, String date) {
        this.texte = texte;
        this.date = date;
        this.likeCount = 0;
        this.dislikeCount = 0;
    }

    // Constructeur pour la base de données
    public Comment(int id, String texte, String date, int likeCount, int dislikeCount) {
        this.id = id;
        this.texte = texte;
        this.date = date;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
    }

    public int getId() {
        return id;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }
    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isDisliked() {
        return disliked;
    }

    public void setDisliked(boolean disliked) {
        this.disliked = disliked;
    }

    public String getDate() {
        return date;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void incrementDislikeCount() {
        this.dislikeCount++;
    }



    public void decrementLikeCount() {
        if (likeCount > 0) {
            likeCount--;
        }
    }



    public void decrementDislikeCount() {
        if (dislikeCount > 0) {
            dislikeCount--;
        }
    }
}
