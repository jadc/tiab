package red.jad.tiab.backend;

import net.minecraft.text.TranslatableText;

import java.util.concurrent.TimeUnit;

public class TimeFormatter {
    public static String ticksToTime(long ticks) {
        String delimiter = new TranslatableText("tooltip.tiab.time_in_a_bottle.delimiter").getString();

        long seconds = ticks / 20;
        long hours = TimeUnit.SECONDS.toHours(seconds);
        seconds -= TimeUnit.HOURS.toSeconds(hours);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        seconds -= TimeUnit.MINUTES.toSeconds(minutes);

        return  ( hours > 0 ? hours + delimiter : "" ) +
                ( minutes > 0 ? (minutes >= 10 ? minutes : (hours > 0 ? "0" : "") + minutes) + delimiter : "") +
                (seconds >= 10 ? seconds : "0" + seconds);
    }
}
