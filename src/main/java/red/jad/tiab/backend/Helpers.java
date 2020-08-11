package red.jad.tiab.backend;

import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import red.jad.tiab.TIAB;

public class Helpers {

    // Constants
    public static final String AUTOCONFIG_MOD_ID = "autoconfig1u";
    public static final String MODMENU_MOD_ID = "modmenu";
    public static final String CLOTHCONFIG_MOD_ID = "cloth-config2";

    public static boolean canRandomlyTick(BlockState target){
        return (TIAB.config.getAccelerateRandomly() && target.getBlock().hasRandomTicks(target));
    }

    public static boolean canTick(BlockState target){
        return (TIAB.config.getAccelerateBlockEntities() && target.getBlock().hasBlockEntity());
    }

    public static boolean isAcceleratable(BlockState target){
        return canTick(target) || canRandomlyTick(target);
    }

    public static void playSound(World world, BlockPos pos, SoundEvent sound, float pitch){
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, ((float)(TIAB.config.getVolume())) / 100, pitch);
    }
}
