package net.fodoth.skina.stopgrowth.mixinutil;

public interface AgeGrowthControl {
    boolean stopgrowth$isGrowthStopped();
    void stopgrowth$setGrowthStopped(boolean stopped);
}
