package red.jad.notimetotick.objects.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import red.jad.notimetotick.TIAB;
import red.jad.notimetotick.backend.Config;
import red.jad.notimetotick.backend.TimeFormatter;
import red.jad.notimetotick.objects.entities.TickerEntity;

import java.util.List;
import java.util.Optional;

public class TimeBottleItem extends Item {

    public TimeBottleItem() {
        super(new Item.Settings()
                .group(ItemGroup.MISC)
                .maxCount(1)
                .rarity(Rarity.RARE)
        );
    }

    /*
    NBT and misc exclusive methods
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

    private MutableText displayTime(PlayerEntity player, World world, ItemStack stack){
        return new TranslatableText("tooltip.notimetotick.time_in_a_bottle", player.isCreative() ? new TranslatableText("tooltip.notimetotick.time_in_a_bottle.infinity").getString() : TimeFormatter.ticksToTime(world.getTime() - getLastUsed(stack)));
    }

    private MutableText displayTime(World world, ItemStack stack){
        return new TranslatableText("tooltip.notimetotick.time_in_a_bottle", TimeFormatter.ticksToTime( world.getTime() - getLastUsed(stack) ));
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

                // Difference of current time to last equipped timestamp added to last used. This makes the stored ticks stop counting when not on you.
                long time = world.getTime();
                if(time % (20 * Config.secondsUntilUpdate) == 0){
                    setLastUsed(stack, getLastUsed(stack) + (time - getLastEquipped(stack)));
                    if(getLastUsed(stack) <= 0) setLastUsed(stack, time);
                    setLastEquipped(stack, time + (20 * Config.secondsUntilUpdate));
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

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        ItemStack stack = context.getStack();
        BlockState state = world.getBlockState(pos);

        if(state.getBlock().hasRandomTicks(state) || state.getBlock().hasBlockEntity()){
            long storedTicks = world.getTime() - getLastUsed(stack);
            if(player != null && (storedTicks >= Config.baseDuration*20 || player.isCreative())){
                if(!world.isClient){
                    // add said ticks to ticker
                    Optional<TickerEntity> tickers = world.getNonSpectatingEntities(TickerEntity.class, new Box(pos).shrink(0.2, 0.2, 0.2)).stream().findFirst();

                    boolean canAfford = false;
                    long cost = Config.baseDuration;

                    if(!tickers.isPresent()){
                        TIAB.TICKER.spawn(world, null, null, null, pos, SpawnReason.TRIGGERED, false, false);
                        canAfford = true;
                    }else{
                        cost = (long) (Config.baseDuration * Math.pow(Config.multiplier, tickers.get().getLevel() + 1));
                        if(storedTicks >= cost*20 || player.isCreative()){
                            tickers.get().setLevel(tickers.get().getLevel() + 1);
                            tickers.get().age = 0;
                            canAfford = true;
                        }
                    }

                    // remove ticks from player
                    if(canAfford){
                        if(!player.isCreative()) setLastUsed(stack, getLastUsed(stack) + cost*20);
                        player.sendMessage(displayTime(player, world, stack), true);

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

    /*
    Client
     */

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if(world != null && stack != null){
            // Time is only correctly displayed when in the player inventory.
            // Workaround to avoid confusion for now
            if(MinecraftClient.getInstance().currentScreen instanceof InventoryScreen){
                tooltip.add(displayTime(world, stack).formatted(Formatting.GRAY));
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
