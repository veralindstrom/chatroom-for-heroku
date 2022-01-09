package com.example.springboottutorial.id1212.controller;

import com.example.springboottutorial.id1212.entities.bridges.ChatroomCategory.ChatroomCategory;
import com.example.springboottutorial.id1212.entities.bridges.ChatroomCategory.ChatroomCategoryRepository;
import com.example.springboottutorial.id1212.entities.bridges.ChatroomUser.ChatroomUser;
import com.example.springboottutorial.id1212.entities.bridges.ChatroomUser.ChatroomUserRepository;
import com.example.springboottutorial.id1212.entities.category.CategoryRepository;
import com.example.springboottutorial.id1212.entities.chat.*;
import com.example.springboottutorial.id1212.entities.file.DBFileRepository;
import com.example.springboottutorial.id1212.entities.user.User;
import com.example.springboottutorial.id1212.entities.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
    private UserRepository userRepository;
    @Autowired
    private ChatroomUserRepository chatroomUserRepository;
    @Autowired
    private ChatroomRepository chatroomRepository;
    private User user;

    public void readCookie(@CookieValue(value = "userId", required = false) String userId) {
        if(userId != null) {
            user = userRepository.findUserByUserId(Integer.parseInt(userId));
        }
    }

    @PostMapping("/home")
    public String findUser(@RequestParam String email, @RequestParam String password, Model model, HttpServletResponse response) {
        User tempUser = userRepository.findUserByEmail(email);
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
        ArrayList<ChatroomUser> chatroomUser = chatroomUserRepository.findChatroomUsersByUserId(user.getUserId());
        ArrayList<Chatroom> chatrooms = new ArrayList<>(); // Remaining in Your chatrooms
        ArrayList<Chatroom> publicChatrooms = chatroomRepository.getAllPublicId();
        ArrayList<Integer> favChatrooms = chatroomUserRepository.getAllChatroomIdsForFavoriteChatroomByUserId(user.getUserId());
        ArrayList<Chatroom> favoriteChatrooms = new ArrayList<>();

        for (Integer favChat : favChatrooms) {
            Chatroom fcr = chatroomRepository.findChatRoomByChatroomId(favChat);
            favoriteChatrooms.add(fcr); // / Users favorite chatrooms
        }
        for (ChatroomUser cu : chatroomUser) {
            Integer chatId = cu.getChatroomId();
            Chatroom cr = chatroomRepository.findChatRoomByChatroomId(chatId);
            chatrooms.add(cr);   // Chatroom the user is part of
        }

        publicChatrooms.removeAll(chatrooms); // Public chatrooms, user is not in
        chatrooms.removeAll(favoriteChatrooms); // Removing favorite from all chatrooms, user is in

        model.addAttribute("chatroom", chatrooms);
        model.addAttribute("pubchatroom", publicChatrooms);
        model.addAttribute("favchatroom", favoriteChatrooms);
    }

   /* @GetMapping("/create-chatroom")
    public String createChatroom(Model model) {
        if (user != null) {
            Chatroom chatroom = new Chatroom();
            ArrayList<Category> categories = (ArrayList<Category>) categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("chatroom", chatroom);

            return "create-chatroom";
        } else {
            String message = "You are logged out";
            model.addAttribute("message", message);
        }
        return "index";
    }*/

  /*  @PostMapping("/create-chatroom-process")
    public String processChatroom(Model model, Chatroom chatroom, @RequestParam(required = false) ArrayList<Integer> categoryId) {
        if(user != null){
            chatroom.addUserCount(1);
            chatroomRepository.save(chatroom);

            if(categoryId != null) {
                for (Integer id : categoryId) {
                    chatroomCategory = new ChatroomCategory();
                    chatroomCategory.setCategoryId(id);
                    chatroomCategory.setChatroomId(chatroom.getId());
                    chatroomCategoryRepository.save(chatroomCategory);
                }
            }
            // ChatroomUser for Admin always
            ChatroomUser chatroomUser = new ChatroomUser();
            chatroomUser.setChatroomId(chatroom.getId());
            chatroomUser.setUserId(user.getUserId());
            chatroomUser.setAdmin(1); // TRUE
            chatroomUser.setFavorite(0); // FALSE
            chatroomUserRepository.save(chatroomUser);


            // Private status for chatroom
            Boolean statusPublic = chatroomRepository.getStatusByChatroomId(chatroom.getId());
            if (!statusPublic) {
                EmailsDTO emails = new EmailsDTO();
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
    }*/

 /*   @PostMapping("/create-chatroom-private-process/{id}")
    public String processChatroomPrivate(Model model, EmailsDTO emails, @PathVariable Integer id) {
        if (user != null) {
            Integer number = emails.getNumber();
            Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);

            String emailInput = emails.getEmails();
            if(emailInput == null) {
                String message = "No emails were entered";
                model.addAttribute("nomail", message);
            }
            else {
                long count = emailInput.chars().filter(ch -> ch == ',').count(); // ex. 3 emails = 2 ","
                long emailsCounted = count + 1;
                if (emailsCounted == number) {
                    if(number != 1) { // If more than one email added
                        String[] uniqueEmails = emailInput.split(", ", number); // check first for number of , to know limit value
                        splitEmailsPrivateChatroom(model, uniqueEmails, chatroom);
                    }
                }
                if (emailsCounted != number) {
                    if (count != 0){ // If more than one email added
                        String missMatch = "You entered " + number + " users to add, but entered " + emailsCounted + " emails.";
                        String[] uniqueEmails = emailInput.split(", ", number); // check first for number of , to know limit value
                        splitEmailsPrivateChatroom(model, uniqueEmails, chatroom);
                        model.addAttribute("missmatch", missMatch);
                    }
                }
                if (count == 0) {
                    Integer userId = userRepository.getUserIdByEmail(emailInput);
                    if(userId == null){
                        String message = "The email you entered is not in our system " + emailInput;
                        model.addAttribute("nouser", message);
                    }
                    if(userId != null){
                        model.addAttribute("onemail", emailInput);
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
    }*/

  /*  public void splitEmailsPrivateChatroom(Model model,String[] emails, Chatroom chatroom) {
        ArrayList<String> failedEmails = new ArrayList<>();
        ArrayList<String> successfulEmails = new ArrayList<>();
        long fail = 0;
        long success = 0;
        for (String mail : emails) {
            System.out.println("EMAILS = " + mail + " ------------------------------------------------------");
            Integer userId = userRepository.getUserIdByEmail(mail);
            if (userId == null) {
                failedEmails.add(mail);
                fail = fail + 1;
            }
            if (userId != user.getUserId()) { // Not re-add admin user, chatroom creator
                if (userId != null) {
                    chatroom.addUserCount(1);
                    ChatroomUser chatroomUser = new ChatroomUser();
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
        }
        else if (success == 0 && fail != 0) { // Failure
            model.addAttribute("failed", failedEmails);
        }
        else if (fail == 0 && success != 0) { // Success
            model.addAttribute("success", successfulEmails);
        }

    }*/


    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "signup";
    }

    @PostMapping("/signup-process")
    public String processRegister(User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);

        return "signup-success";
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
