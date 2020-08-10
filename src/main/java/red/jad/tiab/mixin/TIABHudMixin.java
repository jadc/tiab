package red.jad.tiab.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jad.tiab.TIAB;
import red.jad.tiab.client.TimeTooltip;
import red.jad.tiab.config.Config;

@Mixin(InGameHud.class)
public class TIABHudMixin {
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Inject(at = @At("HEAD"), method = "render")
    public void renderTIABTooltip(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if(TIAB.config.client.hud.display_when != Config.Client.HUDConfig.displayWhen.NEVER) TimeTooltip.render(matrices, this.scaledWidth, this.scaledHeight);
    }
}
