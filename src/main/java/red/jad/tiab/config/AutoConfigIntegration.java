package red.jad.tiab.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import red.jad.tiab.TIAB;

/*
    Only used when AutoConfig is present
    Overrides getters from DefaultConfig class
 */
@Config(name = TIAB.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/light_blue_concrete_powder.png")
public class AutoConfigIntegration extends DefaultConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    Client client = new Client();

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
    Gameplay gameplay = new Gameplay();
    private class Client implements ConfigData {

        @Comment("CLOCK: Rotating clock on all sides of the block. PARTICLES: Flashing particles on every corner of the block. BOTH: Both effects at the same time")
        @ConfigEntry.Gui.Tooltip(count = 3)
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public effectType effect_type = effectType.CLOCK;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int volume = 50;

        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public HUDConfig hud = new HUDConfig();

        class HUDConfig implements ConfigData {

            @Comment("HOVER: Display when hovering over a valid block. ALWAYS: Display when holding the Time in a Bottle. NEVER: Don't display HUD")
            @ConfigEntry.Gui.Tooltip(count = 3)
            @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            public displayWhen display_when = displayWhen.HOVER;

            @Comment("Vertical offset of the HUD from the center of the screen")
            @ConfigEntry.Gui.Tooltip
            public int vertical_offset = -16;

            @ConfigEntry.ColorPicker
            public int color = 0xffffff;
        }
    }


    private class Gameplay implements ConfigData {
        @Comment("Will the Time in a Bottle accelerate block entites? (e.g. furnaces, brewing stands, etc.)")
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean accelerate_block_entities = true;

        @Comment("Will the Time in a Bottle accelerate randomly ticking blocks? (e.g. wheat, sugar cane)")
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean accelerate_randomly = true;

        @Comment("If the block being accelerated is no longer able to be, should the acceleration be cancelled?")
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean cancel_if_invalid = true;

        @Comment("Only the bottle with the most stored time on you will continue to accumulate time")
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean one_bottle_at_a_time = true;

        @Comment("How frequently, in ticks, the bottle's clock updates")
        @ConfigEntry.Gui.Tooltip
        public int update_frequency = 20;

        @Comment("How long, in ticks, the acceleration effect lasts")
        @ConfigEntry.Gui.Tooltip
        public int acceleration_duration = 30*20;

        @Comment("The base of the extra ticks per level equation (x in x^y)")
        @ConfigEntry.Gui.Tooltip
        public int acceleration_base = 2;

        @Comment("The exponent of the extra ticks per level equation (y in x^y)")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 19)
        public int max_level = 5;

        @Comment("The lower this value, the more frequently blocks will be randomly ticked. (i.e. lower = faster growing)")
        @ConfigEntry.Gui.Tooltip(count = 2)
        public int random_acceleration_range = 1365;
    }

    @Override
    public effectType getEffectType(){ return this.client.effect_type; }
    @Override
    public int getVolume(){ return this.client.volume; }
    @Override
    public displayWhen getDisplayWhen(){ return this.client.hud.display_when; }
    @Override
    public int getVerticalOffset(){ return this.client.hud.vertical_offset; }
    @Override
    public int getColor(){ return this.client.hud.color; }

    @Override
    public boolean getAccelerateBlockEntities(){ return this.gameplay.accelerate_block_entities; }
    @Override
    public boolean getAccelerateRandomly(){ return this.gameplay.accelerate_randomly; }
    @Override
    public boolean getCancelIfInvalid(){ return this.gameplay.cancel_if_invalid; }
    @Override
    public boolean getOneBottleAtATime(){ return this.gameplay.one_bottle_at_a_time; }
    @Override
    public int getUpdateFrequency(){ return this.gameplay.update_frequency; }
    @Override
    public int getAccelerationDuration(){ return this.gameplay.acceleration_duration; }
    @Override
    public int getAccelerationBase(){ return this.gameplay.acceleration_base; }
    @Override
    public int getMaxLevel(){ return this.gameplay.max_level; }
    @Override
    public int getRandomAccelerationRange(){ return this.gameplay.random_acceleration_range; }

}
