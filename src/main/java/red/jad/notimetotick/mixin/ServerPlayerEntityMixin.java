package red.jad.notimetotick.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jad.notimetotick.access.PlayerEntityAccess;

/*
Injection into ServerPlayerEntity in order to allow custom nbt to persist after death
 */

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(at = @At("TAIL"), method = "copyFrom")
    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ( (PlayerEntityAccess)( (PlayerEntity)(Object)this ) ).setNTTTBottleLastEquipped(((PlayerEntityAccess)(oldPlayer)).getNTTTBottleLastEquipped());
        ( (PlayerEntityAccess)( (PlayerEntity)(Object)this ) ).setNTTTBottleLastUsed(((PlayerEntityAccess)(oldPlayer)).getNTTTBottleLastUsed());
    }
}
