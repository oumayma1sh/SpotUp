package tn.esprit.spotup.activities;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import tn.esprit.spotup.entities.Comment;

public interface CommentApi {
    @POST("/api/comments")
    Call<Comment> addComment(@Body Comment comment);

    @GET("/api/comments")
    Call<List<Comment>> getAllComments();

    @PUT("/api/comments")
    Call<Comment> updateComment(@Body Comment comment);

    @DELETE("/api/comments/{id}")
    Call<Void> deleteComment(@Path("id") int id);
}

