package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ProcessingStage implements StringRepresentable {

    INTACT("intact"),
    SHEARED("sheared"),
    SKINNED("skinned"),
    DELIMBED("delimbed"),
    BUTCHERED("butchered");

    private final String name;

    ProcessingStage(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }

}
