package red.jad.tiab.config;

import com.google.gson.annotations.Expose;

public class ConfigGameplay {
    @Expose
    public int update_frequency, acceleration_duration, acceleration_base, max_level, random_acceleration_range;
    @Expose
    public boolean accelerate_block_entities, accelerate_randomly, cancel_if_invalid, one_bottle_at_a_time;

    public ConfigGameplay(){
        update_frequency = 20;
        acceleration_duration = 30*20;
        acceleration_base = 2;
        max_level = 10;
        random_acceleration_range = 1365;

        accelerate_block_entities = true;
        accelerate_randomly = true;
        cancel_if_invalid = true;

        one_bottle_at_a_time = true;
    }
}
