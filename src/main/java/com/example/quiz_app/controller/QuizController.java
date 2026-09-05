package com.example.quiz_app.controller;

import com.example.quiz_app.model.Quiz;
import com.example.quiz_app.service.QuizService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("quizzes", quizService.findOwnedQuizzes(user.getUsername()));
        return "quizzes";
    }

    @GetMapping("/new")
    public String createForm() {
        return "quiz-form";
    }

    @PostMapping
    public String create(
            @RequestParam String title,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes) {
        try {
            quizService.createQuiz(title, user.getUsername());
            return "redirect:/quizzes";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/quizzes/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user,
            Model model) {
        model.addAttribute("quiz", quizService.findOwnedQuiz(id, user.getUsername()));
        return "quiz-form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String title,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes) {
        try {
            quizService.updateQuiz(id, title, user.getUsername());
            return "redirect:/quizzes";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/quizzes/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes) {
        try {
            quizService.deleteQuiz(id, user.getUsername());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/quizzes";
    }
}
