package red.jad.notimetotick.access;

/*
Interface to allow access to custom nbt methods
 */

public interface PlayerEntityAccess {
    long getNTTTBottleLastEquipped();
    void setNTTTBottleLastEquipped(long ticks);
    long getNTTTBottleLastUsed();
    void setNTTTBottleLastUsed(long ticks);
}
