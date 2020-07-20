package red.jad.notimetotick.backend;

public class TimeFormatter {
    public static String ticksToTime(long ticks) {
        String delimiter = ":";

        long hours = ((ticks / 20) / 60) / 60;
        long minutes = ((ticks / 20) / 60) % 60;
        long seconds = (ticks / 20) % 60;

        String hoursLZ, minutesLZ, secondsLZ;

        hoursLZ = "" + hours;
        minutesLZ = ((hours > 0 && minutes < 10) ? "0" : "") + minutes;
        secondsLZ = ((seconds < 10 && minutes > 0) ? "0" : "") + seconds;

        if (ticks > (20 * 60 * 60))
            return hoursLZ + delimiter + minutesLZ + delimiter + secondsLZ;
        if (ticks > (20 * 60))
            return minutesLZ + delimiter + secondsLZ;
        return secondsLZ;
    }
}
