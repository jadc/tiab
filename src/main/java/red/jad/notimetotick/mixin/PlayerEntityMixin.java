package red.jad.notimetotick.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jad.notimetotick.NTTT;
import red.jad.notimetotick.access.PlayerEntityAccess;
import red.jad.notimetotick.backend.Config;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityAccess {

    public int NTTTStoredTicks = 0;

    @Inject(at = @At("TAIL"), method = "readCustomDataFromTag")
    public void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
        NTTTStoredTicks = tag.getInt("NTTTStoredTicks");
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToTag")
    public void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("NTTTStoredTicks", NTTTStoredTicks);
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
    public void setStoredTicks(int time){
        NTTTStoredTicks = Math.max(time, 0);
    }

    @Override
    public int getStoredTicks(){
        return NTTTStoredTicks;
    }
}
