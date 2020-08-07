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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
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
                .group(ItemGroup.TOOLS)
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
        return new TranslatableText("tooltip.tiab.time_in_a_bottle", isInfinite ? new TranslatableText("tooltip.tiab.time_in_a_bottle.infinity").getString() : new LiteralText(TimeFormatter.ticksToTime(world.getTime() - getLastUsed(stack))));
    }

    /*
    Tick storage
     */
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if(entity instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) entity;
            long time = world.getTime();
            if(world.isClient){
                if(TIAB.config.effects.hud && selected){
                    if(TIAB.config.gameplay.update_frequency <= 1 || time % TIAB.config.gameplay.update_frequency == 0){
                        player.sendMessage(displayTime(world, stack, player.isCreative()), true);
                    }
                }
            }else{
                if(TIAB.config.gameplay.update_frequency <= 1 || time % TIAB.config.gameplay.update_frequency == 0){
                    // Only allow largest bottle to accumulate time
                    if(TIAB.config.gameplay.one_bottle_at_a_time){
                        for(int i = 0; i < player.inventory.size(); i++){
                            ItemStack other = player.inventory.getStack(i);
                            if(other.getItem() == this && other != stack){
                                // if other has less stored ticks
                                if(getLastUsed(other) > getLastUsed(stack)){
                                    setLastUsed(other, getLastUsed(other) + TIAB.config.gameplay.update_frequency);
                                }
                            }
                        }
                    }

                    // Difference of current time to last equipped timestamp added to last used. This makes the stored ticks stop counting when not on you.
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

        if(player != null){
            if((TIAB.config.gameplay.accelerate_randomly && state.getBlock().hasRandomTicks(state))
                    || (TIAB.config.gameplay.accelerate_block_entities && state.getBlock().hasBlockEntity())){

                if(!world.isClient()){
                    boolean valid = false;
                    double baseCost = player.isCreative() ? 0 : TIAB.config.gameplay.acceleration_duration;
                    double cost = baseCost;
                    long storedTicks = world.getTime() - getLastUsed(stack);

                    Optional<TickerEntity> tickersInBlock = world.getNonSpectatingEntities(TickerEntity.class, new Box(pos).shrink(0.2, 0.2, 0.2)).stream().findFirst();

                    if(!tickersInBlock.isPresent()){
                        TIAB.TICKER.spawn((ServerWorld) world, null, null, null, pos, SpawnReason.TRIGGERED, false, false);
                        valid = true;
                    }else{
                        TickerEntity ticker = tickersInBlock.get();
                        if(ticker.getLevel() < TIAB.config.gameplay.max_level) {
                            cost = baseCost * Math.pow(TIAB.config.gameplay.acceleration_base, ticker.getLevel());
                            if (storedTicks >= cost) {
                                ticker.setLevel(ticker.getLevel() + 1);
                                ticker.age = 0;
                                valid = true;
                            }
                        }
                    }

                    if(valid){
                        setLastUsed(stack, getLastUsed(stack) + (long)cost);
                        if(TIAB.config.effects.play_sounds){
                            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, TIAB.config.effects.volume, 1.5f);
                            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, TIAB.config.effects.volume / 2, 1.5f);
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }

        return super.useOnBlock(context);
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
