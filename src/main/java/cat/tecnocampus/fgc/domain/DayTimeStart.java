package cat.tecnocampus.fgc.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class DayTimeStart {

    private long id;

    @NotNull(message = "Start time cannot be null")
    @Pattern(regexp = "^([0,1][0-9]|2[0-3]):[0-5][0-9]$", message = "Start time must be hh:mm")
    private String timeStart;

    @Pattern(regexp = "^Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday$", message = "Must be a day of week")
    private String dayOfWeek;

    public DayTimeStart() {}

    public DayTimeStart(String day, String time) {
        dayOfWeek = day;
        timeStart = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayTimeStart that = (DayTimeStart) o;

        if (timeStart != null ? !timeStart.equals(that.timeStart) : that.timeStart != null) return false;
        return dayOfWeek != null ? dayOfWeek.equals(that.dayOfWeek) : that.dayOfWeek == null;
    }

    @Override
    public int hashCode() {
        int result = timeStart != null ? timeStart.hashCode() : 0;
        result = 31 * result + (dayOfWeek != null ? dayOfWeek.hashCode() : 0);
        return result;
    }
}
