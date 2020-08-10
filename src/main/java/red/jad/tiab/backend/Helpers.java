package red.jad.tiab.backend;

import net.minecraft.block.BlockState;
import red.jad.tiab.TIAB;

public class Helpers {

    public static boolean canRandomlyTick(BlockState target){
        return (TIAB.oldConfig.gameplay.accelerate_randomly && target.getBlock().hasRandomTicks(target));
    }

    public static boolean canTick(BlockState target){
        return (TIAB.oldConfig.gameplay.accelerate_block_entities && target.getBlock().hasBlockEntity());
    }

    public static boolean isAcceleratable(BlockState target){
        return canTick(target) || canRandomlyTick(target);
    }
}
