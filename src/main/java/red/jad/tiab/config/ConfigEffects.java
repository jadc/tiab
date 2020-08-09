package red.jad.tiab.config;

import com.google.gson.annotations.Expose;

public class ConfigEffects {
    @Expose
    public boolean play_sounds, rotating_clock, particles, hud;
    @Expose
    public float volume;
    @Expose
    public int rotating_clock_opacity;

    public ConfigEffects(){
        play_sounds = true;
        volume = 0.5f;
        rotating_clock = true;
        particles = false;
        rotating_clock_opacity = 8;
        hud = true;
    }
}
