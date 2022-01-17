package com.example.id1212.ChatroomApp;

import com.example.id1212.ChatroomApp.chat.ChatMessage;
import com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory;
import com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser;
import com.example.id1212.ChatroomApp.entities.bridges.DBFileUser.DBFileUserId;
import com.example.id1212.ChatroomApp.entities.file.DBFile;
import com.example.id1212.ChatroomApp.entities.file.DBFileRepository;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.persistence.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(com.example.id1212.ChatroomApp.websocket.WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            logger.info("User Disconnected : " + username);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
package com.example.id1212.ChatroomApp.websocket;

        import org.springframework.context.annotation.Configuration;
        import org.springframework.messaging.simp.config.MessageBrokerRegistry;
        import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }
}

package com.example.id1212.ChatroomApp.filestorage;

        import com.example.id1212.ChatroomApp.entities.file.DBFile;
        import com.example.id1212.ChatroomApp.entities.file.DBFileRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
        import org.springframework.util.StringUtils;
        import org.springframework.web.multipart.MultipartFile;
        import java.io.IOException;

@Service
public class DBFileStorageService {

    @Autowired
    private DBFileRepository dbFileRepository;

    public DBFile storeFile(MultipartFile file) {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());


            DBFile dbFile = new DBFile(fileName, file.getContentType(), file.getBytes());

            return dbFileRepository.save(dbFile);

    }

    public DBFile getFile(String fileId) {
        return dbFileRepository.findById(fileId);
    }
}

package com.example.id1212.ChatroomApp.file;


        import java.io.Serializable;

public class UploadFileResponse implements Serializable {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
    private String fileId;

    public UploadFileResponse(String fileName, String fileDownloadUri, String fileId, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileId = fileId;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public long getSize() {
        return size;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setSize(long size) {
        this.size = size;
    }

    // Getters and Setters (Omitted for brevity)
}

package com.example.id1212.ChatroomApp.entities.user;

        import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userId;

    private String username;
    private String email;
    private String password;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer id) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package com.example.id1212.ChatroomApp.entities.user;

        import org.springframework.data.jpa.repository.Query;
        import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<com.example.id1212.ChatroomApp.entities.user.User, String> {
    com.example.id1212.ChatroomApp.entities.user.User findUserByEmail(String email);
    com.example.id1212.ChatroomApp.entities.user.User findUserByUserId(Integer id);

    @org.springframework.data.jpa.repository.Query("SELECT u.username FROM User u WHERE u.userId = ?1")
    String getUsername(Integer userId);

    @Query("SELECT u.userId FROM User u WHERE u.email = ?1")
    Integer getUserIdByEmail(String email);


}
package com.example.id1212.ChatroomApp.entities.file;

        import org.hibernate.annotations.GenericGenerator;

        import javax.persistence.*;

@Entity
@Table(name = "file")
public class DBFile {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String fileId;

    private String fileName;

    private String fileType;

    @Lob
    private byte[] data;

    public DBFile() {

    }

