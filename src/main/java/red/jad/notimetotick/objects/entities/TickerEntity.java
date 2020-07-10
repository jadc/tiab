package red.jad.notimetotick.objects.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import red.jad.notimetotick.backend.Config;
import red.jad.notimetotick.backend.SpawnPacketHelper;

public class TickerEntity extends Entity {

    private static final TrackedData<Byte> LEVEL = DataTracker.registerData(TickerEntity.class, TrackedDataHandlerRegistry.BYTE);

    public TickerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos target = new BlockPos(this.getX(), this.getY(), this.getZ());

        // Duration
        if(this.age > (Config.baseDuration * Math.pow(Config.multiplier, this.getLevel())) * Config.ticksPerSecond) this.kill();


        //this.getEntityWorld().addImportantParticle(ParticleTypes.CLOUD, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0)
        world.breakBlock(target, true);
    }

    public void setLevel(byte level){
        if (!this.world.isClient) {
            this.getDataTracker().set(LEVEL, level < 0 ? 0 : level);
        }
    }

    public byte getLevel(){
        return (Byte)this.getDataTracker().get(LEVEL);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(LEVEL, (byte)0);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.contains("Level", 99)) {
            this.setLevel(tag.getByte("Level"));
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        tag.putByte("Level", this.getLevel());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return SpawnPacketHelper.createNonLivingPacket(this);
    }
}
