package org.example.diiaclone.controller;

import jakarta.validation.Valid;
import org.example.diiaclone.dto.UserCreateDto;
import org.example.diiaclone.entity.User;
import org.example.diiaclone.mapper.UserMapper;
import org.example.diiaclone.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getAllUsers(Model model) {

        model.addAttribute("users",
                userService.getAllUsers());

        return "users";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {

        model.addAttribute(
                "userCreateDto",
                new UserCreateDto());

        return "user-form";
    }

    @PostMapping
    public String createUser(
            @Valid @ModelAttribute UserCreateDto userCreateDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "user-form";
        }

        User user = UserMapper.toEntity(userCreateDto);

        userService.saveUser(user);

        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return "redirect:/users";
    }

}
