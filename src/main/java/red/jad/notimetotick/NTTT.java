package red.jad.notimetotick;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import red.jad.notimetotick.objects.entities.TickerEntity;
import red.jad.notimetotick.objects.items.TimeBottleItem;

public class NTTT implements ModInitializer {

	public static final String MOD_ID = "notimetotick";
	public static Identifier id(String path){
		return new Identifier(NTTT.MOD_ID, path);
	}

	public static final Item TIME_IN_A_BOTTLE = new TimeBottleItem();

	public static final EntityType<TickerEntity> TICKER = Registry.register(
		Registry.ENTITY_TYPE,
		id("ticker"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, TickerEntity::new).dimensions(EntityDimensions.fixed(0.25F, 0.25F)).build()
	);

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, id("time_in_a_bottle"), TIME_IN_A_BOTTLE);

		//FabricDefaultAttributeRegistry.register(TICKER, TickerEntity.createAttributes());
	}
}
