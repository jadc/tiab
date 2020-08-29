package red.jad.tiab.backend;

import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import red.jad.tiab.Main;

import java.util.concurrent.TimeUnit;

/*
    Miscellaneous utility methods
 */
public class Helpers {

    // Constants
    public static final String AUTOCONFIG_MOD_ID = "autoconfig1u";
    public static final String CLOTHCONFIG_MOD_ID = "cloth-config2";

    // Methods
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

    public static boolean canRandomlyTick(BlockState target){
        return (Main.config.getAccelerateRandomly() && target.getBlock().hasRandomTicks(target));
    }

    public static boolean canTick(BlockState target){
        return (Main.config.getAccelerateBlockEntities() && target.getBlock().hasBlockEntity());
    }

    public static boolean isAcceleratable(BlockState target){
        return canTick(target) || canRandomlyTick(target);
    }

    public static void playSound(World world, BlockPos pos, SoundEvent sound, float pitch){
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, ((float)(Main.config.getVolume())) / 100, pitch);
    }
}
