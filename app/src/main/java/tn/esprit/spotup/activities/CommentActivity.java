package tn.esprit.spotup.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.esprit.spotup.R;
import tn.esprit.spotup.database.DataBaseHelper;
import tn.esprit.spotup.entities.Comment;

public class CommentActivity extends AppCompatActivity {

    ArrayList<Comment> listeCommentaires = new ArrayList<>();
    ArrayList<Comment> displayedComments = new ArrayList<>();
    CommentAdapter adapter;
    DataBaseHelper databaseHelper;


    private static final String[] BAD_WORDS = {"Merde", "Salaud", "Chier"};
   // private LibreTranslateApi libreTranslateApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
     //   libreTranslateApi = ApiClient.getApi(); // Initialize translation API
        CommentApi commentApi = ApiClientSpring.getRetrofitInstance().create(CommentApi.class);

        // Récupérer les éléments de l'interface
        Button commentButton = findViewById(R.id.comment_button);
        final EditText commentInput = findViewById(R.id.comment_input);
        Button submitCommentButton = findViewById(R.id.submit_comment);
        ListView commentsListView = findViewById(R.id.comments_list_view);
        TextView commentsLabel = findViewById(R.id.comments_label);
        Button viewMoreButton = findViewById(R.id.view_more_button); // Nouveau bouton "Voir plus"

        // Initialiser la base de données
        databaseHelper = new DataBaseHelper(this);
        listeCommentaires = databaseHelper.getAllComments();

