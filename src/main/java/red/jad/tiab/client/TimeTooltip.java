package red.jad.tiab.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import red.jad.tiab.TIAB;
import red.jad.tiab.backend.TimeFormatter;
import red.jad.tiab.objects.items.TimeBottleItem;

public class TimeTooltip {

    public static MutableText getText(World world, ItemStack stack, boolean isInfinite){
        return new TranslatableText("tooltip.tiab.time_in_a_bottle", isInfinite ? new TranslatableText("tooltip.tiab.time_in_a_bottle.infinity").getString() : new LiteralText(TimeFormatter.ticksToTime(world.getTime() - TimeBottleItem.getLastUsed(stack))));
    }

    private static final Identifier TIAB_TEXTURE = new Identifier(TIAB.MOD_ID, "icon.png");

    @Environment(EnvType.CLIENT)
    public static void render(MatrixStack matrices, int screenWidth, int screenHeight){
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player != null && client.player.getMainHandStack() != null){
            if(client.player.getMainHandStack().getItem().equals(TIAB.TIME_IN_A_BOTTLE)){

                HitResult blockHit = client.getCameraEntity().rayTrace(5.0D, 0.0F, false);
                BlockState target = client.world.getBlockState(((BlockHitResult) blockHit).getBlockPos());

                if((TIAB.config.gameplay.accelerate_randomly && target.getBlock().hasRandomTicks(target))
                        || (TIAB.config.gameplay.accelerate_block_entities && target.getBlock().hasBlockEntity())){
                    client.getProfiler().push(new Identifier(TIAB.MOD_ID, "time_tooltip").toString());

                    MutableText text = getText(client.world, client.player.getActiveItem(), client.player.isCreative());
                    int textWidth = client.inGameHud.getFontRenderer().getWidth(text);
                    client.inGameHud.getFontRenderer().drawWithShadow(matrices, text, (screenWidth - textWidth)/2f, (screenHeight/2f - 4) + 16, Integer.parseInt("ffffff", 16));

                    client.getProfiler().pop();
                }
            }
        }
    }

}
