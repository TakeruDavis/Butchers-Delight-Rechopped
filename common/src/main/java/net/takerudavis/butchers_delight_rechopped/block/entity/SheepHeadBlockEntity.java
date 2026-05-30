package net.takerudavis.butchers_delight_rechopped.block.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SheepHeadBlockEntity extends SkullBlockEntity {

    // Kolish's skin — placeholder texture for the sheep head drop
    public static final String KOLISH_TEXTURE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1" +
                    "cmUvZTA2OGQzMzJhYWJlOWI4Nzc0MGIzZmFkZjU1NzI0NTJiZWY2NzlmN2FhNTdlOGVjMWUwMTc2Nzg5NzFh" +
                    "ZTgwNyJ9fX0=";

    public static final GameProfile KOLISH_PROFILE;
    static {
        KOLISH_PROFILE = new GameProfile(UUID.nameUUIDFromBytes("Kolish".getBytes()), "Kolish");
        KOLISH_PROFILE.getProperties().put("textures", new Property("textures", KOLISH_TEXTURE));
    }

    public static BlockEntityType<SheepHeadBlockEntity> TYPE;

    public SheepHeadBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return TYPE;
    }

    @Override
    public @Nullable ResourceLocation getNoteBlockSound() {
        return new ResourceLocation("minecraft", "entity.sheep.ambient");
    }

}
