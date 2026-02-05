package it.io.demo.controller;

import it.io.demo.exception.ResourceNotFoundException;
import it.io.demo.model.Task;
import it.io.demo.repository.TaskRepository;
import it.io.demo.service.DynamicTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DynamicTaskService dynamicTaskService;

    @GetMapping
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    @DeleteMapping
    public void deleteTask(@RequestBody Task task) {
        if(!taskRepository.existsById(task.getId())) {
            throw new ResourceNotFoundException("Task not found");
        }
        taskRepository.delete(task);
    }

    @PutMapping
    public Task updateTask(String id, @RequestBody Task task) throws IllegalAccessException {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + id));

        existingTask.setDescription(task.getDescription());
        existingTask.setCompleted(task.isCompleted());
        existingTask.setTitle(task.getTitle());

        dynamicTaskService.inspectTask(task);
        return taskRepository.save(existingTask);
    }
}
