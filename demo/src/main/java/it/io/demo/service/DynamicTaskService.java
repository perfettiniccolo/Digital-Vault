package it.io.demo.service;

import it.io.demo.annotation.SensitiveData;
import it.io.demo.model.Task;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class DynamicTaskService {
    public void inspectTask(Task task) throws IllegalAccessException {

            //Otteniamo tutti i campi della classe task, anche quelli privati
            Field[] fields = task.getClass().getDeclaredFields();

            for (Field field : fields) {
                //Permette di leggere tutti i campi
                field.setAccessible(true);

                try{
                    if(field.isAnnotationPresent(SensitiveData.class)){
                        System.out.println("Campo " + field.getName() + " Valore oscurato");
                    }
                    System.out.println("Campo " + field.getName() + " Tipo " + field.getType().getSimpleName() + " Valore " + field.get(task));
                } catch (IllegalAccessException e) {
                    System.out.println(e.getMessage());
                }
            }
    }
}
