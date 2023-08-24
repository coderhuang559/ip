import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Duke {
    /**
     * The main method of the Duke application.
     * Handles user interactions and manages task-related commands.
     *
     * @param args Command-line arguments (not used).
     * @throws DukeException If an error occurs during user input processing.
     */
    public static void main(String[] args) throws DukeException {
        // Send welcome message
        System.out.println(
                "    ____________________________________________________________\n" +
                "     Hello! I'm BbabBBB\n" +
                "     What can I do for you?\n" +
                "    ____________________________________________________________\n");

        // Implement function to read user input via keyboard
        Scanner scan = new Scanner(System.in);
        ArrayList<Task> taskArray = new ArrayList<>();
        boolean[] statusArray = new boolean[100];

        int inputNum = 0;

        while (true) {
            try {
                String userInput = scan.nextLine().trim();
                if (Objects.equals(userInput, "bye")) {
                    System.out.println(
                            "    ____________________________________________________________\n" +
                                    "     Bye. Hope to see you again soon!\n" +
                                    "    ____________________________________________________________\n");
                    break;

                } else if (Objects.equals(userInput, "list")) {
                    listTasks(taskArray, inputNum);

                } else if (userInput.startsWith("mark")) {
                    markTask(userInput, inputNum, taskArray);

                } else if (userInput.startsWith("unmark")) {
                    unmarkTask(userInput, inputNum, taskArray);

                } else if (userInput.startsWith("todo")) {
                    if (userInput.equals("todo")) {
                        throw new EmptyTaskException("todo");
                    }
                    String taskName = userInput.substring("todo".length()).trim();
                    makeToDo(taskName, inputNum, taskArray);
                    inputNum++;

                } else if (userInput.startsWith("deadline")) {
                    if (userInput.equals("deadline")) {
                        throw new EmptyTaskException("deadline");
                    } else if (userInput.endsWith("/by")) {
                        throw new EmptyDateException("deadline");
                    }
                    String[] deadlineParts = getDeadlineParts(userInput);
                    String taskName = deadlineParts[0];
                    String by = deadlineParts[1];
                    makeDeadline(taskName, by, inputNum, taskArray);
                    inputNum++;

                } else if (userInput.startsWith("event")) {
                    if (userInput.equals("event")) {
                        throw new EmptyTaskException("event");
                    }
                    String[] eventParts = getEventParts(userInput);
                    String taskName = eventParts[0];
                    String start = eventParts[1];
                    String end = eventParts[2];
                    makeEvent(taskName, start, end, inputNum, taskArray);
                    inputNum++;

                } else if (userInput.startsWith("delete")) {
                    deleteTask(userInput, inputNum, taskArray);
                    inputNum--;
                }
                else {
                    throw new InvalidInputException("Invalid Input");
                }
            } catch (InvalidInputException | EmptyTaskException | EmptyDateException | OutOfRangeException e) {
                System.out.println(e);
            }
        }
        scan.close();
    }

    /**
     * Lists the tasks stored in the task array.
     *
     * @param taskArray An ArrayList containing the tasks.
     */
    private static void listTasks(ArrayList<Task> taskArray, int inputNum) {
        String inputArrayString = "";
        for (int i = 0; i < inputNum; i++) {
            Task currentTask = taskArray.get(i);
            if (taskArray.get(i) != null) {
                int num = i + 1;
                inputArrayString += "     " + num + ". " + currentTask.toString() + "\n";
            } else {
                break;
            }
        }
        System.out.println(
                "    ____________________________________________________________\n" +
                        "     Here are the tasks in your list:\n" +
                        inputArrayString +
                        "    ____________________________________________________________\n");

    }

    /**
     * Marks a task as done based on user input.
     *
     * @param userInput The user input containing the task index.
     * @param inputNum  The number of tasks entered.
     * @param taskArray An ArrayList containing the tasks.
     * @throws EmptyTaskException  If the task is missing.
     * @throws OutOfRangeException If the task index is out of range.
     */
    private static void markTask(String userInput, int inputNum, ArrayList<Task> taskArray) throws EmptyTaskException, OutOfRangeException {
        if (userInput.equals("mark")) {
            throw new EmptyTaskException("mark");
        }
        String[] parts = userInput.split("\\s+");
        int taskIndex = Integer.parseInt(parts[1]) - 1;
        if (taskIndex >= inputNum || taskIndex < 0 || taskArray.get(taskIndex) == null) {
            throw new OutOfRangeException("Mark");
        }
        Task currentTask = taskArray.get(taskIndex);
        currentTask.markDone();
        System.out.println(
                "    ____________________________________________________________\n" +
                        "     Nice! I've marked this task as done:\n" +
                        "       " + currentTask.toString() + "\n" +
                        "    ____________________________________________________________\n");
    }

    /**
     * Unmarks a task as done based on user input.
     *
     * @param userInput The user input containing the task index.
     * @param inputNum  The number of tasks entered.
     * @param taskArray An ArrayList containing the tasks.
     * @throws EmptyTaskException  If the task is missing.
     * @throws OutOfRangeException If the task index is out of range.
     */
    private static void unmarkTask(String userInput, int inputNum, ArrayList<Task> taskArray) throws EmptyTaskException, OutOfRangeException {
        if (userInput.equals("unmark")) {
            throw new EmptyTaskException("unmark");
        }
        String[] parts = userInput.split("\\s+");
        int taskIndex = Integer.parseInt(parts[1]) - 1;
        if (taskIndex >= inputNum || taskIndex < 0 || taskArray.get(taskIndex) == null) {
            throw new OutOfRangeException("Unmark");
        }
        Task currentTask = taskArray.get(taskIndex);
        currentTask.unmarkDone();
        System.out.println(
                "    ____________________________________________________________\n" +
                        "     OK, I've marked this task as not done yet:\n" +
                        "       " + currentTask.toString() + "\n" +
                        "    ____________________________________________________________\n");
    }

    /**
     * Creates and adds a new ToDo task.
     *
     * @param taskName  The name of the ToDo task.
     * @param inputNum  The number of tasks entered.
     * @param taskArray An ArrayList containing the tasks.
     */
    private static void makeToDo(String taskName, int inputNum, ArrayList<Task> taskArray) {
        taskArray.add(new ToDo(taskName));
        System.out.println(
                "    ____________________________________________________________\n" +
                        "     Got it. I've added this task:\n" +
                        "       " + taskArray.get(inputNum).toString() + "\n" +
                        "     Now you have " + (inputNum + 1) + " task(s) in the list.\n" +
                        "    ____________________________________________________________\n");
    }

    /**
     * Extracts and returns parts for creating a Deadline task.
     *
     * @param userInput The user input containing task details.
     * @return An array containing the task name and due date.
     * @throws EmptyDateException If the due date is missing.
     */
    private static String[] getDeadlineParts(String userInput) throws EmptyDateException {
        String description = userInput.substring("deadline".length()).trim();
        String[] parts = description.split("/by");
        if (parts.length == 1) {
            throw new EmptyDateException("deadline");
        }
        return new String[]{parts[0].trim(), parts[1].trim()};
    }

    /**
     * Creates and adds a new Deadline task.
     *
     * @param taskName  The name of the Deadline task.
     * @param by        The due date of the Deadline task.
     * @param inputNum  The number of tasks entered.
     * @param taskArray An ArrayList containing the tasks.
     */
    private static void makeDeadline(String taskName, String by, int inputNum, ArrayList<Task> taskArray) {
        taskArray.add(new Deadline(taskName, by));
        System.out.println(
                "    ____________________________________________________________\n" +
                        "     Got it. I've added this task:\n" +
                        "       " + taskArray.get(inputNum).toString() + "\n" +
                        "     Now you have " + (inputNum + 1) + " task(s) in the list.\n" +
                        "    ____________________________________________________________\n");
    }

    /**
     * Extracts and returns parts for creating an Event task.
     *
     * @param userInput The user input containing task details.
     * @return An array containing the task name, start date, and end date.
     * @throws EmptyDateException If the start or end date is missing.
     */
    private static String[] getEventParts(String userInput) throws EmptyDateException {
        String description = userInput.substring("event".length()).trim();
        String[] partsA = description.split("/from");
        String taskName = partsA[0].trim();
        String[] partsB = partsA[1].split("/to");
        if (partsB.length == 1 || partsB[0].trim().isEmpty() || partsB[1].trim().isEmpty()) {
            throw new EmptyDateException("event");
        }
        String start = partsB[0].trim();
        String end = partsB[1].trim();
        return new String[]{taskName, start, end};
    }

    /**
     * Creates and adds a new Event task.
     *
     * @param taskName  The name of the Event task.
     * @param start     The start date of the Event task.
     * @param end       The end date of the Event task.
     * @param inputNum  The number of tasks entered.
     * @param taskArray An ArrayList containing the tasks.
     */
    private static void makeEvent(String taskName, String start, String end, int inputNum, ArrayList<Task> taskArray) {
        taskArray.add(new Event(taskName, start, end));
        System.out.println("    ____________________________________________________________\n" +
                "     Got it. I've added this task:\n" +
                "       " + taskArray.get(inputNum).toString() + "\n" +
                "     Now you have " + (inputNum + 1) + " task(s) in the list.\n" +
                "    ____________________________________________________________\n");
    }

    /**
     * Deletes a task based on user input.
     *
     * @param userInput The user input containing the task index.
     * @param inputNum  The number of tasks entered.
     * @param taskArray An ArrayList containing the tasks.
     * @throws EmptyTaskException  If the task is missing.
     * @throws OutOfRangeException If the task index is out of range.
     */
    private static void deleteTask(String userInput, int inputNum, ArrayList<Task> taskArray) throws EmptyTaskException, OutOfRangeException {
        if (userInput.equals("delete")) {
            throw new EmptyTaskException("delete");
        }
        String[] parts = userInput.split("\\s+");
        int taskIndex = Integer.parseInt(parts[1]) - 1;
        if (taskIndex >= inputNum || taskIndex < 0 || taskArray.get(taskIndex) == null) {
            throw new OutOfRangeException("Delete");
        }
        Task currentTask = taskArray.get(taskIndex);
        taskArray.remove(currentTask);
        System.out.println("    ____________________________________________________________\n" +
                "     Noted. I've removed this task:\n" +
                "       " + currentTask + "\n" +
                "     Now you have " + (inputNum - 1) + " task(s) in the list.\n" +
                "    ____________________________________________________________");
    }


}