    public DBFile(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    public String getId() {
        return fileId;
    }

    public void setId(String id) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
package com.example.id1212.ChatroomApp.entities.file;

        import org.springframework.data.repository.CrudRepository;

public interface DBFileRepository extends CrudRepository<com.example.id1212.ChatroomApp.entities.file.DBFile, String> {
    com.example.id1212.ChatroomApp.entities.file.DBFile findDBFileByFileId(String fileId);
}

package com.example.id1212.ChatroomApp.entities.chat;

        import javax.persistence.*;

@Entity
@Table(name = "chatroom")
public class Chatroom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer chatroomId;
    private String name;
    private int userCount;
    private boolean status;

    public Integer getId() {
        return chatroomId;
    }

    public void setId(Integer chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getUserCount(){
        return userCount;
    }

    public void addUserCount(int userCount){
        this.userCount += userCount;
    }

    public boolean getStatus(){
        return status;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

}
package com.example.id1212.ChatroomApp.entities.chat;

        import org.springframework.data.jpa.repository.Query;
        import org.springframework.data.repository.CrudRepository;

        import java.util.ArrayList;

public interface ChatroomRepository extends CrudRepository<com.example.id1212.ChatroomApp.entities.chat.Chatroom, String> {
    com.example.id1212.ChatroomApp.entities.chat.Chatroom findChatRoomByChatroomId(Integer chatroomId);

    @Query("select c from Chatroom c WHERE c.status = true")
    ArrayList<com.example.id1212.ChatroomApp.entities.chat.Chatroom> getAllPublicId();

    @Query("select c.status from Chatroom c WHERE c.chatroomId = ?1")
    Boolean getStatusByChatroomId(Integer chatroomId);
}

package com.example.id1212.ChatroomApp.entities.chat;

        import javax.persistence.*;
        import java.util.Date;

@Entity
@Table(name="message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer messageId;
    private String message;
    private Date date;
    private String fileId;
    private Integer userId;
    private Integer chatroomId;

    public Integer getId() {
        return messageId;
    }

    public void setId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public String getFileId(){
        return fileId;
    }

    public void setFileId(String fileId){
        this.fileId = fileId;
    }

    public Integer getUserId(){
        return userId;
    }

    public void setUserId(Integer userId){
        this.userId = userId;
    }

    public Integer getchatroomId(){
        return chatroomId;
    }

    public void setchatroomId(Integer chatroomId){
        this.chatroomId = chatroomId;
    }
}
package com.example.id1212.ChatroomApp.entities.chat;

        import org.springframework.data.jpa.repository.Query;
        import org.springframework.data.repository.CrudRepository;

        import java.util.ArrayList;

public interface MessageRepository extends CrudRepository<com.example.id1212.ChatroomApp.entities.chat.Message, Integer> {
    com.example.id1212.ChatroomApp.entities.chat.Message findMessageByMessageId(Integer messageId);
    ArrayList<com.example.id1212.ChatroomApp.entities.chat.Message> findMessagesByChatroomIdAndFileIdIsNotNull(Integer chatroomId);


    @Query("select m from Message m where m.chatroomId = ?1 order by m.date ASC")
    ArrayList<com.example.id1212.ChatroomApp.entities.chat.Message> getAllMessagesInChatroom(Integer chatroomId);

    @Query("select m.userId from Message m where m.messageId = ?1")
    Integer getUserIdByMessageId(Integer messageId);

    @Query("SELECT m.messageId FROM Message m WHERE m.chatroomId = ?1")
    ArrayList<Integer> getAllMessageIdsByChatroomId(Integer chatroomId);
}
package com.example.id1212.ChatroomApp.entities.chat;

        import javax.persistence.*;

@Entity
@Table(name="role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer roleId;
    private String role;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
package com.example.id1212.ChatroomApp.entities.chat;

        import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<com.example.id1212.ChatroomApp.entities.chat.Role, String> {
    com.example.id1212.ChatroomApp.entities.chat.Role findRoleByRoleId(Integer roleId);
}

package com.example.id1212.ChatroomApp.entities.category;

        import javax.persistence.*;
        import java.io.Serializable;

@Entity
@Table(name = "category")
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer categoryId;
    private String category;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }
}
package com.example.id1212.ChatroomApp.entities.category;

        import org.springframework.data.jpa.repository.Query;
        import org.springframework.data.repository.CrudRepository;

        import java.util.ArrayList;

public interface CategoryRepository extends CrudRepository<com.example.id1212.ChatroomApp.entities.category.Category, Integer> {
    com.example.id1212.ChatroomApp.entities.category.Category findCategoryByCategoryId(Integer categoryId);

    @Query("SELECT c.category FROM Category c")
    ArrayList<String> getAllCategoryByName();

    @Query("SELECT c.category FROM Category c WHERE c.categoryId = ?1")
    String getCategoryNameById(Integer categoryId);
}
package com.example.id1212.ChatroomApp.entities.bridges.DBFileUser;

        import javax.persistence.Entity;
        import javax.persistence.Id;
        import javax.persistence.IdClass;
        import javax.persistence.Table;
        import java.io.Serializable;

@Entity
@Table(name="user_file")
@IdClass(DBFileUserId.class)
public class DBFileUser implements Serializable {
    @Id
    private Integer userId;
    @Id
    private String fileId;

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}

package com.example.id1212.ChatroomApp.entities.bridges.DBFileUser;

        import java.io.Serializable;

public class DBFileUserId implements Serializable {
    private String fileId;
    private Integer userId;
}package com.example.id1212.ChatroomApp.entities.bridges.DBFileUser;

        import org.springframework.data.repository.CrudRepository;

public interface DBFileUserRepository extends CrudRepository<com.example.id1212.ChatroomApp.entities.bridges.DBFileUser.DBFileUser, Integer> {
    com.example.id1212.ChatroomApp.entities.bridges.DBFileUser.DBFileUser findFileUserByUserId(Integer userId);
    com.example.id1212.ChatroomApp.entities.bridges.DBFileUser.DBFileUser findFileUserByFileId(String fileId);
}
package com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser;

        import org.springframework.data.jpa.repository.Modifying;
        import org.springframework.data.jpa.repository.Query;
        import org.springframework.data.repository.CrudRepository;

        import javax.transaction.Transactional;
        import java.util.ArrayList;

public interface ChatroomUserRepository extends CrudRepository<ChatroomUser, Integer> {
    ArrayList<ChatroomUser> findChatroomUsersByUserId(Integer userId);
    ChatroomUser findChatroomUserByUserIdAndChatroomId(Integer userId, Integer chatroomId);

    @Query("SELECT c.userId FROM ChatroomUser c WHERE c.chatroomId = ?1")
    ArrayList<Integer> getAllUserIdsByChatroomId(Integer chatroomId);

    @Query("SELECT c.userId FROM ChatroomUser c WHERE c.chatroomId = ?1 order by c.roleId DESC")
    ArrayList<Integer> getAllUserIdsByChatroomIdDescRoleIdOrder(Integer chatroomId);

    @Query("SELECT c.roleId FROM ChatroomUser c WHERE c.userId = ?1 AND c.chatroomId = ?2")
    Integer getRoleIdByUserIdChatroomId(Integer userId, Integer chatroomId);

    @Query("SELECT c.admin FROM ChatroomUser c WHERE c.userId = ?1 AND c.chatroomId = ?2")
    Integer getAdminStatusByUserIdChatroomId(Integer userId, Integer chatroomId);

    @Query("SELECT c.favorite FROM ChatroomUser c WHERE c.userId = ?1 AND c.chatroomId = ?2")
    Integer getFavoriteStatusByUserIdChatroomId(Integer userId, Integer chatroomId);

    @Query("SELECT c.chatroomId FROM ChatroomUser c WHERE c.favorite = 1 AND c.userId = ?1")
    ArrayList<Integer> getAllChatroomIdsForFavoriteChatroomByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatroomUser c SET c.roleId = ?1 WHERE c.chatroomId = ?2 AND c.userId = ?3")
    void updateChatroomUserWithRoleId(Integer roleId, Integer chatroomId, Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatroomUser c SET c.favorite = ?1 WHERE c.chatroomId = ?2 AND c.userId = ?3")
    void updateChatroomUserWithFavoriteStatus (Integer favorite, Integer chatroomId, Integer userId);
}
package com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser;

        import java.io.Serializable;

public class ChatroomUserId implements Serializable {
    private Integer chatroomId;
    private Integer userId;
}
package com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser;

        import javax.persistence.*;

@Entity
@Table(name="chatroom_user")
@IdClass(com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUserId.class)
public class ChatroomUser {
    @Id
    private Integer chatroomId;
    @Id
    private Integer userId;
    @Id
    private Integer roleId;
    private Integer admin;
    private Integer favorite;

    public Integer getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(Integer chatroomId) {
        this.chatroomId = chatroomId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getAdmin(){
        return admin;
    }

    public void setAdmin(Integer admin){
        this.admin = admin;
    }

    public Integer getFavorite(){
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
    }
}
package com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory;

        import org.springframework.data.repository.CrudRepository;

        import java.util.ArrayList;

public interface ChatroomCategoryRepository extends CrudRepository<ChatroomCategory, Integer> {
    ChatroomCategory findChatroomCategoryByCategoryIdAndChatroomId(Integer categoryId, Integer chatroomId);
    ArrayList<ChatroomCategory> findChatroomCategoriesByCategoryId(Integer categoryId);
    ArrayList<ChatroomCategory> findChatroomCategoriesByChatroomId(Integer chatroomId);

}
package com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory;

        import java.io.Serializable;

public class ChatroomCategoryId implements Serializable {
    private Integer chatroomId;
    private Integer categoryId;
}
package com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory;

        import javax.persistence.Entity;
        import javax.persistence.Id;
        import javax.persistence.IdClass;
        import javax.persistence.Table;
        import java.io.Serializable;

@Entity
@Table(name="chatroom_category")
@IdClass(com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategoryId.class)
public class ChatroomCategory implements Serializable {
    @Id
    private Integer chatroomId;
    @Id
    private Integer categoryId;

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setChatroomId(Integer chatroomId) {
        this.chatroomId = chatroomId;
    }

    public Integer getChatroomId() {
        return chatroomId;
    }

}
package com.example.id1212.ChatroomApp.DTO;

public class EmailsDTO {
    private String emails;
    private Integer number;

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }


}
package com.example.id1212.ChatroomApp.DTO;

        import com.example.id1212.ChatroomApp.entities.chat.Message;

public class MessageUserDTO {
    private com.example.id1212.ChatroomApp.entities.chat.Message message;
    private String username;


    public com.example.id1212.ChatroomApp.entities.chat.Message getMessage() {
        return message;
    }

    public void setMessage(com.example.id1212.ChatroomApp.entities.chat.Message message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
package com.example.id1212.ChatroomApp.DTO;

public class UserRoleDTO {
    private String username;
    private String role;
    //private Boolean admin;
    //private UserRoleDTO admin;


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

   /* public UserRoleDTO getAdminStatus() {
        return admin;
    }

    public void setAdminStatus(UserRoleDTO admin) {
        this.admin = admin;
    }*/
}package com.example.id1212.ChatroomApp.controller;

        import com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser;
        import com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUserRepository;
        import com.example.id1212.ChatroomApp.entities.chat.*;
        import com.example.id1212.ChatroomApp.entities.user.User;
        import com.example.id1212.ChatroomApp.entities.user.UserRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.*;

        import javax.servlet.http.Cookie;
        import javax.servlet.http.HttpServletResponse;
        import java.util.ArrayList;

@Controller
public class UserController {
    @Autowired
    private com.example.id1212.ChatroomApp.entities.user.UserRepository userRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUserRepository chatroomUserRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.chat.ChatroomRepository chatroomRepository;
    private com.example.id1212.ChatroomApp.entities.user.User user;

    public void readCookie(@CookieValue(value = "userId", required = false) String userId) {
        if(userId != null) {
            user = userRepository.findUserByUserId(Integer.parseInt(userId));
        }
    }

    @PostMapping("/home")
    public String findUser(@RequestParam String email, @RequestParam String password, Model model, HttpServletResponse response) {
        com.example.id1212.ChatroomApp.entities.user.User tempUser = userRepository.findUserByEmail(email);
        if(new BCryptPasswordEncoder().matches(password, tempUser.getPassword())){
            user = tempUser;
        }
        if (user != null) {
            setCookie(response, user.getUserId().toString());
            model.addAttribute("user", user);
            Integer userId = user.getUserId();
            home(model);
            return "home";
        } else {
            String message = "Invalid e-mail/password";
            model.addAttribute("message", message);
        }
        return "index";
    }

    public void setCookie(HttpServletResponse response, String userId) {
        // create a cookie
        Cookie cookie = new Cookie("userId", userId);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // global cookie accessible every where

        //add cookie to response
        response.addCookie(cookie);
    }

    @GetMapping("/home")
    public String test(Model model, @CookieValue(value = "userId", required = false) String userId) {
        readCookie(userId);
        if (user != null) {
            model.addAttribute("user", user);
            home(model);
            return "home";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    private void home(Model model) {
        ArrayList<com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser> chatroomUser = chatroomUserRepository.findChatroomUsersByUserId(user.getUserId());
        ArrayList<com.example.id1212.ChatroomApp.entities.chat.Chatroom> chatrooms = new ArrayList<>(); // Remaining in Your chatrooms
        ArrayList<com.example.id1212.ChatroomApp.entities.chat.Chatroom> publicChatrooms = chatroomRepository.getAllPublicId();
        ArrayList<Integer> favChatrooms = chatroomUserRepository.getAllChatroomIdsForFavoriteChatroomByUserId(user.getUserId());
        ArrayList<com.example.id1212.ChatroomApp.entities.chat.Chatroom> favoriteChatrooms = new ArrayList<>();

        for (Integer favChat : favChatrooms) {
            com.example.id1212.ChatroomApp.entities.chat.Chatroom fcr = chatroomRepository.findChatRoomByChatroomId(favChat);
            favoriteChatrooms.add(fcr); // / Users favorite chatrooms
        }
        for (com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser cu : chatroomUser) {
            Integer chatId = cu.getChatroomId();
            com.example.id1212.ChatroomApp.entities.chat.Chatroom cr = chatroomRepository.findChatRoomByChatroomId(chatId);
            chatrooms.add(cr);   // Chatroom the user is part of
        }

        publicChatrooms.removeAll(chatrooms); // Public chatrooms, user is not in
        chatrooms.removeAll(favoriteChatrooms); // Removing favorite from all chatrooms, user is in

        model.addAttribute("chatroom", chatrooms);
        model.addAttribute("pubchatroom", publicChatrooms);
        model.addAttribute("favchatroom", favoriteChatrooms);
    }


    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new com.example.id1212.ChatroomApp.entities.user.User());

        return "signup";
    }

    @PostMapping("/signup-process")
    public String processRegister(Model model, com.example.id1212.ChatroomApp.entities.user.User user) {
        if (user != null) {
            String email = user.getEmail();
            com.example.id1212.ChatroomApp.entities.user.User existing = userRepository.findUserByEmail(email);
            if (existing != null) {
                String exist = "E-mail is already registered";
                model.addAttribute("exist", exist);
                return "signup";
            } else {
                user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
                userRepository.save(user);
                return "signup-success";
            }
        }
        else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @GetMapping("/logout")
    public String logout(Model model, HttpServletResponse response) {
        user = null;
        Cookie cookie = new Cookie("userId", null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        //add cookie to response
        response.addCookie(cookie);

        return "redirect:/home"; //to let user know she logged out
    }
}
package com.example.id1212.ChatroomApp.controller;

        import com.example.id1212.ChatroomApp.entities.file.DBFile;
        import com.example.id1212.ChatroomApp.entities.file.DBFileRepository;
        import com.example.id1212.ChatroomApp.file.UploadFileResponse;
        import com.example.id1212.ChatroomApp.filestorage.DBFileStorageService;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.core.io.ByteArrayResource;
        import org.springframework.core.io.Resource;
        import org.springframework.http.HttpHeaders;
        import org.springframework.http.MediaType;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;
        import org.springframework.web.multipart.MultipartFile;
        import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

        import static java.lang.Integer.parseInt;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(com.example.id1212.ChatroomApp.controller.FileController.class);

    @Autowired
    private com.example.id1212.ChatroomApp.filestorage.DBFileStorageService dbFileStorageService;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.file.DBFileRepository dbFileRepository;

    @PostMapping("/uploadFile")
    public com.example.id1212.ChatroomApp.file.UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        com.example.id1212.ChatroomApp.entities.file.DBFile dbFile = dbFileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(dbFile.getId())
                .toUriString();

        return new com.example.id1212.ChatroomApp.file.UploadFileResponse(dbFile.getFileName(), fileDownloadUri, dbFile.getId(),
                file.getContentType(), file.getSize());
    }

    @GetMapping("/downloadFile/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        // Load file from database
        com.example.id1212.ChatroomApp.entities.file.DBFile dbFile = dbFileStorageService.getFile(fileId);
        if(dbFile == null)
            dbFile = dbFileRepository.findDBFileByFileId(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dbFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"")
                .body(new ByteArrayResource(dbFile.getData()));
    }

}
package com.example.id1212.ChatroomApp.controller;

        import com.example.id1212.ChatroomApp.DTO.EmailsDTO;
        import com.example.id1212.ChatroomApp.DTO.MessageUserDTO;
        import com.example.id1212.ChatroomApp.DTO.UserRoleDTO;
        import com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory;
        import com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategoryRepository;
        import com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser;
        import com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUserRepository;
        import com.example.id1212.ChatroomApp.entities.category.Category;
        import com.example.id1212.ChatroomApp.entities.category.CategoryRepository;
        import com.example.id1212.ChatroomApp.entities.chat.*;
        import com.example.id1212.ChatroomApp.entities.file.DBFileRepository;
        import com.example.id1212.ChatroomApp.entities.user.User;
        import com.example.id1212.ChatroomApp.entities.user.UserRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.*;

        import java.util.ArrayList;

@Controller
public class ChatroomController {
    @Autowired
    private com.example.id1212.ChatroomApp.entities.user.UserRepository userRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUserRepository chatroomUserRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.chat.ChatroomRepository chatroomRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.category.CategoryRepository categoryRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategoryRepository chatroomCategoryRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.chat.RoleRepository roleRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.chat.MessageRepository messageRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.file.DBFileRepository fileRepository;

    private com.example.id1212.ChatroomApp.entities.user.User user;
    private com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory chatroomCategory;

    public void readCookie(@CookieValue(value = "userId", required = false) String userId) {
        if (userId != null) {
            user = userRepository.findUserByUserId(Integer.parseInt(userId));
        }
    }

    @GetMapping("/chatroom/{id}")
    public String showChatroom(@PathVariable Integer id, Model model, @CookieValue(value = "userId", required = false) String userIdFromCookie) {
        readCookie(userIdFromCookie);
        if (user != null) {
            Integer userId = user.getUserId();
            com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);
            com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser chatroomUser = chatroomUserRepository.findChatroomUserByUserIdAndChatroomId(userId, id);
            ArrayList<com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory> chatroomCategories = chatroomCategoryRepository.findChatroomCategoriesByChatroomId(id);
            ArrayList<com.example.id1212.ChatroomApp.entities.category.Category> categories = new ArrayList<>();


            for (com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory chatroomCategory : chatroomCategories) {
                categories.add(categoryRepository.findCategoryByCategoryId(chatroomCategory.getCategoryId()));
            }
            if(categories.size() > 0) {
                model.addAttribute("categories", categories);
            }

            if (chatroomUser == null) { // if chatroomUser not exist - user joined new public room
                chatroom.addUserCount(1);
                chatroomRepository.save(chatroom);
                chatroomUser = new com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser();
                chatroomUser.setChatroomId(id);
                chatroomUser.setUserId(userId);
                chatroomUser.setAdmin(0); // FALSE
                chatroomUser.setFavorite(0); // FALSE
                chatroomUserRepository.save(chatroomUser);
                return "redirect:/chatroom/{id}";
            }

            if (chatroom != null) {
                prevConversation(model, id);
                chatroomRole(model, id);
                chatroomFiles(model, id);


                model.addAttribute("status", chatroom.getStatus()); //if status is true then public


                Integer favoriteStatus = chatroomUserRepository.getFavoriteStatusByUserIdChatroomId(userId, id);
                if (favoriteStatus.equals(1)) {
                    model.addAttribute("favorite", favoriteStatus);
                }

                Integer roleId = chatroomUser.getRoleId();
                if (roleId != null) {
                    com.example.id1212.ChatroomApp.entities.chat.Role role = roleRepository.findRoleByRoleId(roleId);
                    String roleName = role.getRole();
                    model.addAttribute("userRole", roleName);
                }

                if(chatroomUserRepository.getAdminStatusByUserIdChatroomId(userId, id).equals(1)){
                    Boolean admin = true;
                    model.addAttribute("admin", admin);
                }

                com.example.id1212.ChatroomApp.entities.chat.Chatroom cr = chatroomRepository.findChatRoomByChatroomId(id);
                model.addAttribute("user", user);
                model.addAttribute("chatroom", cr);
                model.addAttribute("chatroomId", id);
                chatroom(model, cr);
            }

            return "chatroom";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    private void chatroomFiles(Model model, Integer chatroomId) {
        ArrayList<com.example.id1212.ChatroomApp.entities.chat.Message> fileMessages = messageRepository.findMessagesByChatroomIdAndFileIdIsNotNull(chatroomId);

        model.addAttribute("fileRepository", fileRepository);
        model.addAttribute("fileMessages", fileMessages);
        model.addAttribute("userRepo", userRepository);
    }

    private void chatroomRole(Model model, Integer chatroomId) {
        ArrayList<Integer> userIdsInChatroom = chatroomUserRepository.getAllUserIdsByChatroomIdDescRoleIdOrder(chatroomId);
        ArrayList<com.example.id1212.ChatroomApp.DTO.UserRoleDTO> userRoles = new ArrayList<com.example.id1212.ChatroomApp.DTO.UserRoleDTO>();
        com.example.id1212.ChatroomApp.DTO.UserRoleDTO adminUser = new com.example.id1212.ChatroomApp.DTO.UserRoleDTO();

        for (Integer userId : userIdsInChatroom) {
            com.example.id1212.ChatroomApp.DTO.UserRoleDTO userRole = new com.example.id1212.ChatroomApp.DTO.UserRoleDTO();
            String username = userRepository.getUsername(userId);
            Integer admin = chatroomUserRepository.getAdminStatusByUserIdChatroomId(userId, chatroomId);
            Integer roleId = chatroomUserRepository.getRoleIdByUserIdChatroomId(userId, chatroomId);

            if (roleId != null) {
                com.example.id1212.ChatroomApp.entities.chat.Role role = roleRepository.findRoleByRoleId(roleId);
                String roleName = role.getRole();
                if (admin == 1) { // TRUE
                    adminUser.setRole(roleName);
                } else {
                    userRole.setRole(roleName);
                }
            }

            if (admin == 1) { // TRUE
                adminUser.setUsername(username);
            } else {
                userRole.setUsername(username);
                userRoles.add(userRole);
            }
        }
        model.addAttribute("adminuser", adminUser);
        model.addAttribute("userroles", userRoles);
    }

    private void chatroom(Model model, com.example.id1212.ChatroomApp.entities.chat.Chatroom chat) {
        Integer chatId = chat.getId();
        ArrayList<Integer> userIdList = chatroomUserRepository.getAllUserIdsByChatroomId(chatId);
        ArrayList<String> userNames = new ArrayList<String>();
        for (Integer id : userIdList) {
            String name = userRepository.getUsername(id);
            userNames.add(name);
        }
        model.addAttribute("usernames", userNames);
    }

    private void prevConversation(Model model, Integer chatroomId) {
        ArrayList<com.example.id1212.ChatroomApp.entities.chat.Message> conversation = messageRepository.getAllMessagesInChatroom(chatroomId);
        ArrayList<com.example.id1212.ChatroomApp.DTO.MessageUserDTO> usernameConversations = new ArrayList<com.example.id1212.ChatroomApp.DTO.MessageUserDTO>();
        for (com.example.id1212.ChatroomApp.entities.chat.Message mes : conversation) {
            Integer userId = messageRepository.getUserIdByMessageId(mes.getId());
            String name = userRepository.getUsername(userId);

            com.example.id1212.ChatroomApp.DTO.MessageUserDTO userMessage = new com.example.id1212.ChatroomApp.DTO.MessageUserDTO();
            userMessage.setMessage(mes);
            userMessage.setUsername(name);
            usernameConversations.add(userMessage);
        }
        model.addAttribute("conversation", usernameConversations);
    }

    @GetMapping("/chatroom/{id}/create-role")
    public String createRole(Model model, @PathVariable Integer id, @CookieValue(value = "userId", required = false) String userIdFromCookie) {
        readCookie(userIdFromCookie);
        if (user != null) {
            Integer currentRole = chatroomUserRepository.getRoleIdByUserIdChatroomId(user.getUserId(), id);
            if (currentRole != null) {
                chatroomUserRepository.updateChatroomUserWithRoleId(null, id, user.getUserId());
            }
            com.example.id1212.ChatroomApp.entities.chat.Role role = new com.example.id1212.ChatroomApp.entities.chat.Role();
            com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);
            model.addAttribute("role", role);
            model.addAttribute("user", user);
            model.addAttribute("chatroom", chatroom);

            return "create-role";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @PostMapping("/chatroom/{id}/create-role-process")
    public String processRole(Model model, @PathVariable Integer id, String role) { // Does not work when Role role as should
        if (user != null) {
            com.example.id1212.ChatroomApp.entities.chat.Role chatRole = new com.example.id1212.ChatroomApp.entities.chat.Role();
            chatRole.setRole(role);
            roleRepository.save(chatRole);

            Integer roleId = chatRole.getRoleId();
            Integer userId = user.getUserId();

            chatroomUserRepository.updateChatroomUserWithRoleId(roleId, id, userId);

            com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);
            model.addAttribute("chatroom", chatroom);
            model.addAttribute("role", role);

            return "create-role-success";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @GetMapping("/leave-chatroom/{id}")
    public String leaveChatroom(@PathVariable Integer id, Model model) {
        if(user != null) {
            com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser chatroomUser = chatroomUserRepository.findChatroomUserByUserIdAndChatroomId(user.getUserId(), id);

            chatroomUserRepository.delete(chatroomUser);
            com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);
            ArrayList<com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory> chatroomCategories = chatroomCategoryRepository.findChatroomCategoriesByChatroomId(id);
            if (chatroomUser.getAdmin() == 1) {
                if (chatroomCategories.size() > 0) {
                    for (com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory cc : chatroomCategories) {
                        chatroomCategoryRepository.delete(cc);
                    }
                }
                /*if chatroom had messages those needs to be removed too */
                ArrayList<Integer> messageIdsInChatroom = messageRepository.getAllMessageIdsByChatroomId(id);
                if (messageIdsInChatroom.size() > 0) {
                    for (Integer mId : messageIdsInChatroom) {
                        com.example.id1212.ChatroomApp.entities.chat.Message m = messageRepository.findMessageByMessageId(mId);
                        messageRepository.delete(m);
                    }
                }
                chatroomRepository.delete(chatroom);
            }
            return "leave-chatroom";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @GetMapping("/create-chatroom")
    public String createChatroom(Model model, @CookieValue(value = "userId", required = false) String userId) {
        readCookie(userId);
        if (user != null) {
            com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom = new com.example.id1212.ChatroomApp.entities.chat.Chatroom();
            ArrayList<com.example.id1212.ChatroomApp.entities.category.Category> categories = (ArrayList<com.example.id1212.ChatroomApp.entities.category.Category>) categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("chatroom", chatroom);

            return "create-chatroom";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @PostMapping("/create-chatroom-process")
    public String processChatroom(Model model, com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom, @RequestParam(required = false) ArrayList<Integer> categoryId) {
        if (user != null) {
            chatroom.addUserCount(1);
            chatroomRepository.save(chatroom);

            if (categoryId != null) {
                for (Integer id : categoryId) {
                    chatroomCategory = new com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory();
                    chatroomCategory.setCategoryId(id);
                    chatroomCategory.setChatroomId(chatroom.getId());
                    chatroomCategoryRepository.save(chatroomCategory);
                }
            }
            // ChatroomUser for Admin always
            com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser chatroomUser = new com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser();
            chatroomUser.setChatroomId(chatroom.getId());
            chatroomUser.setUserId(user.getUserId());
            chatroomUser.setAdmin(1); // TRUE
            chatroomUser.setFavorite(0); // FALSE
            chatroomUserRepository.save(chatroomUser);


            // Private status for chatroom
            Boolean statusPublic = chatroomRepository.getStatusByChatroomId(chatroom.getId());
            if (!statusPublic) {
                com.example.id1212.ChatroomApp.DTO.EmailsDTO emails = new com.example.id1212.ChatroomApp.DTO.EmailsDTO();
                model.addAttribute("emails", emails);
                model.addAttribute("user", user);
                model.addAttribute("chatroom", chatroom);

                return "create-chatroom-private";
            }

            return "create-chatroom-success";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @GetMapping("/create-category")
    public String createCategory(Model model, @CookieValue(value = "userId", required = false) String userId) {
        readCookie(userId);
        if (user != null) {
            com.example.id1212.ChatroomApp.entities.category.Category category = new com.example.id1212.ChatroomApp.entities.category.Category();
            model.addAttribute("category", category);
            model.addAttribute("user", user);

            return "create-category";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @PostMapping("/create-category-process")
    public String processCategory(Model model, String category) { // Does not work when Role role as should
        if (user != null) {
            ArrayList<String> currentCat = categoryRepository.getAllCategoryByName();
            Boolean alreadyExists = false;
            for (String name : currentCat) {
                if (name.equalsIgnoreCase(category)) {
                    alreadyExists = true;
                    String existmes = "Category already exists";
                    model.addAttribute("existmes", existmes);
                }
            }
            if (!alreadyExists) {
                String categoryName = category.substring(0, 1).toUpperCase() + category.substring(1);
                com.example.id1212.ChatroomApp.entities.category.Category newCategory = new com.example.id1212.ChatroomApp.entities.category.Category();
                newCategory.setCategory(categoryName);
                categoryRepository.save(newCategory);
            }
            com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom = new com.example.id1212.ChatroomApp.entities.chat.Chatroom();
            ArrayList<com.example.id1212.ChatroomApp.entities.category.Category> categories = (ArrayList<com.example.id1212.ChatroomApp.entities.category.Category>) categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("chatroom", chatroom);
            model.addAttribute("user", user);

            return "create-chatroom"; // send in as model variable to show only if successful add
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @GetMapping("/remove-category")
    public String removeCategory(Model model, @CookieValue(value = "userId", required = false) String userId) {
        readCookie(userId);
        if (user != null) {
            ArrayList<com.example.id1212.ChatroomApp.entities.category.Category> categories = (ArrayList<com.example.id1212.ChatroomApp.entities.category.Category>) categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("user", user);

            return "remove-category";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @PostMapping("/remove-category-process")
    public String processRemoveCategory(Model model, @RequestParam(required = false) ArrayList<Integer> categoryId) { // Does not work when Role role as should
        if (user != null) {
            if (categoryId != null) {
                for (Integer id : categoryId) {
                    ArrayList<com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory> chatroomCategories = chatroomCategoryRepository.findChatroomCategoriesByCategoryId(id);
                    String catName = categoryRepository.getCategoryNameById(id);
                    if (chatroomCategories.size() > 0) {
                        //cannotRemove.add(catName);
                        String notremove = "Category " + catName + " cannot be removed when used in chatrooms.";
                        model.addAttribute("notremove", notremove);
                    } else {
                        com.example.id1212.ChatroomApp.entities.category.Category removeCat = categoryRepository.findCategoryByCategoryId(id);
                        categoryRepository.delete(removeCat);
                    }
                }
            }

            com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom = new com.example.id1212.ChatroomApp.entities.chat.Chatroom();
            ArrayList<com.example.id1212.ChatroomApp.entities.category.Category> categories = (ArrayList<com.example.id1212.ChatroomApp.entities.category.Category>) categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("chatroom", chatroom);
            model.addAttribute("user", user);

            return "create-chatroom"; // send in as model variable to show only if successful add
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @GetMapping("/chatroom/{id}/change-chatroom-category")
    public String changeChatroomCategory(Model model, @PathVariable Integer id, @CookieValue(value = "userId", required = false) String userId) {
        readCookie(userId);
        if (user != null) {
            ArrayList<com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory> chatroomCategories = chatroomCategoryRepository.findChatroomCategoriesByChatroomId(id);
            ArrayList<com.example.id1212.ChatroomApp.entities.category.Category> categories = new ArrayList<>();
            for (com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory chatroomCategory : chatroomCategories) {
                categories.add(categoryRepository.findCategoryByCategoryId(chatroomCategory.getCategoryId()));
            }
            ArrayList<com.example.id1212.ChatroomApp.entities.category.Category> allCategories = (ArrayList<com.example.id1212.ChatroomApp.entities.category.Category>) categoryRepository.findAll();
            com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);

            allCategories.removeAll(categories);

            if(categories.size() > 0){
                model.addAttribute("chatcategories", categories); // chatroom categories
            }
            if(allCategories.size() > 0){
                model.addAttribute("categories", allCategories); // all categories
            }

            model.addAttribute("chatroom", chatroom);
            model.addAttribute("user", user);

            return "change-chatroom-category";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @PostMapping("/chatroom/{id}/change-chatroom-category-process")
    public String changeChatroomCategory(Model model, @PathVariable Integer id, @RequestParam(required = false) ArrayList<Integer> categoryId, @RequestParam(required = false) ArrayList<Integer> chatcategoryId) { // Does not work when Role role as should
        if (user != null) {
            if (chatcategoryId != null) {
                for (Integer catId : chatcategoryId) {
                    com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory chatroomCategory = chatroomCategoryRepository.findChatroomCategoryByCategoryIdAndChatroomId(catId, id);
                    if (chatroomCategory != null) {
                        chatroomCategoryRepository.delete(chatroomCategory);
                    }
                }
            }
            com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);
            if (categoryId != null) {
                for (Integer catId : categoryId) {
                    chatroomCategory = new com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory.ChatroomCategory();
                    chatroomCategory.setCategoryId(catId);
                    chatroomCategory.setChatroomId(chatroom.getId());
                    chatroomCategoryRepository.save(chatroomCategory);
                }
            }
            model.addAttribute("chatroom", chatroom);
            model.addAttribute("user", user);

            return "redirect:/chatroom/{id}";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @PostMapping("/chatroom/{id}/change-favorite")
    public String changeFavorite(Model model, @PathVariable Integer id) { // Does not work when Role role as should
        if (user != null) {
            Integer currentStatus = chatroomUserRepository.getFavoriteStatusByUserIdChatroomId(user.getUserId(), id);
            if(currentStatus.equals(1)) { // remove favorite
                Integer falseStatus = 0;
                chatroomUserRepository.updateChatroomUserWithFavoriteStatus(falseStatus, id, user.getUserId());
            }
            if (currentStatus.equals(0)){ // make favorite
                Integer trueStatus = 1;
                chatroomUserRepository.updateChatroomUserWithFavoriteStatus(trueStatus, id, user.getUserId());
                model.addAttribute("favorite", trueStatus);
            }

            com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);
            model.addAttribute("chatroom", chatroom);
            model.addAttribute("user", user);

            return "redirect:/chatroom/{id}";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    @PostMapping("/create-chatroom-private-process/{id}")
    public String processChatroomPrivate(Model model, com.example.id1212.ChatroomApp.DTO.EmailsDTO emails, @PathVariable Integer id) {
        if (user != null) {
            Integer number = emails.getNumber();
            com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);

            String emailInput = emails.getEmails();
            if (emailInput == null || emailInput.equals("") || emailInput.isEmpty()) {
                String message = "No emails were entered";
                model.addAttribute("nomail", message);
            } else {
                long count = emailInput.chars().filter(ch -> ch == ',').count(); // ex. 3 emails = 2 ","
                long emailsCounted = count + 1;
                if (count == 0) {
                    Integer userId = userRepository.getUserIdByEmail(emailInput);
                    if (userId == null) {
                        String message = "The email you entered is not in our system " + emailInput;
                        model.addAttribute("nouser", message);
                    }
                    if (userId != null) {
                        model.addAttribute("onemail", emailInput);
                    }
                }
                if (emailsCounted == number) {
                    if (number != 1) { // If more than one email added
                        String[] uniqueEmails = emailInput.split(", ", number); // check first for number of , to know limit value
                        splitEmailsPrivateChatroom(model, uniqueEmails, chatroom);
                    }
                }
                if (emailsCounted != number) {
                    if (count != 0) { // If more than one email added
                        String missMatch = "You entered " + number + " users to add, but entered " + emailsCounted + " emails";
                        String[] uniqueEmails = emailInput.split(", ", number); // check first for number of , to know limit value
                        splitEmailsPrivateChatroom(model, uniqueEmails, chatroom);
                        model.addAttribute("missmatch", missMatch);
                    }
                }
            }
            model.addAttribute("chatroom", chatroom);
            return "create-chatroom-success";

        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }

    public void splitEmailsPrivateChatroom(Model model, String[] emails, com.example.id1212.ChatroomApp.entities.chat.Chatroom chatroom) {
        ArrayList<String> failedEmails = new ArrayList<>();
        ArrayList<String> successfulEmails = new ArrayList<>();
        long fail = 0;
        long success = 0;
        for (String mail : emails) {
            Integer userId = userRepository.getUserIdByEmail(mail);
            if (userId == null) {
                failedEmails.add(mail);
                fail = fail + 1;
            }
            if (userId != user.getUserId()) { // Not re-add admin user, chatroom creator
                if (userId != null) {
                    chatroom.addUserCount(1);
                    com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser chatroomUser = new com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUser();
                    chatroomUser.setChatroomId(chatroom.getId());
                    chatroomUser.setUserId(userId);
                    chatroomUser.setAdmin(0); // FALSE
                    chatroomUser.setFavorite(0); // FALSE
                    chatroomUserRepository.save(chatroomUser);
                    successfulEmails.add(mail);
                    success = success + 1;
                }
            }
        }
        if (fail != 0 && success != 0) { // Not fail or succeed completely
            model.addAttribute("failed", failedEmails);
            model.addAttribute("success", successfulEmails);
        } else if (success == 0 && fail != 0) { // Failure
            model.addAttribute("failed", failedEmails);
        } else if (fail == 0 && success != 0) { // Success
            model.addAttribute("success", successfulEmails);
        }

    }
}
package com.example.id1212.ChatroomApp.controller;

        import com.example.id1212.ChatroomApp.chat.ChatMessage;
        import com.example.id1212.ChatroomApp.entities.bridges.DBFileUser.DBFileUser;
        import com.example.id1212.ChatroomApp.entities.bridges.DBFileUser.DBFileUserRepository;
        import com.example.id1212.ChatroomApp.entities.file.DBFileRepository;
        import com.example.id1212.ChatroomApp.entities.user.UserRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.messaging.handler.annotation.MessageMapping;
        import org.springframework.messaging.handler.annotation.Payload;
        import org.springframework.messaging.handler.annotation.SendTo;
        import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
        import org.springframework.stereotype.Controller;

        import com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUserRepository;


        import com.example.id1212.ChatroomApp.entities.chat.MessageRepository;
        import com.example.id1212.ChatroomApp.entities.chat.Message;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Date;

@Controller
public class ChatController {
    @Autowired
    private com.example.id1212.ChatroomApp.entities.chat.MessageRepository messageRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUserRepository chatroomUserRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.file.DBFileRepository dbFileRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.bridges.DBFileUser.DBFileUserRepository dbFileUserRepository;
    @Autowired
    private com.example.id1212.ChatroomApp.entities.user.UserRepository userRepository;


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) throws ParseException {
        String messageContent = chatMessage.getContent();
        String date = chatMessage.getDate(); // date is correct
        Integer chatroomId = chatMessage.getChatroomId();
        Integer userId = chatMessage.getUserId(); // if username is not unique
        String fileId = chatMessage.getFileId();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = formatter.parse(date);

        com.example.id1212.ChatroomApp.entities.chat.Message message = new com.example.id1212.ChatroomApp.entities.chat.Message();
        message.setMessage(messageContent);
        message.setUserId(userId);
        message.setchatroomId(chatroomId);
        message.setDate(newDate);
        message.setFileId(fileId);
        messageRepository.save(message);

        Integer messageId = message.getId();
        chatMessage.setMessageId(messageId);

        if(fileId != null) {
            com.example.id1212.ChatroomApp.entities.bridges.DBFileUser.DBFileUser dbFileUser = new com.example.id1212.ChatroomApp.entities.bridges.DBFileUser.DBFileUser();
            dbFileUser.setFileId(fileId);
            dbFileUser.setUserId(userId);
            dbFileUserRepository.save(dbFileUser);
        }
        return chatMessage;
    }


    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.leaveUser")
    @SendTo("/topic/public")
    public ChatMessage removeUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().remove("username", chatMessage.getSender());
        return chatMessage;
    }

}
package com.example.id1212.ChatroomApp.chat;


public class ChatMessage {
    private com.example.id1212.ChatroomApp.chat.ChatMessage.MessageType type;
    private String content;
    private String sender;
    private String date;
    private Integer chatroomId;
    private Integer userId;
    private String fileId;
    private Integer messageId;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        FILE
    }

    public com.example.id1212.ChatroomApp.chat.ChatMessage.MessageType getType() {
        return type;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setType(com.example.id1212.ChatroomApp.chat.ChatMessage.MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(Integer chatroomId) {
        this.chatroomId = chatroomId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }
}
package com.example.id1212;

        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
public class ChatroomApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.example.id1212.ChatroomApplication.class, args);
    }

}
