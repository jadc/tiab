package red.jad.tiab.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(InGameOverlayRenderer.class)
public class TIABHudMixin {
    /**
     * @author j
     */
    @Overwrite
    public static void renderOverlays(MinecraftClient minecraftClient, MatrixStack matrixStack) {
        System.out.println("Overlay cancelled");
    }
}
