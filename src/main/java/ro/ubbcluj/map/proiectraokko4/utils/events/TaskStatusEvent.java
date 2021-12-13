package ro.ubbcluj.map.proiectraokko4.utils.events;


import javafx.concurrent.Task;

public class TaskStatusEvent implements Event {
    private TaskExecutionStatusEventType type;
    private Task task;
    public TaskStatusEvent(TaskExecutionStatusEventType type, Task task) {
        this.task=task;
        this.type=type;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskExecutionStatusEventType getType() {
        return type;
    }

    public void setType(TaskExecutionStatusEventType type) {
        this.type = type;
    }
}
