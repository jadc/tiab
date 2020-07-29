package red.jad.tiab.config;

import com.google.gson.annotations.Expose;

public class ConfigEffects {
    @Expose
    public boolean play_sounds;
    @Expose
    public float volume;

    public ConfigEffects(){
        play_sounds = true;
        volume = 0.25f;
    }
}
