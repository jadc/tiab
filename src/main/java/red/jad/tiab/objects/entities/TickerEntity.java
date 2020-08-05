package red.jad.tiab.objects.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import red.jad.tiab.TIAB;
import red.jad.tiab.backend.SpawnPacketHelper;

import java.util.Random;

public class TickerEntity extends Entity {

    private static final TrackedData<Integer> LEVEL = DataTracker.registerData(TickerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public TickerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        // Anti-crash
        if(getLevel() <= 0 || getLevel() > 20){
            if(!this.world.isClient()) TIAB.LOG.warn("'{}' at [{}, {}, {}] had an invalid level of {} and was removed to avoid crashing the game; lower the configured max level below 20!", this.getDisplayName().getString(), this.getX(), this.getY(), this.getZ(), getLevel());
            perish();
        }

        // Duration
        if(TIAB.config.gameplay.acceleration_duration == 0 || this.age > TIAB.config.gameplay.acceleration_duration) perish();

        BlockPos target = new BlockPos(this.getX(), this.getY(), this.getZ());
        BlockState state = world.getBlockState(target);

        if(TIAB.config.effects.particles && state.getOutlineShape(world, target) != null){
            VoxelShape shape = state.getOutlineShape(world, target);

            int rate = (TIAB.config.gameplay.max_level * 4) / getLevel();
            if(rate <= 1 || this.age % rate == 0){
                for(int x = 0; x <= 1; x++){
                    for(int z = 0; z <= 1; z++){
                        for(int y = 0; y <= 1; y++){
                            world.addParticle(
                                    ParticleTypes.BUBBLE_POP,
                                    target.getX() + (x == 0 ? x : shape.getMax(Direction.Axis.X)),
                                    target.getY() + (y == 0 ? y : shape.getMax(Direction.Axis.Y)),
                                    target.getZ() + (z == 0 ? z : shape.getMax(Direction.Axis.Z)),
                                    0, 0, 0
                            );
                        }
                    }
                }

            }
        }

        if(!world.isClient()){
            // If 'invalid' block, check config
            if(TIAB.config.gameplay.cancel_if_invalid){
                if((TIAB.config.gameplay.accelerate_block_entities && !state.getBlock().hasBlockEntity())
                        && (TIAB.config.gameplay.accelerate_randomly && !state.getBlock().hasRandomTicks(state))) {
                    perish();
                }
            }

            int howManyTicksToTick = (int) Math.pow(TIAB.config.gameplay.acceleration_base, getLevel()) - 1; // - 1 cus block is ticking itself
            for(int i = 0; i < howManyTicksToTick; i++){
                if(TIAB.config.gameplay.accelerate_randomly && state.getBlock().hasRandomTicks(state)){
                    // random_acceleration_range lower = more likely to random tick
                    if(this.world.getRandom().nextInt(TIAB.config.gameplay.random_acceleration_range) == 0){
                        state.getBlock().randomTick(state, (ServerWorld) world, target, this.world.getRandom());
                    }
                }
                if(TIAB.config.gameplay.accelerate_block_entities && state.getBlock().hasBlockEntity()) {
                    BlockEntity tile = world.getBlockEntity(target);
                    if(tile != null && !tile.isRemoved() && tile instanceof Tickable){
                        ((Tickable) tile).tick();
                    }
                }
            }
        }
    }

    public void perish(){
        if(TIAB.config.effects.play_sounds){
            world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, TIAB.config.effects.volume, 0.1f);
            world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, TIAB.config.effects.volume / 2, 1.5f);
        }
        this.kill();
    }

    /*
        Entity Data Handling
     */
    public void setLevel(int level){
        if (!this.world.isClient) {
            // This disgusting assortment of mins and maxxes basically does...
            // 1 <-> level <-> (max_level, which itself is maxxed at 20)
            this.getDataTracker().set(LEVEL, Math.max(Math.min(Math.min(level, 20), TIAB.config.gameplay.max_level), 1));
        }
    }

    public int getLevel(){
        return (Integer) this.getDataTracker().get(LEVEL);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(LEVEL, 1);
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

    /*
        Fabric, can you please fix entities not displaying to the client
     */
    @Override
    public Packet<?> createSpawnPacket() {
        return SpawnPacketHelper.createNonLivingPacket(this);
    }
}
