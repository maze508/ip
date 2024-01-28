package duke;
public class DukeException extends Exception {
    /**
     * Constructs a DukeException with the specified error message.
     *
     * @param errorMessage The error message associated with the exception.
     */
    public DukeException(String errorMessage) {
        super("!!! " + errorMessage + " !!!");
    }
}