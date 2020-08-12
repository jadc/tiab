package red.jad.tiab.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
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

        @ConfigEntry.Gui.Tooltip(count = 3)
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public effectType effect_type = effectType.CLOCK;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int volume = 50;

        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public HUDConfig hud = new HUDConfig();

        class HUDConfig implements ConfigData {

            @ConfigEntry.Gui.Tooltip(count = 3)
            @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            public displayWhen display_when = displayWhen.HOVER;

            @ConfigEntry.Gui.Tooltip
            public int vertical_offset = -16;

            @ConfigEntry.ColorPicker
            public int color = 0xffffff;
        }
    }


    private class Gameplay implements ConfigData {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean accelerate_block_entities = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean accelerate_randomly = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean cancel_if_invalid = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean one_bottle_at_a_time = true;

        @ConfigEntry.Gui.Tooltip
        public int update_frequency = 20;

        @ConfigEntry.Gui.Tooltip
        public int acceleration_duration = 30*20;

        @ConfigEntry.Gui.Tooltip
        public int acceleration_base = 2;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 19)
        public int max_level = 5;

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
