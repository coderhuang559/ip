package duke;

import exceptions.InvalidInputException;
import exceptions.EmptyTaskException;
import exceptions.EmptyDateException;
import exceptions.OutOfRangeException;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Parser {
    public static void parseInput(String userInput, TaskList taskList) {
            try {

                if (Objects.equals(userInput, "bye")) {
                    taskList.updateTaskFile();
                    Ui.printExitMessage();
                    System.exit(0);

                } else if (Objects.equals(userInput, "list")) {
                    System.out.println("Here are the tasks in your list:");
                    System.out.println(TaskList.listTasks());

                } else if (userInput.startsWith("mark")) {
                    taskList.markTask(userInput);
                    taskList.updateTaskFile();

                } else if (userInput.startsWith("unmark")) {
                    taskList.unmarkTask(userInput);
                    taskList.updateTaskFile();

                } else if (userInput.startsWith("todo")) {
                    if (userInput.equals("todo")) {
                        throw new EmptyTaskException("todo");
                    }
                    taskList.makeToDo(userInput);
                    taskList.updateTaskFile();

                } else if (userInput.startsWith("deadline")) {
                    if (userInput.equals("deadline")) {
                        throw new EmptyTaskException("deadline");
                    } else if (userInput.endsWith("/by")) {
                        throw new EmptyDateException("deadline");
                    }
                    taskList.makeDeadline(userInput);
                    taskList.updateTaskFile();

                } else if (userInput.startsWith("event")) {
                    if (userInput.equals("event")) {
                        throw new EmptyTaskException("event");
                    }
                    taskList.makeEvent(userInput);
                    taskList.updateTaskFile();

                } else if (userInput.startsWith("delete")) {
                    taskList.deleteTask(userInput);
                    taskList.updateTaskFile();
                }
                else {
                    throw new InvalidInputException("Invalid Input");
                }
            } catch (InvalidInputException | EmptyTaskException | EmptyDateException | OutOfRangeException |
                     IOException | DateTimeParseException e) {
                System.out.println(e);
            }
        }
    }
