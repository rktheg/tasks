package se.edu.streamdemo.data;

import se.edu.streamdemo.task.Deadline;
import se.edu.streamdemo.task.Event;
import se.edu.streamdemo.task.Task;
import se.edu.streamdemo.task.Todo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

public class Datamanager {
    // Extracted constants to eliminate magic literalx
    public static final String TODO = "T";
    public static final String DEADLINE = "D";
    public static final String EVENT = "E";
    private File dataFile;

    public File getDataFile() {
        return dataFile;
    }

    public Datamanager(String fileName) {
        dataFile = new File(fileName);
    }

    public void createFile() {
        try {
            if (dataFile.exists()) {
                System.out.println("file exists");
                return;
            }
            if (!dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }
            dataFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Cannot create file; reason: " + e.getMessage());
        }
    }

    private ArrayList readFile() throws IOException {
        // Guard clause
        if (!dataFile.exists()) {
            throw new FileNotFoundException();
        }
        // Guard clause
        if (dataFile.length() == 0) {
            System.out.println("empty file");
            throw new IOException();
        }
        // Happy path
        ArrayList<String> dataItems = (ArrayList) Files.readAllLines(dataFile.toPath(), Charset.defaultCharset());
        //default charset is if its in a different language
        return dataItems;
    }

    public ArrayList<Task> loadData() {
        ArrayList<Task> taskList = null;
        try {
            ArrayList<String> dataItems = readFile();
            taskList = parse(dataItems);
        } catch (IOException e) {
            System.out.println("Cannot load data; reason: " + e.getMessage());
            e.printStackTrace(); // Printing stack trace is not the most useful thing to do! You can log it if you wish
        }
        return taskList;
    }

    private ArrayList<Task> parse(ArrayList<String> dataItems) {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (String line : dataItems) {
            String taskDescription = getTaskDescription(line);
            String taskType = getTaskType(line);
            switch (taskType) {
            case TODO:
                Todo todo = new Todo(taskDescription);
                allTasks.add(todo);
                break;
            case DEADLINE:
                Deadline deadline = new Deadline(taskDescription);
                allTasks.add(deadline);
                break;
            case EVENT:
                Event event = new Event(taskDescription);
                allTasks.add(event);
                break;
            default:
                System.out.println("Unknown task encountered. Skipping");
                break;
            }
        }
        return allTasks;
    }

    /**
     * Returns task type
     * Note: Extracted string replacement to ensure better SLAP in the caller method
     * @param inputLine
     */
    private static String getTaskType(String inputLine) {
        String taskType = inputLine.substring(0, 2);
        taskType = taskType.replace("[", "");
        taskType = taskType.replace("]", "");
        return taskType;
    }

    /**
     * Returns task description
     * Note: Extracted substringing to ensure better SLAP in the caller method
     * @param inputLine
     */
    private static String getTaskDescription(String inputLine) {
        String taskDescription = inputLine.substring(4);
        return taskDescription;
    }
}