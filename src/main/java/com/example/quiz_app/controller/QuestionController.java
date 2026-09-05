package com.example.quiz_app.controller;

import com.example.quiz_app.service.QuestionService;
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
@RequestMapping("/quizzes/{quizId}/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final QuizService quizService;

    public QuestionController(QuestionService questionService, QuizService quizService) {
        this.questionService = questionService;
        this.quizService = quizService;
    }

    @GetMapping
    public String list(
            @PathVariable Long quizId,
            @AuthenticationPrincipal UserDetails user,
            Model model) {
        model.addAttribute("quiz", quizService.findOwnedQuiz(quizId, user.getUsername()));
        model.addAttribute("questions", questionService.findQuestionsForQuiz(quizId, user.getUsername()));
        return "questions";
    }

    @GetMapping("/new")
    public String createForm(
            @PathVariable Long quizId,
            @AuthenticationPrincipal UserDetails user,
            Model model) {
        model.addAttribute("quiz", quizService.findOwnedQuiz(quizId, user.getUsername()));
        return "question-form";
    }

    @PostMapping
    public String create(
            @PathVariable Long quizId,
            @RequestParam String content,
            @RequestParam Integer timeLimit,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes) {
        try {
            questionService.createQuestion(quizId, content, timeLimit, user.getUsername());
            return "redirect:/quizzes/" + quizId + "/questions";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/quizzes/" + quizId + "/questions/new";
        }
    }

    @GetMapping("/{questionId}/edit")
    public String editForm(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserDetails user,
            Model model) {
        model.addAttribute("quiz", quizService.findOwnedQuiz(quizId, user.getUsername()));
        model.addAttribute("question",
                questionService.findQuestion(questionId, quizId, user.getUsername()));
        return "question-form";
    }

    @PostMapping("/{questionId}")
    public String update(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @RequestParam String content,
            @RequestParam Integer timeLimit,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes) {
        try {
            questionService.updateQuestion(
                    questionId, quizId, content, timeLimit, user.getUsername());
            return "redirect:/quizzes/" + quizId + "/questions";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/quizzes/" + quizId + "/questions/" + questionId + "/edit";
        }
    }

    @PostMapping("/{questionId}/delete")
    public String delete(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes) {
        try {
            questionService.deleteQuestion(questionId, quizId, user.getUsername());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/quizzes/" + quizId + "/questions";
    }
}
