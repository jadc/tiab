package red.jad.notimetotick.objects.items;

import jdk.internal.jline.internal.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.java.games.input.Component;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import red.jad.notimetotick.NTTT;
import red.jad.notimetotick.access.PlayerEntityAccess;
import red.jad.notimetotick.backend.Config;
import red.jad.notimetotick.objects.entities.TickerEntity;

import java.util.List;

public class TimeBottleItem extends Item {

    private final String tagKey = "storedTicks";
    private final String tagStoringKey = "storing";

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

        if(entity instanceof PlayerEntity){

            PlayerEntity player = (PlayerEntity) entity;
            if(selected) player.sendMessage(new LiteralText(ticksToTime(getStoredTicks(stack))), true);

            if(world.getTime() % Config.ticksPerSecond == 0 || Config.ticksPerSecond == 0){
                if(getIsStoring(stack)) setStoredTicks(stack, getStoredTicks(stack) + 20);

                // Prevent more than one time bottle from accumulating time
                for(int i = 0; i < player.inventory.size(); i++){
                    ItemStack otherStack = player.inventory.getStack(i);
                    if(otherStack != stack && otherStack.getItem() == this){
                        int legalTime = getStoredTicks(stack);
                        int illegalTime = getStoredTicks(otherStack);

                        if(legalTime >= illegalTime) setIsStoring(otherStack, false);
                    }
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.sendMessage(new LiteralText(((PlayerEntityAccess)user).getStoredTicks() + ""), false);
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        ItemStack stack = context.getStack();

        //if(state.getBlock().hasRandomTicks(state) || state.getBlock().hasBlockEntity()){
            if((this.getStoredTicks(stack)) > Config.tickingDuration*20){
                if(!world.isClient){
                    TickerEntity ticker = new TickerEntity(NTTT.TICKER, world);
                    ticker.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    world.spawnEntity(ticker);
                    this.setStoredTicks(stack, this.getStoredTicks(stack) - Config.tickingDuration*20);
                    if (player != null) player.getItemCooldownManager().set(this, 10);
                }else{
                    if (player != null) player.swingHand(context.getHand());
                }
            }

        //}
        return ActionResult.PASS;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if(stack.hasTag() && stack.getTag() != null){
            CompoundTag tag = stack.getTag();
            if(tag.contains(tagKey)){
                Formatting color = getIsStoring(stack) ? Formatting.GRAY : Formatting.DARK_GRAY;
                tooltip.add(new LiteralText(ticksToTime(this.getStoredTicks(stack))).formatted(color));
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    /*
        Time in a Bottle Specific Methods
         */
    public void setStoredTicks(ItemStack stack, int time){
        CompoundTag tag;
        if(stack.hasTag() && stack.getTag() != null){
            tag = stack.getTag();
        }else{
            tag = new CompoundTag();
        }

        tag.putInt(tagKey, time);

        stack.setTag(tag);
    }

    public int getStoredTicks(ItemStack stack){
        CompoundTag tag;
        if(stack.hasTag() && stack.getTag() != null){
            tag = stack.getTag();
            if(tag.contains(tagKey)){
                return tag.getInt(tagKey);
            }
        }
        return 0;
    }

    public void setIsStoring(ItemStack stack, boolean isStoring){
        CompoundTag tag;
        if(stack.hasTag() && stack.getTag() != null){
            tag = stack.getTag();
        }else{
            tag = new CompoundTag();
        }

        tag.putBoolean(tagStoringKey, isStoring);

        stack.setTag(tag);
    }

    public boolean getIsStoring(ItemStack stack){
        CompoundTag tag;
        if(stack.hasTag() && stack.getTag() != null){
            tag = stack.getTag();
            if(tag.contains(tagStoringKey)){
                return tag.getBoolean(tagStoringKey);
            }
        }
        return false;
    }

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
