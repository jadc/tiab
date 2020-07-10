package red.jad.notimetotick.objects.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import red.jad.notimetotick.NTTT;
import red.jad.notimetotick.access.PlayerEntityAccess;
import red.jad.notimetotick.backend.Config;
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
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if(!world.isClient){
            if(entity instanceof PlayerEntity){
                PlayerEntity player = (PlayerEntity) entity;

                // HUD
                if(selected) player.sendMessage(new LiteralText(ticksToTime(((PlayerEntityAccess)player).getStoredTicks())), true);

                // Time accumulation
                if(player.world.getTime() % Config.ticksPerSecond == 0 || Config.ticksPerSecond == 0){
                    Inventory inv = player.inventory;
                    boolean hasTimeBottle = false;
                    for(int i = 0; i < inv.size(); i++){
                        if(inv.getStack(i).getItem() == this){
                            hasTimeBottle = true;
                            break;
                        }
                    }
                    if(hasTimeBottle) ((PlayerEntityAccess)player).setStoredTicks(((PlayerEntityAccess)player).getStoredTicks() + 20);
                }
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if(state.getBlock().hasRandomTicks(state) || state.getBlock().hasBlockEntity()){
            if(player != null && (((PlayerEntityAccess)player).getStoredTicks() ) >= Config.baseDuration*20){
                if(!world.isClient){
                    // remove ticks from player
                    ((PlayerEntityAccess)player).setStoredTicks(((PlayerEntityAccess)player).getStoredTicks() - Config.baseDuration*20);

                    // add said ticks to ticker
                    TickerEntity ticker = new TickerEntity(NTTT.TICKER, world);
                    ticker.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    world.spawnEntity(ticker);

                    player.getItemCooldownManager().set(this, 10);
                }else{
                    player.swingHand(context.getHand());
                }
            }

        }
        return ActionResult.PASS;
    }

    // Time in a Bottle Specific Methods

    public String ticksToTime(int ticks) {
        String delimiter = ":";

        int hours = ((ticks / 20) / 60) / 60;
        int minutes = ((ticks / 20) / 60) % 60;
        int seconds = (ticks / 20) % 60;

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
