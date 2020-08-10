package red.jad.tiab.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import red.jad.tiab.TIAB;

@me.sargunvohra.mcmods.autoconfig1u.annotation.Config(name = TIAB.MOD_ID)
@me.sargunvohra.mcmods.autoconfig1u.annotation.Config.Gui.Background("minecraft:textures/block/light_blue_concrete_powder.png")
public class Config implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    Client client = new Client();

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
    Gameplay gameplay = new Gameplay();
    private static class Client implements ConfigData {

        enum effectType { CLOCK, PARTICLES, BOTH }

        @ConfigEntry.Gui.Tooltip(count = 3)
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        private effectType effect_type = effectType.CLOCK;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        private int volume = 50;

        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        HUDConfig hud = new HUDConfig();

        private static class HUDConfig implements ConfigData {
            enum displayWhen { HOVER, ALWAYS, NEVER }

            @ConfigEntry.Gui.Tooltip(count = 3)
            @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            private displayWhen display_when = displayWhen.HOVER;

            @ConfigEntry.Gui.Tooltip
            private int vertical_offset = -16;

            @ConfigEntry.ColorPicker
            private int color = 0xffffff;
        }
    }


    private static class Gameplay implements ConfigData {
        @ConfigEntry.Gui.Tooltip(count = 2)
        private boolean accelerate_block_entities = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        private boolean accelerate_randomly = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        private boolean cancel_if_invalid = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        private boolean one_bottle_at_a_time = true;

        @ConfigEntry.Gui.Tooltip
        private int update_frequency = 20;

        @ConfigEntry.Gui.Tooltip
        private int acceleration_duration = 30*20;

        @ConfigEntry.Gui.Tooltip
        private int acceleration_base = 2;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 19)
        private int max_level = 5;

        @ConfigEntry.Gui.Tooltip(count = 2)
        private int random_acceleration_range = 1365;
    }
}
