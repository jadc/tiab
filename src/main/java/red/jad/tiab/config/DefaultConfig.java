package red.jad.tiab.config;

import red.jad.tiab.backend.Helpers;

/*
    Used if AutoConfig is not present.
    Constants that cannot be configured due to no AutoConfig.
 */
public class DefaultConfig {
    // Enumerators
    public enum effectType { CLOCK, PARTICLES, BOTH }
    public enum displayWhen { HOVER, ALWAYS, NEVER }

    public effectType getEffectType(){              return effectType.CLOCK; }
    public int getVolume(){                         return 50; }
    public displayWhen getDisplayWhen(){            return displayWhen.HOVER; }
    public int getVerticalOffset(){                 return -16; }
    public int getColor(){                          return 0xffffff; }

    public boolean getAccelerateBlockEntities(){    return true; }
    public boolean getAccelerateRandomly(){         return true; }
    public boolean getCancelIfInvalid(){            return true; }
    public boolean getOneBottleAtATime(){           return true; }
    public int getUpdateFrequency(){                return 20; }
    public int getAccelerationDuration(){           return 30*20; }
    public int getAccelerationBase(){               return 2; }
    public int getMaxLevel(){                       return 5; }
    public int getRandomAccelerationRange(){        return 1365; }


}
