package Kolokviumski.fourth10;


import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//Component
interface ITask {
    int getPriority();

    LocalDateTime getDeadline();

    String getCategory();

}

class Task implements ITask {
    String category, name, description;

    public Task(String category, String name, String description) {
        this.category = category;
        this.name = name;
        this.description = description;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public LocalDateTime getDeadline() {
        return LocalDateTime.MAX;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

abstract class TaskDecorator implements ITask {
    ITask wrapped;

    public TaskDecorator(ITask wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getCategory() {
        return wrapped.getCategory();
    }
}

class PriorityTaskDecorator extends TaskDecorator {
    int priority;


    public PriorityTaskDecorator(ITask wrapped, int priority) {
        super(wrapped);
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public LocalDateTime getDeadline() {
        return wrapped.getDeadline();
    }

    @Override
    public String toString() {
        return wrapped.toString().replace("}", ", priority=" + priority + '}');
    }
}

class DeadlineTaskDecorator extends TaskDecorator {
    LocalDateTime deadline;


    public DeadlineTaskDecorator(ITask wrapped, LocalDateTime deadline) {
        super(wrapped);
        this.deadline = deadline;
    }

    @Override
    public int getPriority() {
        return wrapped.getPriority();
    }

    @Override
    public LocalDateTime getDeadline() {
        return deadline;
    }

    @Override
    public String toString() {
        return wrapped.toString().replace("}", ", deadline=" + deadline + '}');
    }
}

class TaskFactory {
    public static ITask createTask(String line) {
        //School,NP,prepare for June exam :) - no priority, no deadline 3
        //School,NP,solve all exercises,3 - priority, no deadline 4
        //work,Netcetera,research for ML algorithms,2020-06-28T16:00:00.000 - deadline, no priority 4
        //work,Netcetera,new feature for the web app,2020-06-28T16:00:00.000,1 - priority and deadline 5

        String[] parts = line.split(",");
        String category = parts[0];
        String name = parts[1];
        String description = parts[2];

        //

        ITask task = new Task(category, name, description);
        if (parts.length == 3) {
            return task;
        } else if (parts.length == 4) {
            try {
                int priority = Integer.parseInt(parts[3]);
                return new PriorityTaskDecorator(task, priority);
            } catch (Exception e) {
                LocalDateTime deadline = LocalDateTime.parse(parts[3]);
                return new DeadlineTaskDecorator(task, deadline);
            }
        } else {
            LocalDateTime deadline = LocalDateTime.parse(parts[3]);
            int priority = Integer.parseInt(parts[4]);
            task = new DeadlineTaskDecorator(task, deadline);
            task = new PriorityTaskDecorator(task, priority);
            return task;

//            return new PriorityTaskDecorator(new DeadlineTaskDecorator(task, deadline), priority);
        }
    }
}

class TaskManager {

    Map<String, List<ITask>> tasks = new HashMap<>();

    public void readTasks(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        tasks = br.lines()
                .map(TaskFactory::createTask)
                .collect(Collectors.groupingBy(
                        ITask::getCategory,
                        Collectors.toList()
                ));
    }

    public void printTasks(PrintStream out, boolean priority, boolean category) {
        PrintWriter pw = new PrintWriter(out);

        if (category) {
            tasks.forEach((cat, tasks) -> {
                pw.println(cat.toUpperCase());
                for (ITask task : tasks) {
                    pw.println(task.toString());
                }
            });
        }

        pw.flush();
    }
}


public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}
