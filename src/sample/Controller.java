package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import sample.model.Comment;
import sample.model.Post;
import sample.model.User;

import java.util.ArrayList;
import java.util.List;

public class Controller {


    @FXML
    TextField txtCommentSearch;
    @FXML
    TextField txtCommentId;
    @FXML
    TextField txtContent;
    @FXML
    TextField txtPostId;
    @FXML
    TextField txtUserId;



    private RestTemplate createRestTemplate(MediaType type) {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(type);
        converter.setSupportedMediaTypes(mediaTypes);
        messageConverters.add(converter);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Message");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void onSearchClicked(ActionEvent event) {
        try {
            long commentId = Long.parseLong(txtCommentSearch.getText());

            RestTemplate restTemplate = this.createRestTemplate(MediaType.ALL);

            String fooResourceUrl
                    = "http://localhost:8080/api/comment/" + commentId;
            ResponseEntity<Comment> response = restTemplate.getForEntity(fooResourceUrl, Comment.class);
            System.out.println("Status code: " + response.getStatusCode());
            Comment comment = response.getBody();

            txtCommentId.setText(Long.toString(comment.getId()));
            txtContent.setText(comment.getText());
            txtPostId.setText(Long.toString(comment.getPost().getId()));
            txtUserId.setText(Long.toString(comment.getUser().getId()));

        }catch(Exception e){
            showAlert(Alert.AlertType.ERROR, "Error searching comments, please check input value");
        }
    }

    @FXML
    public void onDelete(ActionEvent event) {
       try{
           long commentId = Long.parseLong(txtCommentId.getText());

           RestTemplate restTemplate = this.createRestTemplate(MediaType.ALL);

           String fooResourceUrl
                   = "http://localhost:8080/api/comment/" + commentId;
           restTemplate.delete(fooResourceUrl);

       }catch(Exception e){
           showAlert(Alert.AlertType.ERROR, "Error deleting comments, please check input value");
       }

    }

    @FXML
    public void onAdd(ActionEvent event) {
     try {
         Comment c = new Comment();

         c.setText(txtContent.getText());
         c.setPost(new Post(Long.parseLong(txtPostId.getText())));
         c.setUser(new User(Long.parseLong(txtUserId.getText())));
         RestTemplate restTemplate = this.createRestTemplate(MediaType.APPLICATION_JSON);

         String fooResourceUrl
                 = "http://localhost:8080/api/comment";
         ResponseEntity<Comment> response = restTemplate.postForEntity(fooResourceUrl,c,Comment.class);
         System.out.println("Status code: " + response.getStatusCode());
     } catch(Exception e ){
         showAlert(Alert.AlertType.ERROR, "Error adding comments, please check input value");
     }

    }

    @FXML
    public void onUpdate(ActionEvent event) {
        try {
            Comment c = new Comment();
            c.setId(Long.parseLong(txtUserId.getText()));
            c.setText(txtContent.getText());
            c.setPost(new Post(Long.parseLong(txtPostId.getText())));
            c.setUser(new User(Long.parseLong(txtUserId.getText())));
            RestTemplate template = this.createRestTemplate(MediaType.APPLICATION_JSON);


            String fooResourceUrl
                    = "http://localhost:8080/api/comment";
            HttpEntity<Comment> requestUpdate = new HttpEntity<>(c);
            template.exchange(fooResourceUrl, HttpMethod.PUT, requestUpdate, Void.class);
        } catch(Exception e ){
            showAlert(Alert.AlertType.ERROR, "Error updating comments, please check input value");
        }
    }



}
