package red.jad.tiab.backend;

import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import red.jad.tiab.TIAB;

public class Helpers {

    public static boolean canRandomlyTick(BlockState target){
        return (TIAB.config.gameplay.accelerate_randomly && target.getBlock().hasRandomTicks(target));
    }

    public static boolean canTick(BlockState target){
        return (TIAB.config.gameplay.accelerate_block_entities && target.getBlock().hasBlockEntity());
    }

    public static boolean isAcceleratable(BlockState target){
        return canTick(target) || canRandomlyTick(target);
    }

    public static void playSound(World world, BlockPos pos, SoundEvent sound){
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, ((float)(TIAB.config.client.volume)) / 100, 0.1f);
    }
}
