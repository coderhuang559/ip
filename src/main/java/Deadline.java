import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {

    protected LocalDateTime by;

    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String getType() {
        return "D";
    }

    @Override
    public String getDescription() {
        String formattedBy = this.by.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
        return super.toString() + " | " + formattedBy ;
    }

    public String getByFormatted() {
        return this.by.format(DateTimeFormatter.ofPattern("d MMM yyyy h:mma"));
    }

    @Override
    public String statusAndTask() {
        return "[D]" + statusString() + " " + super.toString() + " (by: " + getByFormatted() + ")";
    }

}
