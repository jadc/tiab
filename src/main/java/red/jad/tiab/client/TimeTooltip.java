package red.jad.tiab.client;

import com.google.gson.internal.$Gson$Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import red.jad.tiab.Main;
import red.jad.tiab.backend.Helpers;
import red.jad.tiab.mixin.InGameHudAccessor;
import red.jad.tiab.config.DefaultConfig;
import red.jad.tiab.objects.items.TimeBottleItem;

/*
    Clock HUD that appears when holding the TIAB
 */
public class TimeTooltip {

    private static long time = 0; // To prevent incorrect time when transitioning after deselect (since ItemStack != TIAB)
    @Environment(EnvType.CLIENT)
    public static MutableText getText(World world, ItemStack stack, boolean isInfinite){
        if(stack.getItem().equals(Main.TIME_IN_A_BOTTLE)) time = world.getTime() - TimeBottleItem.getLastUsed(stack);
        return new TranslatableText("tooltip.tiab.time_in_a_bottle", isInfinite ? new TranslatableText("tooltip.tiab.time_in_a_bottle.infinity").getString() : new LiteralText(Helpers.ticksToTime(time)));
    }

    private static final float fade_length = 2.0f;
    private static final float offset_distance = -2.0f;

    private static float fade = 0;
    private static float offset = offset_distance;
    private static float now = 0;

    @Environment(EnvType.CLIENT)
    private static boolean shouldRender(MinecraftClient client){
        if(Main.config.getDisplayWhen() == DefaultConfig.displayWhen.NEVER) return false;
        if(client.player == null) return false;
        if(client.player.getMainHandStack() == null) return false;

        // TODO: Allow offhand
        boolean holding = client.player.getMainHandStack().getItem().equals(Main.TIME_IN_A_BOTTLE);

        if(Main.config.getDisplayWhen() == DefaultConfig.displayWhen.HOVER){
            if(client.crosshairTarget instanceof BlockHitResult){
                BlockHitResult blockHit = (BlockHitResult) client.crosshairTarget;
                BlockState target = client.world.getBlockState(blockHit.getBlockPos());
                return holding && Helpers.isAcceleratable(target);
            }
        }

        return holding;
    }

    @Environment(EnvType.CLIENT)
    public static void render(MatrixStack matrices, float delta){
        MinecraftClient client = MinecraftClient.getInstance();

        float before = now;
        now = MinecraftClient.getInstance().world.getTime() + delta;
        float td = (now - before) * Main.config.getSpeed();

        int direction = shouldRender(client) ? 1 : -1;

        fade += td * direction;
        fade = Math.max(Math.min(fade, 10.0f), 0);
        offset += (td/4) * direction;
        offset = Math.max(Math.min(offset, 0), offset_distance);

        if(fade > 0){
            client.getProfiler().push(Main.id("time_tooltip").toString());

            MutableText text = getText(client.world, client.player.getMainHandStack(), client.player.isCreative());
            matrices.push();
            matrices.translate(0, -offset, 0);
            renderText(client.inGameHud, matrices, text.getString());
            matrices.pop();

            client.getProfiler().pop();
        }


    }

    @Environment(EnvType.CLIENT)
    public static void renderText(InGameHud hud, MatrixStack matrices, String text){
        int textWidth = hud.getFontRenderer().getWidth(text);
        float centeredX = (((InGameHudAccessor)(Object)hud).scaledWidth() - textWidth) / 2f;
        float centeredY = ((InGameHudAccessor)(Object)hud).scaledHeight() / 2f - 4;

        int o = (int)(fade * 256.0f / 10.0f);
        o = Math.max(Math.min(o, 255), 5); // for some reason, 5 and below renders solid TODO: a less symptom treating fix
        if(o > 5){
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            if(Main.config.getShadow()){
                hud.getFontRenderer().drawWithShadow(matrices, text, centeredX, centeredY - Main.config.getVerticalOffset(), Main.config.getColor() + (o << 24));
            }else{
                hud.getFontRenderer().draw(matrices, text, centeredX, centeredY - Main.config.getVerticalOffset(), Main.config.getColor() + (o << 24));
            }

            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        }
    }

}
