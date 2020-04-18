package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML
    TextField txtCommentSearch;
    @FXML
    TextField txtCommentId;
    @FXML
    TextField txtContent;
    @FXML
    ComboBox<Post> cbPostId;
    @FXML
    ComboBox<User> cbUserId;



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
    private void clearForm(){
        cbPostId.setValue(null);
        cbUserId.setValue(null);
        txtContent.setText("");
        txtCommentId.setText("");
        txtCommentSearch.setText("");
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
            cbPostId.setValue(comment.getPost());
            cbUserId.setValue(comment.getUser());



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
           this.showAlert(Alert.AlertType.INFORMATION,"Deleted comment");
           this.clearForm();
       }catch(Exception e){
           showAlert(Alert.AlertType.ERROR, "Error deleting comments, please check input value");
       }

    }

    @FXML
    public void onAdd(ActionEvent event) {
     try {
         Comment c = new Comment();

         c.setText(txtContent.getText());
         c.setPost(cbPostId.getValue());
         c.setUser(cbUserId.getValue());
         RestTemplate restTemplate = this.createRestTemplate(MediaType.APPLICATION_JSON);

         String fooResourceUrl
                 = "http://localhost:8080/api/comment";
         ResponseEntity<Comment> response = restTemplate.postForEntity(fooResourceUrl,c,Comment.class);
         System.out.println("Status code: " + response.getStatusCode());
         this.showAlert(Alert.AlertType.INFORMATION,"Added comment");
         this.clearForm();
     } catch(Exception e ){
         showAlert(Alert.AlertType.ERROR, "Error adding comments, please check input value");
     }

    }

    @FXML
    public void onUpdate(ActionEvent event) {
        try {
            Comment c = new Comment();
            c.setId(Long.parseLong(txtCommentId.getText()));
            c.setText(txtContent.getText());
            c.setPost(cbPostId.getValue());
            c.setUser(cbUserId.getValue());
            RestTemplate template = this.createRestTemplate(MediaType.APPLICATION_JSON);


            String fooResourceUrl
                    = "http://localhost:8080/api/comment";
            HttpEntity<Comment> requestUpdate = new HttpEntity<>(c);
            template.exchange(fooResourceUrl, HttpMethod.PUT, requestUpdate, Void.class);
            this.showAlert(Alert.AlertType.INFORMATION,"Updated comment");
        } catch(Exception e ){
            showAlert(Alert.AlertType.ERROR, "Error updating comments, please check input value");
        }
    }


    private void getAllUsers() {
        try{
        RestTemplate restTemplate = this.createRestTemplate(MediaType.ALL);

        String fooResourceUrl
                = "http://localhost:8080/api/user/";

        ResponseEntity<User[]> response = restTemplate.getForEntity(fooResourceUrl, User[].class);
        System.out.println("Status code: " + response.getStatusCode());
        User[] users = response.getBody();

        List<User> lectureList = Arrays.asList(users);
        ObservableList<User> observableList = FXCollections.observableArrayList(lectureList);

        cbUserId.getItems().setAll(observableList);

            cbUserId.setConverter(new StringConverter<User>() {

                @Override
                public String toString(User object) {
                    if(object == null) {
                        return "";
                    }
                    return object.getEmail();
                }

                @Override
                public User fromString(String string) {
                    return cbUserId.getItems().stream().filter(ap ->
                            string.equals(ap.getId())).findFirst().orElse(null);
                }
            });

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllPosts() {
        try{
        RestTemplate restTemplate = this.createRestTemplate(MediaType.ALL);

        String fooResourceUrl
                = "http://localhost:8080/api/post/";

        ResponseEntity<Post[]> response = restTemplate.getForEntity(fooResourceUrl, Post[].class);
        System.out.println("Status code: " + response.getStatusCode());
        Post[] posts = response.getBody();

        List<Post> lectureList = Arrays.asList(posts);
        ObservableList<Post> observableList = FXCollections.observableArrayList(lectureList);

        cbPostId.getItems().setAll(observableList);

            cbPostId.setConverter(new StringConverter<Post>() {

                @Override
                public String toString(Post object) {
                    if(object == null) {
                        return "";
                    }
                    return object.getDescription();
                }

                @Override
                public Post fromString(String string) {
                    return cbPostId.getItems().stream().filter(ap ->
                            string.equals(ap.getId())).findFirst().orElse(null);
                }
            });
        }catch (Exception e ){
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.getAllUsers();
        this.getAllPosts();
    }
}
