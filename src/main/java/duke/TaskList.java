package duke;

import exceptions.EmptyTaskException;
import exceptions.EmptyDateException;
import exceptions.OutOfRangeException;
import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.ToDo;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The TaskList class manages the list of tasks in the Duke application.
 * It provides methods to add, mark, unmark, delete, and list tasks.
 */
public class TaskList {
    private static ArrayList<Task> taskArray;

    /**
     * Constructs a TaskList instance with an empty task array.
     */
    public TaskList() {
        taskArray = new ArrayList<>();
    }

    /**
     * Constructs a TaskList instance with the specified task array.
     *
     * @param tasks The initial task array.
     */
    public TaskList(ArrayList<Task> tasks) {
        taskArray = tasks;
    }

    public String handleListCommand() {
        String listTasks = listTasks(taskArray);
        if (listTasks != "") {
            System.out.println("Here are the tasks in your list:");
            System.out.println(listTasks);
            return "Here are the tasks in your list:\n" + listTasks;
        } else {
            System.out.println("There are no tasks in your list at the moment. Add some!");
            return "There are no tasks in your list at the moment. Add some!";
        }
    }

    /**
     * Lists the tasks stored in the task array.
     *
     * @return A string containing the list of tasks.
     */
     public static String listTasks(ArrayList<Task> taskArray) {
        String inputArrayString = "";
        int num = 1;
        for (Task task : taskArray) {
            inputArrayString += num + ". " + task.statusAndTask() + "\n";
            num++;
        }
        return inputArrayString;
    }

    /**
     * Marks a task as done based on user input.
     *
     * @param userInput The user input specifying the task to be marked.
     * @throws EmptyTaskException If the user input is missing task details.
     * @throws OutOfRangeException If the task index is out of the array range.
     * @throws IOException If an I/O error occurs while updating the task file.
     * @return A string containing a message and the details of the marked task.
     */
    public static String markTask(String userInput) throws EmptyTaskException, OutOfRangeException {
        Task currentTask = markOrUnmark(userInput, "Mark");
        currentTask.markDone();
        printMarkTask(currentTask);
        return "Nice! I've marked this task as done:\n" + currentTask.statusAndTask();
    }

    /**
     * Returns the task to be marked or unmarked as done based on user input.
     *
     * @param userInput The user input specifying the task to be marked.
     * @param command The command specifying to mark or unmark task.
     * @throws EmptyTaskException If the user input is missing task details.
     * @throws OutOfRangeException If the task index is uot of the array range.
     * @return The Task to be marked or unmarked.
     */
    public static Task markOrUnmark(String userInput, String command) throws EmptyTaskException, OutOfRangeException {
        if (userInput.equals(command)) {
            throw new EmptyTaskException(command);
        }
        int taskIndex = getTaskIndexFromMark(userInput);
        if (isInvalidIndex(taskIndex)) {
            throw new OutOfRangeException(command);
        }
        return taskArray.get(taskIndex);
    }

    /**
     * Extracts the task index from the mark or unmark userInput.
     *
     * @param userInput The user input specifying the task to be marked or unmarked.
     * @return The index of the task to be marked or unmarked.
     */
    public static int getTaskIndexFromMark(String userInput) {
        String[] markAndTaskNum = userInput.split("\\s+");
        markAndTaskNum = trimStringElements(markAndTaskNum);
        int taskNum = Integer.parseInt(markAndTaskNum[1]);
        int taskIndex = taskNum - 1;
        return taskIndex;
    }
    
