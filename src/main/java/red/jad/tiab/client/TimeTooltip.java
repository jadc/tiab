package red.jad.tiab.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import red.jad.tiab.TIAB;
import red.jad.tiab.backend.Helpers;
import red.jad.tiab.backend.TimeFormatter;
import red.jad.tiab.config.DefaultConfig;
import red.jad.tiab.objects.items.TimeBottleItem;

public class TimeTooltip {

    public static MutableText getText(World world, ItemStack stack, boolean isInfinite){
        return new TranslatableText("tooltip.tiab.time_in_a_bottle", isInfinite ? new TranslatableText("tooltip.tiab.time_in_a_bottle.infinity").getString() : new LiteralText(TimeFormatter.ticksToTime(world.getTime() - TimeBottleItem.getLastUsed(stack))));
    }

    @Environment(EnvType.CLIENT)
    public static void render(MatrixStack matrices, int screenWidth, int screenHeight){
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player != null && client.player.getMainHandStack() != null){
            if(client.player.getMainHandStack().getItem().equals(TIAB.TIME_IN_A_BOTTLE)){
                if(client.crosshairTarget instanceof BlockHitResult){
                    BlockHitResult blockHit = (BlockHitResult) client.crosshairTarget;
                    BlockState target = client.world.getBlockState(blockHit.getBlockPos());

                    if(TIAB.config.getDisplayWhen() == DefaultConfig.displayWhen.ALWAYS || Helpers.isAcceleratable(target)){
                        client.getProfiler().push(new Identifier(TIAB.MOD_ID, "time_tooltip").toString());

                        MutableText text = getText(client.world, client.player.getActiveItem(), client.player.isCreative());
                        int textWidth = client.inGameHud.getFontRenderer().getWidth(text);

                        float centeredX = (screenWidth - textWidth) / 2f;
                        float centeredY = screenHeight / 2f - 4;

                        client.inGameHud.getFontRenderer().drawWithShadow(matrices, text.getString(), centeredX, centeredY - TIAB.config.getVerticalOffset(), TIAB.config.getColor());

                        client.getProfiler().pop();
                    }
                }
            }
        }
    }

}
