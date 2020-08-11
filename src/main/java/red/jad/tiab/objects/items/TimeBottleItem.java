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
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import red.jad.tiab.TIAB;
import red.jad.tiab.backend.Helpers;
import red.jad.tiab.client.TimeTooltip;
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

    public static long getLastUsed(ItemStack stack){
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

    private static CompoundTag getOrCreateTag(ItemStack stack){
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

        if(entity instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) entity;
            long time = world.getTime();
            if(!world.isClient){
                if(TIAB.config.getUpdateFrequency() <= 1 || time % TIAB.config.getUpdateFrequency() == 0){
                    // Only allow largest bottle to accumulate time
                    if(TIAB.config.getOneBottleAtATime()){
                        for(int i = 0; i < player.inventory.size(); i++){
                            ItemStack other = player.inventory.getStack(i);
                            if(other.getItem() == this && other != stack){
                                // if other has less stored ticks
                                if(getLastUsed(other) > getLastUsed(stack)){
                                    setLastUsed(other, getLastUsed(other) + TIAB.config.getUpdateFrequency());
                                }
                            }
                        }
                    }

                    // Difference of current time to last equipped timestamp added to last used. This makes the stored ticks stop counting when not on you.
                    setLastUsed(stack, getLastUsed(stack) + (time - getLastEquipped(stack)));
                    if(getLastUsed(stack) <= 0) setLastUsed(stack, time);
                    setLastEquipped(stack, time + TIAB.config.getUpdateFrequency());
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
            if(Helpers.isAcceleratable(state)){

                if(!world.isClient()){
                    boolean valid = false;
                    double baseCost = player.isCreative() ? 0 : TIAB.config.getAccelerationDuration();
                    double cost = baseCost;
                    long storedTicks = world.getTime() - getLastUsed(stack);

                    Optional<TickerEntity> tickersInBlock = world.getNonSpectatingEntities(TickerEntity.class, new Box(pos).shrink(0.2, 0.2, 0.2)).stream().findFirst();

                    if(!tickersInBlock.isPresent()){
                        TIAB.TICKER.spawn((ServerWorld) world, null, null, null, pos, SpawnReason.TRIGGERED, false, false);
                        valid = true;
                    }else{
                        TickerEntity ticker = tickersInBlock.get();
                        if(ticker.getLevel() < TIAB.config.getMaxLevel()) {
                            cost = baseCost * Math.pow(TIAB.config.getAccelerationBase(), ticker.getLevel());
                            if (storedTicks >= cost) {
                                ticker.setLevel(ticker.getLevel() + 1);
                                ticker.age = 0;
                                valid = true;
                            }
                        }
                    }

                    if(valid){
                        setLastUsed(stack, getLastUsed(stack) + (long)cost);
                        if(TIAB.config.getVolume() > 0){
                            Helpers.playSound(world, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.5f);
                            Helpers.playSound(world, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, 1.5f);

                            //world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, TIAB.oldConfig.effects.volume, 1.5f);
                            //world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, TIAB.oldConfig.effects.volume / 2, 1.5f);
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
                tooltip.add(TimeTooltip.getText(world, stack, false).formatted(Formatting.GRAY));
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
