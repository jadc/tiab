package red.jad.tiab.config;

import com.google.gson.annotations.Expose;

public class ConfigEffects {
    @Expose
    public boolean play_sounds, rotating_clock, particles, hud;
    @Expose
    public float volume;
    @Expose
    public int opacity;

    public ConfigEffects(){
        play_sounds = true;
        rotating_clock = true;
        particles = false;
        volume = 0.5f;
        opacity = 8;
        hud = true;
    }
}
