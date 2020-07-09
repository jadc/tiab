package red.jad.notimetotick.objects.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import red.jad.notimetotick.NTTT;
import red.jad.notimetotick.objects.entities.TickerEntity;

public class TimeBottleItem extends Item {
    public TimeBottleItem() {
        super(new Item.Settings()
                .group(ItemGroup.MISC)
                .maxCount(1)
                .rarity(Rarity.RARE)
        );
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if(state.getBlock().hasRandomTicks(state) || state.getBlock().hasBlockEntity()){
            TickerEntity ticker = new TickerEntity(NTTT.TICKER, world);
            ticker.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            world.spawnEntity(ticker);
            if (player != null) player.swingHand(context.getHand());
        }
        return ActionResult.CONSUME;
    }
}
