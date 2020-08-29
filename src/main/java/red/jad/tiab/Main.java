package red.jad.tiab;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import red.jad.tiab.backend.CrouchlessUseHandler;
import red.jad.tiab.backend.Helpers;
import red.jad.tiab.backend.SpawnPacketHelper;
import red.jad.tiab.client.ClientNetworking;
import red.jad.tiab.client.TimeTooltip;
import red.jad.tiab.config.AutoConfigIntegration;
import red.jad.tiab.config.DefaultConfig;
import red.jad.tiab.objects.entities.TickerEntity;
import red.jad.tiab.objects.entities.render.TickerEntityRenderer;
import red.jad.tiab.objects.items.TimeBottleItem;

public class Main implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "tiab";
    public static DefaultConfig config;

    public static Identifier id(String path){
        return new Identifier(Main.MOD_ID, path);
    }
    public static final Item TIME_IN_A_BOTTLE = new TimeBottleItem();

    public static final EntityType<TickerEntity> TICKER = Registry.register(
            Registry.ENTITY_TYPE,
            id("ticker"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, TickerEntity::new).dimensions(EntityDimensions.fixed(0.25F, 0.25F)).build()
    );

    @Override
    public void onInitialize() {
        HudRenderCallback.EVENT.register(((matrixStack, v) -> {
            if(Main.config.getDisplayWhen() != DefaultConfig.displayWhen.NEVER) TimeTooltip.render(matrixStack, v);
        }));

        CrouchlessUseHandler.init();

        Registry.register(Registry.ITEM, id("time_in_a_bottle"), TIME_IN_A_BOTTLE);

        if(FabricLoader.getInstance().isModLoaded(Helpers.AUTOCONFIG_MOD_ID)){
            AutoConfig.register(AutoConfigIntegration.class, JanksonConfigSerializer::new);
            config = AutoConfig.getConfigHolder(AutoConfigIntegration.class).getConfig();
        }else{
            config = new DefaultConfig();
        }
    }

    /*
        Fabric doesn't work properly for entities not extending MobEntity.
        Fabric pls fix this shit so I can remove it
    */
    @Environment(EnvType.CLIENT)
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(TICKER, ((dispatcher, context) -> {
            return new TickerEntityRenderer(dispatcher);
        }));
        ClientSidePacketRegistry.INSTANCE.register(SpawnPacketHelper.SPAWN_PACKET, ClientNetworking::spawnNonLivingEntity);
    }
}
