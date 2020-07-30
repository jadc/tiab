package red.jad.tiab.objects.entities.render;

import net.fabricmc.fabric.impl.client.indigo.renderer.render.ItemRenderContext;
import net.fabricmc.loader.util.sat4j.core.Vec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.datafixer.fix.ChunkPalettedStorageFix;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.graalvm.compiler.loop.InductionVariable;
import red.jad.tiab.TIAB;
import red.jad.tiab.objects.entities.TickerEntity;

import static net.minecraft.client.util.math.Vector3f.*;

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
        float time = entity.getEntityWorld().getTime() + tickDelta;
        float lvl = entity.getLevel();

        if(!TIAB.config.effects.minimal){
            for(Direction d : Direction.values()){
                if(!d.equals(Direction.DOWN) && !d.equals(Direction.UP)){
                    matrices.push();
                    matrices.translate(d.getOffsetX()/1.94f, (d.getOffsetY()/1.94f + 0.5), d.getOffsetZ()/1.94f);

                    matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(d.asRotation()));

                    float angle = (time/20) * lvl;
                    if(d.getAxis().equals(Direction.Axis.X)){
                        matrices.multiply(POSITIVE_Z.getRadialQuaternion(angle));
                    }else{
                        matrices.multiply(NEGATIVE_Z.getRadialQuaternion(angle));
                    }

                    matrices.translate(0, -0.125, 0);

                    MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(TIAB.EFFECT_CLOCK_HAND), ModelTransformation.Mode.GROUND, 240, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
                    matrices.pop();

                    matrices.push();
                    matrices.translate(d.getOffsetX()/1.95f, (d.getOffsetY()/1.95f + 0.5), d.getOffsetZ()/1.95f);

                    matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(d.asRotation()));

                    matrices.translate(0, -0.125, 0);

                    MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(TIAB.EFFECT_CLOCK), ModelTransformation.Mode.GROUND, 240, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
                    matrices.pop();
                }
            }
        }
    }
}
