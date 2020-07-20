package red.jad.notimetotick.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jad.notimetotick.NTTT;
import red.jad.notimetotick.access.PlayerEntityAccess;
import red.jad.notimetotick.backend.Config;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityAccess {

    public long NTTTBottleLastEquipped;
    public long NTTTBottleLastUsed;

    @Inject(at = @At("TAIL"), method = "readCustomDataFromTag")
    public void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(NTTT.MOD_ID, 10)) {
            CompoundTag section = tag.getCompound(NTTT.MOD_ID);
            NTTTBottleLastEquipped = section.getLong("NTTTBottleLastEquipped");
            NTTTBottleLastUsed = section.getLong("NTTTBottleLastUsed");
        }
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToTag")
    public void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
        CompoundTag section = new CompoundTag();

        section.putLong("LastEquipped", NTTTBottleLastEquipped);
        section.putLong("LastUsed", NTTTBottleLastUsed);

        tag.put(NTTT.MOD_ID, section);
    }

    /*
    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci){
        if(!((PlayerEntity)(Object)this).world.isClient){
            if(((PlayerEntity)(Object)this).world.getTime() % Config.ticksPerSecond == 0){
                Inventory inv = ((PlayerEntity)(Object)this).inventory;
                boolean hasTimeBottle = false;
                for(int i = 0; i < inv.size(); i++){
                    if(inv.getStack(i).getItem() == NTTT.TIME_IN_A_BOTTLE){
                        hasTimeBottle = true;
                        break;
                    }
                }
                if(hasTimeBottle) NTTTStoredTicks += 20;
            }
        }
    }
    */

    @Override
    public void setNTTTBottleLastEquipped(long time){
        NTTTBottleLastEquipped = Math.max(time, 0);
    }

    @Override
    public long getNTTTBottleLastEquipped(){
        return NTTTBottleLastEquipped;
    }

    @Override
    public void setNTTTBottleLastUsed(long time){
        NTTTBottleLastUsed = Math.max(time, 0);
    }

    @Override
    public long getNTTTBottleLastUsed(){
        return NTTTBottleLastUsed;
    }
}
