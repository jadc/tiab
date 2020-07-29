package red.jad.tiab.objects.entities.render;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import red.jad.tiab.TIAB;
import red.jad.tiab.objects.entities.TickerEntity;

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
        if(TIAB.config.effects.effect_type == 2){

            ItemStack stack = new ItemStack(Items.CLOCK);

            matrices.push();

            float time = entity.getEntityWorld().getTime() + tickDelta;
            matrices.translate(0, 2, 0);
            MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(Items.SNOWBALL), ModelTransformation.Mode.GROUND, WorldRenderer.getLightmapCoordinates(entity.getEntityWorld(), entity.getBlockPos().up(255)), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);

            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(time * 4));
            MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(Items.ITEM_FRAME), ModelTransformation.Mode.GROUND, WorldRenderer.getLightmapCoordinates(entity.getEntityWorld(), entity.getBlockPos().up(255)), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);

            matrices.pop();
        }
    }
}
