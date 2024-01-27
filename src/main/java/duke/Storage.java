package duke;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Storage {
    private String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    public ArrayList<Task> load() throws DukeException {
        ensureDirectoryExists();
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(this.filePath);

        // Create the file if it does not exist
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new DukeException("Error Creating File: " + e.getMessage());
            }
        }

        // Load tasks from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = parseLineToTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new DukeException("Error Loading Tasks: " + e.getMessage());
        }
        return tasks;
    }

    public void save(ArrayList<Task> tasks) throws DukeException {
        ensureDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filePath))) {
            for (Task task : tasks) {
                writer.write(task.saveData());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DukeException("Error Storing Tasks: " + e.getMessage());
        }
    }

    private void ensureDirectoryExists() throws DukeException {
        try {
            File file = new File(this.filePath);
            if (!file.exists()) {
                Files.createDirectories(Paths.get(this.filePath).getParent());
            }
        } catch (IOException e) {
            throw new DukeException("Error Creating Directory: " + e.getMessage());
        }
    }

    private static Task parseLineToTask(String line) throws DukeException {
        // Assuming the format is "T | 1 | read book"
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            throw new DukeException("Invalid task format in file");
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        switch (type) {
            case "T":
                Todo todo = new Todo(description);
                if (isDone) todo.markAsDone();
                return todo;
            case "D":
                if (parts.length < 4) {
                    throw new DukeException("Invalid deadline format in file");
                }
                LocalDate byDate = LocalDate.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE);
                Deadline deadline = new Deadline(description, byDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                if (isDone) deadline.markAsDone();
                return deadline;
            case "E":
                if (parts.length < 5) {
                    throw new DukeException("Invalid event format in file");
                }
                LocalDate fromDate = LocalDate.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate toDate = LocalDate.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE);
                Event event = new Event(description, fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                if (isDone) event.markAsDone();
                return event;
            default:
                throw new DukeException("Unknown task type: " + type);
        }
    }
}