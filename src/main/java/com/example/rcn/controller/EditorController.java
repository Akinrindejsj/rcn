package com.example.rcn.controller;

import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import com.example.rcn.service.ArticleService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Editor-facing review console driven by the Flowable "article-approval"
 * process. Editors claim a pending task and approve or reject it.
 * (Candidate group: rcn-editors.)
 */
@Controller
@RequestMapping("/editor")
public class EditorController {

    private final ArticleService articleService;
    private final TaskService taskService;

    public EditorController(ArticleService articleService, TaskService taskService) {
        this.articleService = articleService;
        this.taskService = taskService;
    }

    /**
     * List all tasks in the rcn-editors queue (claimed and unclaimed).
     */
    @GetMapping
    public String queue(Model model, Principal principal) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateGroup("rcn-editors")
                .orderByTaskCreateTime()
                .desc()
                .list();
        model.addAttribute("tasks", tasks);
        return "pages/editor-queue";
    }

    /**
     * Open the review form for a specific task.
     */
    @GetMapping("/tasks/{taskId}")
    public String review(@PathVariable("taskId") String taskId, Model model) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new IllegalArgumentException("That review task no longer exists.");
        }
        Long articleId = (Long) taskService.getVariable(task.getId(), "articleId");
        Article article = articleService.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("The article for this task could not be found."));
        model.addAttribute("task", task);
        model.addAttribute("article", article);
        return "pages/editor-review";
    }

    /**
     * Approve or reject the submission. Claim+complete the task with the decision.
     */
    @PostMapping("/tasks/{taskId}")
    public String decide(@PathVariable("taskId") String taskId,
                         @RequestParam("decision") String decision,
                         @RequestParam(value = "comment", required = false) String comment,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "That review task is no longer available.");
            return "redirect:/editor";
        }

        // If it's unclaimed, claim it for the current editor.
        if (task.getAssignee() == null) {
            taskService.claim(taskId, principal != null ? principal.getName() : "unknown");
        }

        Map<String, Object> variables = new HashMap<>();
        if ("reject".equalsIgnoreCase(decision)) {
            variables.put("approved", false);
            variables.put("rejectionComment", comment == null ? "" : comment);
            taskService.complete(taskId, variables);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Submission rejected. The author will be notified.");
        } else {
            variables.put("approved", true);
            variables.put("editorComment", comment == null ? "" : comment);
            taskService.complete(taskId, variables);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Submission approved and published.");
        }
        return "redirect:/editor";
    }
}
