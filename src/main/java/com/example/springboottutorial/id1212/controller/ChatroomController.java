package com.example.springboottutorial.id1212.controller;

import com.example.springboottutorial.id1212.DTO.EmailsDTO;
import com.example.springboottutorial.id1212.DTO.MessageUserDTO;
import com.example.springboottutorial.id1212.DTO.UserRoleDTO;
import com.example.springboottutorial.id1212.entities.bridges.ChatroomCategory.ChatroomCategory;
import com.example.springboottutorial.id1212.entities.bridges.ChatroomCategory.ChatroomCategoryRepository;
import com.example.springboottutorial.id1212.entities.bridges.ChatroomUser.ChatroomUser;
import com.example.springboottutorial.id1212.entities.bridges.ChatroomUser.ChatroomUserRepository;
import com.example.springboottutorial.id1212.entities.category.Category;
import com.example.springboottutorial.id1212.entities.category.CategoryRepository;
import com.example.springboottutorial.id1212.entities.chat.*;
import com.example.springboottutorial.id1212.entities.file.DBFileRepository;
import com.example.springboottutorial.id1212.entities.user.User;
import com.example.springboottutorial.id1212.entities.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
public class ChatroomController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private  ChatroomUserRepository chatroomUserRepository;
    @Autowired
    private  ChatroomRepository chatroomRepository;
    @Autowired
    private  CategoryRepository categoryRepository;
    @Autowired
    private  ChatroomCategoryRepository chatroomCategoryRepository;
    @Autowired
    private  RoleRepository roleRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private DBFileRepository fileRepository;

    private User user;
    private ChatroomCategory chatroomCategory;

    public void readCookie(@CookieValue(value = "userId", required = false) String userId) {
        if(userId != null) {
            user = userRepository.findUserByUserId(Integer.parseInt(userId));
        }
    }

    @GetMapping("/test-chat-functionality")
    public String showTestChat(){
        return "test-chat-functionality";
    }

    @GetMapping("/chatroom/{id}")
    public String showChatroom(@PathVariable Integer id, Model model, @CookieValue(value = "userId", required = false) String userIdFromCookie) {
        readCookie(userIdFromCookie);
        if (user != null) {
            Integer userId = user.getUserId();
            Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);
            ChatroomUser chatroomUser = chatroomUserRepository.findChatroomUserByUserIdAndChatroomId(userId, id);
            //Chatroom chatroomWithUser = chatroomRepository.findChatRoomByChatroomId(chatroomUser.getChatroomId());
            ArrayList<ChatroomCategory> chatroomCategories = chatroomCategoryRepository.findChatroomCategoriesByChatroomId(id);
            ArrayList<Category> categories = new ArrayList<>();


            for (ChatroomCategory chatroomCategory : chatroomCategories) {
                categories.add(categoryRepository.findCategoryBycategoryId(chatroomCategory.getCategoryId()));
            }

            if (chatroomUser == null) { // if chatroomUser not exist - user joined new public room
                chatroom.addUserCount(1);
                chatroomRepository.save(chatroom);
                chatroomUser = new ChatroomUser();
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
                String favoriteString = "false";
                if (favoriteStatus != null) { //
                    if (favoriteStatus.equals(1)) {
                        favoriteString = "true";
                    }
                } else {
                    favoriteStatus = 0;
                }
                model.addAttribute("favString", favoriteString); // must have for js code, not work with bool or number
                model.addAttribute("favorite", favoriteStatus);

                Integer roleId = chatroomUser.getRoleId();
                if (roleId != null) {
                    Role role = roleRepository.findRoleByRoleId(roleId);
                    String roleName = role.getRole();
                    model.addAttribute("userRole", roleName);
                }

                model.addAttribute("categories", categories);
                //model.addAttribute("chatroom", chatroomWithUser);
                Chatroom cr = chatroomRepository.findChatRoomByChatroomId(id);
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
        ArrayList<Message> fileMessages = messageRepository.findMessagesByChatroomIdAndFileIdIsNotNull(chatroomId);

        model.addAttribute("fileRepository", fileRepository);
        model.addAttribute("fileMessages", fileMessages);
        model.addAttribute("userRepo", userRepository);
    }

    private void chatroomRole(Model model, Integer chatroomId) {
        ArrayList<Integer> userIdsInChatroom = chatroomUserRepository.getAllUserIdsByChatroomIdDescRoleIdOrder(chatroomId);
        ArrayList<UserRoleDTO> userRoles = new ArrayList<UserRoleDTO>();
        UserRoleDTO adminUser = new UserRoleDTO();

        for (Integer userId : userIdsInChatroom) {
            UserRoleDTO userRole = new UserRoleDTO();
            String username = userRepository.getUsername(userId);
            Integer admin = chatroomUserRepository.getAdminStatusByUserIdChatroomId(userId, chatroomId);
            Integer roleId = chatroomUserRepository.getRoleIdByUserIdChatroomId(userId, chatroomId);

            if (roleId != null) {
                Role role = roleRepository.findRoleByRoleId(roleId);
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

    private void chatroom(Model model, Chatroom chat) {
        Integer chatId = chat.getId();
        //Integer userId = user.getUserId();
        //ArrayList<ChatroomUser> chatroomUserList = chatroomUserRepository.findChatroomUsersByChatroomId(chatId);
        ArrayList<Integer> userIdList = chatroomUserRepository.getAllUserIdsByChatroomId(chatId);
        ArrayList<String> userNames = new ArrayList<String>();
        for (Integer id : userIdList) {
            String name = userRepository.getUsername(id);
            userNames.add(name);
        }
        model.addAttribute("usernames", userNames);
    }

    private void prevConversation(Model model, Integer chatroomId) {
        ArrayList<Message> conversation = messageRepository.getAllMessagesInChatroom(chatroomId);
        ArrayList<MessageUserDTO> usernameConversations = new ArrayList<MessageUserDTO>();
        for (Message mes : conversation) {
            Integer userId = messageRepository.getUserIdByMessageId(mes.getId());
            String name = userRepository.getUsername(userId);

            MessageUserDTO userMessage = new MessageUserDTO();
            userMessage.setMessage(mes);
            userMessage.setUsername(name);
            usernameConversations.add(userMessage);
        }
        model.addAttribute("conversation", usernameConversations);
    }

    @GetMapping("/chatroom/{id}/create-role")
    public String createRole(Model model, @PathVariable Integer id,  @CookieValue(value = "userId", required = false) String userIdFromCookie) {
        readCookie(userIdFromCookie);
        if (user != null) {
            Integer currentRole = chatroomUserRepository.getRoleIdByUserIdChatroomId(user.getUserId(), id);
            if (currentRole != null) {
                chatroomUserRepository.updateChatroomUserWithRoleId(null, id, user.getUserId());
            }
            Role role = new Role();
            Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);
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
            Role chatRole = new Role();
            chatRole.setRole(role);
            roleRepository.save(chatRole);

            Integer roleId = chatRole.getRoleId();
            Integer userId = user.getUserId();

            //ChatroomUser chatroomUser = chatroomUserRepository.findChatroomUserByUserIdAndChatroomId(user.getUserId(), id);

            chatroomUserRepository.updateChatroomUserWithRoleId(roleId, id, userId);

            Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);
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
        ChatroomUser chatroomUser = chatroomUserRepository.findChatroomUserByUserIdAndChatroomId(user.getUserId(), id);
       /* Integer roleId = chatroomUserRepository.getRoleIdByUserIdChatroomId(user.getUserId(), id);
        Role role = roleRepository.findRoleByRoleId(roleId);
        roleRepository.delete(role);*/

        chatroomUserRepository.delete(chatroomUser);
        System.out.println("AFTER DELETE CHATROOM USER! ----------------------------------------");
        Chatroom chatroom = chatroomRepository.findChatRoomByChatroomId(id);
        ArrayList<ChatroomCategory> chatroomCategories = chatroomCategoryRepository.findChatroomCategoriesByChatroomId(id);
        if (chatroomUser.getAdmin() == 1) { // TRUE
            /*if chatroom had categories the bridge needs to be removed first*/
            if (chatroomCategories.size() > 0) {
                for (ChatroomCategory cc : chatroomCategories) {
                    chatroomCategoryRepository.delete(cc);
                }
            }
            /*if chatroom had messages those needs to be removed too */
            ArrayList<Integer> messageIdsInChatroom = messageRepository.getAllMessageIdsByChatroomId(id);
            if (messageIdsInChatroom.size() > 0) {
                for (Integer mId : messageIdsInChatroom) {
                    Message m = messageRepository.findMessageByMessageId(mId);
                    messageRepository.delete(m);
                }
            }
            /*deleting chatroom*/
            chatroomRepository.delete(chatroom);
        }
        return "leave-chatroom";
    }

    @GetMapping("/create-chatroom")
    public String createChatroom(Model model, @CookieValue(value = "userId", required = false) String userId) {
        readCookie(userId);
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
    }

    @PostMapping("/create-chatroom-process")
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
    }

    @PostMapping("/create-chatroom-private-process/{id}")
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
                        String missMatch = "You entered " + number + "users to add, but entered " + emailsCounted + " many emails.";
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
    }

    public void splitEmailsPrivateChatroom(Model model,String[] emails, Chatroom chatroom) {
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

    }
}
