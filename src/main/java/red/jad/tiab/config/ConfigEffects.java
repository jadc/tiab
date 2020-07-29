package red.jad.tiab.config;

import com.google.gson.annotations.Expose;

public class ConfigEffects {
    @Expose
    public boolean play_sounds;
    @Expose
    public float volume;
    @Expose
    public int effect_type;

    public ConfigEffects(){
        play_sounds = true;
        volume = 0.25f;
        effect_type = 2;
    }
}
