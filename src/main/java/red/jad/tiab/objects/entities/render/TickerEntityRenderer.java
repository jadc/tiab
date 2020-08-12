package red.jad.tiab.objects.entities.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import red.jad.tiab.TIAB;
import red.jad.tiab.backend.Helpers;
import red.jad.tiab.config.DefaultConfig;
import red.jad.tiab.objects.entities.TickerEntity;

import static net.minecraft.client.util.math.Vector3f.*;

/*
    Creates the rotating clock effect
 */
public class TickerEntityRenderer extends EntityRenderer<TickerEntity> {
    public TickerEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public Identifier getTexture(TickerEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }

    @Override
    public void render(TickerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        final ItemStack stack = new ItemStack(Items.CLOCK);
        float time = entity.getEntityWorld().getTime() + tickDelta;

        if(TIAB.config.getEffectType() != DefaultConfig.effectType.PARTICLES){
            for(Direction d : Direction.values()){
                if(!d.equals(Direction.DOWN) && !d.equals(Direction.UP)){
                    // don't render if inside block
                    BlockPos side = entity.getBlockPos().add(d.getOffsetX(), d.getOffsetY(), d.getOffsetZ());
                    if(entity.getEntityWorld().getBlockState(side).isTranslucent(entity.getEntityWorld(), side)){
                        matrices.push();

                        // center in block
                        matrices.translate(d.getOffsetX()/1.95f, (d.getOffsetY()/1.95f + 0.5), d.getOffsetZ()/1.95f);

                        // align to block sides
                        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(d.asRotation()));

                        // spin clock
                        Vector3f vec = NEGATIVE_Z;
                        if(d.getAxis().equals(Direction.Axis.X)) vec = POSITIVE_Z;
                        matrices.multiply(vec.getRadialQuaternion((time / 20) * entity.getLevel()));

                        // rotate from center
                        matrices.translate(0, -0.125, 0);

                        // render item
                        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, 242, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
                        matrices.pop();
                    }

                }
            }
        }
    }
}
