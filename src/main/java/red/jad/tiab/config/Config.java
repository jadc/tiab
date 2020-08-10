package red.jad.tiab.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import red.jad.tiab.TIAB;

@me.sargunvohra.mcmods.autoconfig1u.annotation.Config(name = TIAB.MOD_ID)
@me.sargunvohra.mcmods.autoconfig1u.annotation.Config.Gui.Background("minecraft:textures/block/light_blue_concrete_powder.png")
public class Config implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public Client client = new Client();

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
    public Gameplay gameplay = new Gameplay();
    public static class Client implements ConfigData {

        public enum effectType { CLOCK, PARTICLES, BOTH }

        @ConfigEntry.Gui.Tooltip(count = 3)
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public effectType effect_type = effectType.CLOCK;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int volume = 50;

        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public HUDConfig hud = new HUDConfig();

        public static class HUDConfig implements ConfigData {
            public enum displayWhen { HOVER, ALWAYS, NEVER }

            @ConfigEntry.Gui.Tooltip(count = 3)
            @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            public displayWhen display_when = displayWhen.HOVER;

            @ConfigEntry.Gui.Tooltip
            public int vertical_offset = -16;

            @ConfigEntry.ColorPicker
            public int color = 0xffffff;
        }
    }


    public static class Gameplay implements ConfigData {
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
}