        // Trier les commentaires par le nombre de likes
        Collections.sort(listeCommentaires, new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return Integer.compare(o2.getLikeCount(), o1.getLikeCount());
            }
        });

        // Afficher seulement les deux commentaires les plus aimés par défaut
        if (listeCommentaires.size() > 2) {
            displayedComments.addAll(listeCommentaires.subList(0, 2));
        } else {
            displayedComments.addAll(listeCommentaires);
        }

        // Initialiser l'adaptateur personnalisé pour la ListView des commentaires affichés
        adapter = new CommentAdapter(displayedComments);
        commentsListView.setAdapter(adapter);

        commentsLabel.setVisibility(View.VISIBLE);
        commentsListView.setVisibility(View.VISIBLE);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentInput.setVisibility(View.VISIBLE);
                submitCommentButton.setVisibility(View.VISIBLE);
            }
        });

        submitCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentaire = commentInput.getText().toString();

                if (!commentaire.isEmpty()) {
                    // Vérifier si le commentaire contient un mot interdit
                    if (containsBadWords(commentaire)) {
                        Toast.makeText(CommentActivity.this, "Commentaire contient des mots interdits", Toast.LENGTH_SHORT).show();
                    } else {
                        String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                        Comment newComment = new Comment(commentaire, currentDate);

                        // Appeler l'API pour ajouter le commentaire
                        commentApi.addComment(newComment).enqueue(new Callback<Comment>() {
                            @Override
                            public void onResponse(Call<Comment> call, Response<Comment> response) {
                                if (response.isSuccessful()) {
                                    // Ajouter le commentaire à la base de données locale
                                    databaseHelper.addComment(response.body());  // Enregistrer dans SQLite
                                    listeCommentaires.add(response.body());
                                    updateDisplayedComments();
                                    adapter.notifyDataSetChanged();
                                    commentInput.setText("");
                                    commentsLabel.setVisibility(View.VISIBLE);
                                    commentsListView.setVisibility(View.VISIBLE);
                                    Toast.makeText(CommentActivity.this, "Commentaire ajouté avec succès !", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Gérer l'échec de la requête
                                    Toast.makeText(CommentActivity.this, "Erreur lors de l'ajout du commentaire", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Comment> call, Throwable t) {
                                Log.e("API Error", "Erreur réseau", t);
                                Toast.makeText(CommentActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Si le commentaire est vide, afficher un message d'erreur
                    Toast.makeText(CommentActivity.this, "Veuillez écrire un commentaire", Toast.LENGTH_SHORT).show();
                }
            }
        });



        viewMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayedComments.clear();
                displayedComments.addAll(listeCommentaires); // Afficher tous les commentaires
                adapter.notifyDataSetChanged();
                viewMoreButton.setVisibility(View.GONE); // Cacher le bouton "Voir plus"
            }
        });
    }


//    private void translateText(final String text, String sourceLang, String targetLang) {
//        // Appel à l'API de traduction
//        libreTranslateApi.translate(text, sourceLang, targetLang).enqueue(new Callback<TranslationResponse>() {
//            @Override
//            public void onResponse(Call<TranslationResponse> call, Response<TranslationResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String translatedText = response.body().getTranslatedText();
//                    Log.d("Translation", "Traduction réussie: " + translatedText);  // Log pour débogage
//                    // Sauvegarder le texte traduit dans la base de données
//                    String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
//                    Comment newComment = new Comment(translatedText, currentDate);
//                    databaseHelper.addComment(newComment);
//                    listeCommentaires.add(newComment);
//                    updateDisplayedComments();
//                    adapter.notifyDataSetChanged();
//                    Toast.makeText(MainActivity.this, "Commentaire ajouté avec la date et traduit !", Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.e("Translation", "Erreur de traduction");  // Log en cas d'erreur
//                    Toast.makeText(MainActivity.this, "Erreur de traduction", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TranslationResponse> call, Throwable t) {
//                Log.e("Translation", "Échec de la traduction: " + t.getMessage());  // Log de l'erreur
//                Toast.makeText(MainActivity.this, "Échec de la traduction: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    private void updateDisplayedComments() {
        // Trier les commentaires par le nombre de likes
        Collections.sort(listeCommentaires, new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return Integer.compare(o2.getLikeCount(), o1.getLikeCount());
            }
        });

        // Afficher seulement les deux commentaires les plus aimés par défaut
        displayedComments.clear();
        if (listeCommentaires.size() > 2) {
            displayedComments.addAll(listeCommentaires.subList(0, 2));
        } else {
            displayedComments.addAll(listeCommentaires);
        }
    }

    // Vérifier si un commentaire contient des mots interdits
    private boolean containsBadWords(String commentaire) {
        for (String badWord : BAD_WORDS) {
            if (commentaire.toLowerCase().contains(badWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // Adapter modifié pour la gestion avec SQLite
    class CommentAdapter extends ArrayAdapter<Comment> {

        CommentAdapter(ArrayList<Comment> comments) {
            super(CommentActivity.this, 0, comments);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
            }

            final Comment commentaire = getItem(position);

            TextView commentText = convertView.findViewById(R.id.comment_text);
            commentText.setText(commentaire.getTexte());

            TextView commentDate = convertView.findViewById(R.id.comment_date);
            commentDate.setText(commentaire.getDate());

            final TextView likeCountText = convertView.findViewById(R.id.like_count);
            final TextView dislikeCountText = convertView.findViewById(R.id.dislike_count);
            likeCountText.setText(String.valueOf(commentaire.getLikeCount()));
            dislikeCountText.setText(String.valueOf(commentaire.getDislikeCount()));

            ImageButton deleteButton = convertView.findViewById(R.id.delete_comment_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                    builder.setTitle("Confirmer la suppression");
                    builder.setMessage("Êtes-vous sûr de vouloir supprimer ce commentaire ?");

                    builder.setPositiveButton("Oui", (dialog, which) -> {
                        databaseHelper.deleteComment(commentaire.getId());
                        listeCommentaires.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(CommentActivity.this, "Commentaire supprimé", Toast.LENGTH_SHORT).show();
                    });

                    builder.setNegativeButton("Non", (dialog, which) -> dialog.dismiss());
                    builder.show();
                }
            });

            ImageButton editButton = convertView.findViewById(R.id.edit_comment_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditCommentDialog(position);
                }
            });

            final ImageButton likeButton = convertView.findViewById(R.id.like_button);
            final ImageButton dislikeButton = convertView.findViewById(R.id.dislike_button);

            // Mettre à jour les icônes des likes et dislikes
            updateLikeDislikeIcons(likeButton, dislikeButton, commentaire);

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commentaire.isLiked()) {
                        commentaire.decrementLikeCount();
                        commentaire.setLiked(false);
                    } else {
                        commentaire.incrementLikeCount();
                        commentaire.setLiked(true);

                        if (commentaire.isDisliked()) {
                            commentaire.decrementDislikeCount();
                            commentaire.setDisliked(false);
                        }
                    }
                    databaseHelper.updateComment(commentaire);
                    likeCountText.setText(String.valueOf(commentaire.getLikeCount()));
                    dislikeCountText.setText(String.valueOf(commentaire.getDislikeCount()));
                    updateLikeDislikeIcons(likeButton, dislikeButton, commentaire);
                }
            });

            dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commentaire.isDisliked()) {
                        commentaire.decrementDislikeCount();
                        commentaire.setDisliked(false);
                    } else {
                        commentaire.incrementDislikeCount();
                        commentaire.setDisliked(true);

                        if (commentaire.isLiked()) {
                            commentaire.decrementLikeCount();
                            commentaire.setLiked(false);
                        }
                    }
                    databaseHelper.updateComment(commentaire);
                    likeCountText.setText(String.valueOf(commentaire.getLikeCount()));
                    dislikeCountText.setText(String.valueOf(commentaire.getDislikeCount()));
                    updateLikeDislikeIcons(likeButton, dislikeButton, commentaire);
                }
            });

            return convertView;
        }

        private void updateLikeDislikeIcons(ImageButton likeButton, ImageButton dislikeButton, Comment comment) {
            if (comment.isLiked()) {
                likeButton.setColorFilter(getContext().getResources().getColor(R.color.liked_color));
            } else {
                likeButton.setColorFilter(getContext().getResources().getColor(R.color.default_icon_color));
            }

            if (comment.isDisliked()) {
                dislikeButton.setColorFilter(getContext().getResources().getColor(R.color.disliked_color));
            } else {
                dislikeButton.setColorFilter(getContext().getResources().getColor(R.color.default_icon_color));
            }
        }

        private void showEditCommentDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
            builder.setTitle("Modifier le commentaire");

            final EditText input = new EditText(CommentActivity.this);
            input.setText(listeCommentaires.get(position).getTexte());
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String nouveauCommentaire = input.getText().toString();
                if (!nouveauCommentaire.isEmpty()) {
                    // Vérifier si le commentaire contient un mot interdit
                    if (containsBadWords(nouveauCommentaire)) {
                        Toast.makeText(CommentActivity.this, "Le commentaire contient des mots interdits", Toast.LENGTH_SHORT).show();
                    } else {
                        // Mettre à jour le commentaire
                        Comment comment = listeCommentaires.get(position);
                        comment.setTexte(nouveauCommentaire);
                        databaseHelper.updateComment(comment);
                        notifyDataSetChanged();
                        Toast.makeText(CommentActivity.this, "Commentaire modifié", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CommentActivity.this, "Le commentaire ne peut pas être vide", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
            builder.show();
        }
    }
}
