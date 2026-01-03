package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum MeatState implements StringRepresentable {

    FRESH("fresh"),
    COOKED("cooked");
//    ROTTEN("rotten"); planned for later
//    BURNT("burnt"); maybe?

    private final String name;

    MeatState(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }

}