    public static void printMarkTask(Task currentTask) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(currentTask.statusAndTask());
    }

    /**
     * Unmarks a task as done based on user input.
     *
     * @param userInput The user input specifying the task to be unmarked.
     * @throws EmptyTaskException If the user input is missing task details.
     * @throws OutOfRangeException If the task index is out of the array range.
     */
    public static String unmarkTask(String userInput) throws EmptyTaskException, OutOfRangeException {
        Task currentTask = markOrUnmark(userInput, "Unmark");
        currentTask.unmarkDone();
        printUnmarkTask(currentTask);
        return "OK, I've marked this task as not done yet:\n" + currentTask.statusAndTask();
    }

    public static void printUnmarkTask(Task currentTask) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(currentTask.statusAndTask());
    }

    /**
     * Adds a ToDo task based on user input.
     *
     * @param userInput The user input specifying the ToDo task.
     */
    public static String makeToDo(String userInput) throws EmptyTaskException {
        if (userInput.equals("todo")) {
            throw new EmptyTaskException("todo");
        }
        String taskName = userInput.substring("todo".length()).trim();
        ToDo newToDo = new ToDo(taskName);
        taskArray.add(newToDo);

        printMakeTask(newToDo);
        return makeTaskString(newToDo);
    }

    /**
     * Adds a Deadline task based on user input.
     *
     * @param userInput The user input specifying the Deadline task.
     * @throws EmptyDateException If the user input is missing the task deadline.
     */
    public static String makeDeadline(String userInput) throws EmptyDateException, EmptyTaskException {
        if (userInput.equals("deadline")) {
            throw new EmptyTaskException("deadline");
        } else if (userInput.endsWith("/by")) {
            throw new EmptyDateException("deadline");
        }
        String description = userInput.substring("deadline".length()).trim();
        String[] descriptionAndBy = description.split("/by");
        descriptionAndBy = trimStringElements(descriptionAndBy);

        boolean hasEmptyDate = descriptionAndBy.length == 1;
        if (hasEmptyDate) {
            throw new EmptyDateException("deadline");
        }

        String taskName = descriptionAndBy[0];
        LocalDateTime by = Storage.saveAsDate(descriptionAndBy[1]);
      
        // Users should not be able to create deadlines that are already over
        assert by.isAfter(LocalDateTime.now()): "Deadline should not have passed already.";
      
        Task newDeadline = new Deadline(taskName, by);
        taskArray.add(newDeadline);

        printMakeTask(newDeadline);
        return makeTaskString(newDeadline);
    }

    public static String[] trimStringElements(String[] elements) {
        int len = elements.length;
        String[] trimmedElements = new String[len];
        for (int i = 0; i < len; i++) {
            trimmedElements[i] = elements[i].trim();
        }
        return trimmedElements;
    }

    public static void printMakeTask(Task newTask) {
        System.out.println("Got it. I've added this task:");
        System.out.println(newTask.statusAndTask());
        System.out.println("Now you have " + taskArray.size() + " task(s) in the list.");
    }

    public static String makeTaskString(Task newTask) {
        return "Got it. I've added this task:\n" +
                newTask.statusAndTask() + "\n" +
                "Now you have " + taskArray.size() + " task(s) in the list.";
    }

    /**
     * Adds an Event task based on user input.
     *
     * @param userInput The user input specifying the Event task.
     * @throws EmptyDateException If the user input is missing the event start or end date.
     */
    public static String makeEvent(String userInput) throws EmptyDateException, EmptyTaskException {
        if (userInput.equals("event")) {
            throw new EmptyTaskException("event");
        }
        String description = userInput.substring("event".length()).trim();
        String[] descriptionAndFromTo = trimStringElements(description.split("/from"));
        String taskName = descriptionAndFromTo[0];

        String[] fromAndTo = trimStringElements(descriptionAndFromTo[1].split("/to"));
        String start = fromAndTo[0];
        String end = fromAndTo[1];

        boolean hasEmptyStart = start.isEmpty();
        boolean hasEmptyEnd = end.isEmpty();
        boolean hasEmptyDateField = hasEmptyStart || hasEmptyEnd;
        if (hasEmptyDateField) {
            throw new EmptyDateException("event");
        }

        String[] eventParts = {taskName, start, end};
        LocalDateTime startDateTime = Storage.saveAsDate(eventParts[1]);
        LocalDateTime endDateTime = Storage.saveAsDate(eventParts[2]);
      
        assert endDateTime.isAfter(startDateTime) : "End date should be after start date.";

        Event newEvent = new Event(taskName, startDateTime, endDateTime);
        taskArray.add(newEvent);
        printMakeTask(newEvent);
        return makeTaskString(newEvent);
    }

    /**
     * Deletes a task based on user input.
     *
     * @param userInput The user input specifying the task to be deleted.
     * @throws EmptyTaskException If the user input is missing task details.
     * @throws OutOfRangeException If the task index is out of the array range.
     */
    public static String deleteTask(String userInput) throws EmptyTaskException, OutOfRangeException {
        if (userInput.equals("delete")) {
            throw new EmptyTaskException("delete");
        }
        String[] parts = userInput.split("\\s+");
        int taskIndex = Integer.parseInt(parts[1]) - 1;

        if (isInvalidIndex(taskIndex)) {
            throw new OutOfRangeException("Delete");
        }

        Task currentTask = taskArray.get(taskIndex);
        taskArray.remove(currentTask);
        printDeleteTask(currentTask);
        return "Noted. I've removed this task:\n" +
                currentTask.statusAndTask() + "\n" +
                "Now you have " + taskArray.size() + " task(s) in the list.";
    }

    public static boolean isInvalidIndex(int taskIndex) {
        boolean isMoreThanLengthLimit = taskIndex >= taskArray.size();
        boolean isLessThanLengthLimit = taskIndex < 0;
        boolean isInvalidIndex = isMoreThanLengthLimit || isLessThanLengthLimit;

        return isInvalidIndex;
    }

    public static void printDeleteTask(Task currentTask) {
        System.out.println("Noted. I've removed this task:");
        System.out.println(currentTask.statusAndTask());
        System.out.println("Now you have " + taskArray.size() + " task(s) in the list.");
    }

    /**
     * Updates the task file with the current task array.
     *
     * @throws IOException If an I/O error occurs while updating the task file.
     */
    public static void updateTaskFile() throws IOException {
        try {
            Storage.generateTaskFileContent(taskArray);
        } catch (IOException e) {
            System.out.println("Something went wrong while updating the task file: " + e.getMessage());
        }
    }

    public String handleFindCommand(String userInput) throws EmptyTaskException {
        String foundTasks = find(userInput);
        if (foundTasks != "") {
            System.out.println("Here are the matching tasks in your list:");
            System.out.println(foundTasks);
            return "Here are the matching tasks in your list:\n" + foundTasks;
        } else {
            System.out.println("There are no matching tasks in your list.");
            return "There are no matching tasks in your list.";
        }
    }

    /**
     * Retrieves a task from the task array based on its index.
     *
     * @param userInput The index of the task to retrieve.
     * @return The retrieved task.
     */
    public String find(String userInput) throws EmptyTaskException {
        if (userInput.equals("find")) {
            throw new EmptyTaskException("find");
        }
        String[] parts = userInput.split("\\s+");
        String keyword = parts[1];
        ArrayList<Task> foundTasks = new ArrayList<>();
        for (Task task : taskArray) {
            if (task.getName().contains(keyword)) {
                foundTasks.add(task);
            }
        }
        return listTasks(foundTasks);
    }

    public String handleSortCommand() {
        sortedTasksByDate();
        String listSortedTasks = listSortedTasks();
        if (listSortedTasks != "") {
            System.out.println("Here are the tasks in your list:");
            System.out.println(listSortedTasks);
            return "Here are the tasks in your list:\n" + listSortedTasks;
        } else {
            System.out.println("There are no tasks in your list at the moment. Add some!");
            return "There are no tasks in your list at the moment. Add some!";
        }
    }

    public static ArrayList<Task> sortedTasksByDate() {
        ArrayList<Task> sortedTaskArray = new ArrayList<>();
        sortedTaskArray.addAll(taskArray);
        Collections.sort(sortedTaskArray, new TaskDateComparator());
        return sortedTaskArray;
    }

    public static String listSortedTasks() {
        ArrayList<Task> sortedTaskArray = sortedTasksByDate();
        String inputArrayString = "";
        int num = 1;
        for (Task task : sortedTaskArray) {
            inputArrayString += num + ". " + task.statusAndTask() + "\n";
            num++;
        }
        return inputArrayString;
    }

    public Task getTask(int i) {
         return taskArray.get(i);
    }
}
