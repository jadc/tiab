package red.jad.notimetotick.objects.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import red.jad.notimetotick.backend.Config;
import red.jad.notimetotick.backend.SpawnPacketHelper;

import java.util.Random;

public class TickerEntity extends Entity {

    private static final TrackedData<Integer> LEVEL = DataTracker.registerData(TickerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public TickerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
            BlockPos target = new BlockPos(this.getX(), this.getY(), this.getZ());
            BlockState state = world.getBlockState(target);

            // Duration
            //if( this.age > (Config.baseDuration * 20) ) this.kill();

            if(state.getOutlineShape(world, target) != null){
                VoxelShape shape = state.getOutlineShape(world, target);

                if(world.getTime() % (20/(getLevel()+1)) == 0){
                    tickingEffect( target.getX(), target.getY(), target.getZ() );
                    tickingEffect( target.getX() + shape.getMax(Direction.Axis.X), target.getY(), target.getZ() );
                    tickingEffect( target.getX(), target.getY() + shape.getMax(Direction.Axis.Y), target.getZ() );
                    tickingEffect( target.getX() + shape.getMax(Direction.Axis.X), target.getY() + shape.getMax(Direction.Axis.Y), target.getZ() );

                    tickingEffect( target.getX(), target.getY(), target.getZ() + shape.getMax(Direction.Axis.Z) );
                    tickingEffect( target.getX() + shape.getMax(Direction.Axis.X), target.getY(), target.getZ() + shape.getMax(Direction.Axis.Z) );
                    tickingEffect( target.getX(), target.getY() + shape.getMax(Direction.Axis.Y), target.getZ() + shape.getMax(Direction.Axis.Z) );
                    tickingEffect( target.getX() + shape.getMax(Direction.Axis.X), target.getY() + shape.getMax(Direction.Axis.Y), target.getZ() + shape.getMax(Direction.Axis.Z) );

                }
            }

        if(!world.isClient()){
            int howManyTicksToTick = (int) Math.pow(Config.multiplier, (getLevel() + 1)) - 1;
            for(int i = 0; i < howManyTicksToTick; i++){
                if(!state.getBlock().hasBlockEntity() && !state.getBlock().hasRandomTicks(state)) this.kill();

                if(state.getBlock().hasRandomTicks(state)){
                    if(this.world.getRandom().nextInt(1365) == 0) state.getBlock().randomTick(state, (ServerWorld) world, target, this.world.getRandom());
                }
                if(state.getBlock().hasBlockEntity()) {
                    BlockEntity tile = world.getBlockEntity(target);
                    if(tile != null && !tile.isRemoved() && tile instanceof Tickable){
                        ((Tickable) tile).tick();
                    }
                }
            }
        }
    }

    public void tickingEffect(double x, double y, double z){
        world.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, 0, 0, 0);
    }

    public void setLevel(int level){
        if (!this.world.isClient) {
            this.getDataTracker().set(LEVEL, Math.max(level, 0));
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
