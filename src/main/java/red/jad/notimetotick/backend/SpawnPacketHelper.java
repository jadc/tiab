package red.jad.notimetotick.backend;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import red.jad.notimetotick.NTTT;

public final class SpawnPacketHelper {
    public static final Identifier SPAWN_PACKET = NTTT.id("spawn/nonliving/generic");

    private SpawnPacketHelper() {
    }

    public static Packet<?> createNonLivingPacket(Entity entity) {
        if (entity.world.isClient()) {
            throw new IllegalArgumentException("Cannot create spawn packet for entity on a ClientWorld");
        }

        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(entity.getEntityId());
        buf.writeUuid(entity.getUuid());
        buf.writeIdentifier(Registry.ENTITY_TYPE.getId(entity.getType()));
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeByte(MathHelper.floor(entity.pitch * 256.0F / 360.0F));
        buf.writeByte(MathHelper.floor(entity.yaw * 256.0F / 360.0F));
        buf.writeShort((int) (MathHelper.clamp(entity.getVelocity().getX(), -3.9D, 3.9D) * 8000.0D));
        buf.writeShort((int) (MathHelper.clamp(entity.getVelocity().getY(), -3.9D, 3.9D) * 8000.0D));
        buf.writeShort((int) (MathHelper.clamp(entity.getVelocity().getZ(), -3.9D, 3.9D) * 8000.0D));

        return ServerSidePacketRegistry.INSTANCE.toPacket(SPAWN_PACKET, buf);
    }
}
