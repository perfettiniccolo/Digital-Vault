package it.io.demo;

import it.io.demo.model.Task;
import it.io.demo.repository.TaskRepository;
import it.io.demo.service.DynamicTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestReflectionRunner implements CommandLineRunner {
    @Autowired
    DynamicTaskService dynamicTaskService;

    @Autowired
    TaskRepository taskRepository;

    @Override
    public void run(String... args) throws Exception {
        //Creazione task di prova
        Task task = new Task();
        task.setId("t1");
        task.setTitle("Task 1");
        task.setDescription("Description 1");
        task.setCompleted(false);

        //Chiamata al servizio dinamico
        dynamicTaskService.inspectTask(task);

        //Salvataggio su MongoDB
        taskRepository.save(task);
    }
}
