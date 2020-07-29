package red.jad.tiab.objects.items;

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
import red.jad.tiab.TIAB;
import red.jad.tiab.backend.TimeFormatter;
import red.jad.tiab.objects.entities.TickerEntity;

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

    private MutableText displayTime(World world, ItemStack stack, boolean isInfinite){
        return new TranslatableText("tooltip.tiab.time_in_a_bottle", isInfinite ? new TranslatableText("tooltip.tiab.time_in_a_bottle.infinity").getString() : TimeFormatter.ticksToTime(world.getTime() - getLastUsed(stack)));
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

                if(selected) player.sendMessage(displayTime(world, stack, player.isCreative()), true);

                // Difference of current time to last equipped timestamp added to last used. This makes the stored ticks stop counting when not on you.
                long time = world.getTime();
                if(TIAB.config.gameplay.update_frequency <= 0 || time % TIAB.config.gameplay.update_frequency == 0){
                    setLastUsed(stack, getLastUsed(stack) + (time - getLastEquipped(stack)));
                    if(getLastUsed(stack) <= 0) setLastUsed(stack, time);
                    setLastEquipped(stack, time + TIAB.config.gameplay.update_frequency);
                }
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
            if(player != null && (storedTicks >= TIAB.config.gameplay.acceleration_duration || player.isCreative())){
                if(!world.isClient){
                    // add said ticks to ticker
                    Optional<TickerEntity> tickers = world.getNonSpectatingEntities(TickerEntity.class, new Box(pos).shrink(0.2, 0.2, 0.2)).stream().findFirst();

                    boolean canAfford = false;
                    long cost = TIAB.config.gameplay.acceleration_duration;

                    if(!tickers.isPresent()){
                        TIAB.TICKER.spawn(world, null, null, null, pos, SpawnReason.TRIGGERED, false, false);
                        canAfford = true;
                    }else{
                        if(tickers.get().getLevel() < 20){
                            cost = (long)
                                    (TIAB.config.gameplay.acceleration_duration *
                                            (Math.pow(TIAB.config.gameplay.acceleration_base, tickers.get().getLevel()))
                                    );
                            if(storedTicks >= cost || player.isCreative()){
                                tickers.get().setLevel(tickers.get().getLevel() + 1);
                                tickers.get().age = 0;
                                canAfford = true;
                            }
                        }
                    }

                    // remove ticks from player
                    if(canAfford){
                        if(!player.isCreative()) setLastUsed(stack, getLastUsed(stack) + cost);

                        // fx
                        if(TIAB.config.effects.play_sounds){
                            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, TIAB.config.effects.volume, 1.5f);
                            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, TIAB.config.effects.volume / 2, 1.5f);
                        }
                    }else{
                        if(TIAB.config.effects.play_sounds) world.playSound(null, pos, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.BLOCKS, TIAB.config.effects.volume / 2, 0.5f);
                    }
                }else{
                    player.swingHand(context.getHand());
                }
            }

            return ActionResult.success(context.getWorld().isClient);
        }else{
            return super.useOnBlock(context);
        }
    }

    /*
        Client
     */
    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if(world != null && stack != null){
            /*
                Time is only correctly displayed when in the player inventory,
                workaround to avoid confusion for now.
                TODO (probably never): Not do this.
             */
            if(MinecraftClient.getInstance().currentScreen instanceof InventoryScreen){
                tooltip.add(displayTime(world, stack, false).formatted(Formatting.GRAY));
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
