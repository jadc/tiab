package red.jad.notimetotick.objects.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import red.jad.notimetotick.backend.Config;
import red.jad.notimetotick.backend.TimeFormatter;

import java.util.List;

public class TimeBottleItem extends Item {

    public TimeBottleItem() {
        super(new Item.Settings()
                .group(ItemGroup.MISC)
                .maxCount(1)
                .rarity(Rarity.RARE)
        );
    }

    /*
    NBT
     */
    private void setLastUsed(ItemStack stack, long last){
        CompoundTag tag = getOrCreateTag(stack);
        tag.putLong("lastUsed", last);
        stack.setTag(tag);
    }

    private long getLastUsed(ItemStack stack){
        CompoundTag tag = getOrCreateTag(stack);
        if(tag.contains("lastUsed")){
            return tag.getLong("lastUsed");
        }
        return 0;
    }

    private void setLastEquipped(ItemStack stack, long last){
        CompoundTag tag = getOrCreateTag(stack);
        tag.putLong("lastEquipped", last);
        stack.setTag(tag);
    }

    private long getLastEquipped(ItemStack stack){
        CompoundTag tag = getOrCreateTag(stack);
        if(tag.contains("lastEquipped")){
            return tag.getLong("lastEquipped");
        }
        return 0;
    }

    private CompoundTag getOrCreateTag(ItemStack stack){
        CompoundTag tag;
        if(stack.hasTag() && stack.getTag() != null){
            tag = stack.getTag();
        }else{
            tag = new CompoundTag();
        }
        return tag;
    }

    /*
    Tick storage
     */
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if(!world.isClient){
            if(entity instanceof PlayerEntity){
                PlayerEntity player = (PlayerEntity) entity;

                long time = world.getTime();
                if(time % (20 * Config.secondsUntilUpdate) == 0){
                    setLastUsed(stack, getLastUsed(stack) + (time - getLastEquipped(stack)));
                    if(getLastUsed(stack) <= 0) setLastUsed(stack, time);
                    setLastEquipped(stack, time + (20 * Config.secondsUntilUpdate));
                }

                if(selected){
                    String hud = "∞";
                    if(!player.isCreative()) hud = TimeFormatter.ticksToTime(world.getTime() - getLastUsed(stack));
                    player.sendMessage(new LiteralText(hud), true);
                }


                /*
                boolean hasTimeBottle = false;
                for(int i = 0; i < player.inventory.size(); i++){
                    if(player.inventory.getStack(i).getItem() == this){
                        hasTimeBottle = true;
                        break;
                    }
                }
                */
                //if(hasTimeBottle){
                /*
                    if(world.getTime() % 20 == 0){

                        long diff = world.getTime() - ((PlayerEntityAccess)player).getNTTTBottleLastEquipped();
                        ((PlayerEntityAccess)player).setNTTTBottleLastUsed(((PlayerEntityAccess)player).getNTTTBottleLastUsed() + diff);

                        // Initialize
                        if(((PlayerEntityAccess)player).getNTTTBottleLastUsed() == 0){
                            ((PlayerEntityAccess)player).setNTTTBottleLastUsed(world.getTime());
                        }

                        ((PlayerEntityAccess)player).setNTTTBottleLastEquipped(world.getTime() + 20);
                        // HUD
                        if(selected){
                            String hud = "∞";
                            if(!player.isCreative()) hud = TimeFormatter.ticksToTime(getStoredTicks(player));
                            player.sendMessage(new LiteralText(hud), true);
                        }
                    }
                    */
                //}

            }
        }
    }

    /*

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if(state.getBlock().hasRandomTicks(state) || state.getBlock().hasBlockEntity()){
            if(player != null && (getStoredTicks(player) >= Config.baseDuration*20 || player.isCreative()){
                if(!world.isClient){

                    // add said ticks to ticker
                    Optional<TickerEntity> tickers = world.getNonSpectatingEntities(TickerEntity.class, new Box(pos).shrink(0.2, 0.2, 0.2)).stream().findFirst();

                    boolean canAfford = false;
                    long cost = Config.baseDuration;

                    if(!tickers.isPresent()){
                        NTTT.TICKER.spawn(world, null, null, null, pos, SpawnReason.TRIGGERED, false, false);
                        canAfford = true;
                    }else{
                        cost = (long) (Config.baseDuration * Math.pow(Config.multiplier, tickers.get().getLevel() + 1));
                        if(getStoredTicks(player) >= cost*20 || player.isCreative()){
                            tickers.get().setLevel(tickers.get().getLevel() + 1);
                            tickers.get().age = 0;
                            canAfford = true;
                        }
                    }

                    // remove ticks from player
                    if(canAfford){
                        if(!player.isCreative()) ((PlayerEntityAccess)player).setNTTTBottleLastUsed(((PlayerEntityAccess)player).getNTTTBottleLastUsed() + cost*20);

                        // sfx
                        world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1f, 1f);
                    }
                }else{
                    player.swingHand(context.getHand());
                }
            }
        }
        return ActionResult.CONSUME;
    }

    */

    /*
    Client
     */

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if(world != null && stack != null){
            tooltip.add(new LiteralText(
                    "⌛ " + TimeFormatter.ticksToTime(world.getTime() - getLastUsed(stack))
            ).formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
