package red.jad.notimetotick.objects.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import red.jad.notimetotick.backend.SpawnPacketHelper;

public class TickerEntity extends Entity {

    private static final TrackedData<Integer> LEVEL = DataTracker.registerData(TickerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public TickerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        //this.getEntityWorld().addImportantParticle(ParticleTypes.CLOUD, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        super.tick();
    }

    public void setLevel(int level){
        if (!this.world.isClient) {
            this.getDataTracker().set(LEVEL, level);
        }
    }

    public int getLevel(){
        return (Integer)this.getDataTracker().get(LEVEL);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(LEVEL, 0);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.contains("Level", 99)) {
            this.setLevel(tag.getInt("Level"));
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        tag.putInt("Level", this.getLevel());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return SpawnPacketHelper.createNonLivingPacket(this);
    }
}
