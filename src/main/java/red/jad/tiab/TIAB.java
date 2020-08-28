package red.jad.tiab;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Logger;
import red.jad.tiab.backend.CrouchlessUseHandler;
import red.jad.tiab.backend.Helpers;
import red.jad.tiab.config.AutoConfigIntegration;
import red.jad.tiab.config.DefaultConfig;
import red.jad.tiab.objects.entities.TickerEntity;
import red.jad.tiab.objects.items.TimeBottleItem;

import org.apache.logging.log4j.LogManager;

public class TIAB implements ModInitializer {

	public static final String MOD_ID = "tiab";

	public static final Logger LOG = LogManager.getLogger(TIAB.MOD_ID);
	public static Identifier id(String path){
		return new Identifier(TIAB.MOD_ID, path);
	}
	public static DefaultConfig config;

	public static final Item TIME_IN_A_BOTTLE = new TimeBottleItem();

	public static final EntityType<TickerEntity> TICKER = Registry.register(
		Registry.ENTITY_TYPE,
		id("ticker"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, TickerEntity::new).dimensions(EntityDimensions.fixed(0.25F, 0.25F)).build()
	);

	@Override
	public void onInitialize() {

		CrouchlessUseHandler.init();

		Registry.register(Registry.ITEM, id("time_in_a_bottle"), TIME_IN_A_BOTTLE);

		if(FabricLoader.getInstance().isModLoaded(Helpers.AUTOCONFIG_MOD_ID)){
			AutoConfig.register(AutoConfigIntegration.class, GsonConfigSerializer::new);
			config = AutoConfig.getConfigHolder(AutoConfigIntegration.class).getConfig();
		}else{
			config = new DefaultConfig();
		}
	}
}
