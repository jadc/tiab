package red.jad.tiab.backend;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import red.jad.tiab.objects.items.TimeBottleItem;

//
/*
    Allows item use preceding other actions if TIAB
    Used with permission: https://gist.github.com/shartte/6a4e61a73ef7a243c615760d6368ca08
    God knows how this works... lol
 */
public class CrouchlessUseHandler {

    public static void init() {
        UseBlockCallback.EVENT.register(CrouchlessUseHandler::handleItemUse);
    }

    private static ActionResult handleItemUse(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        if (playerEntity.isSpectator()) return ActionResult.PASS;

        ItemStack itemStack = playerEntity.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (item instanceof TimeBottleItem) {
            ItemUsageContext context = new ItemUsageContext(playerEntity, hand, blockHitResult);
            TimeBottleItem bottle = (TimeBottleItem) item;
            return bottle.onItemUseFirst(context);
        }

        return ActionResult.PASS;
    }
}
