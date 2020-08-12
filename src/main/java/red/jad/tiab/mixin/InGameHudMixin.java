package red.jad.tiab.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jad.tiab.TIAB;
import red.jad.tiab.backend.Helpers;
import red.jad.tiab.client.TimeTooltip;
import red.jad.tiab.config.DefaultConfig;

/*
    Injects into HUD to display TIAB clock
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Inject(at = @At("HEAD"), method = "render")
    public void renderTIABTooltip(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if(TIAB.config.getDisplayWhen() != DefaultConfig.displayWhen.NEVER) TimeTooltip.render(matrices, this.scaledWidth, this.scaledHeight);
    }
}
