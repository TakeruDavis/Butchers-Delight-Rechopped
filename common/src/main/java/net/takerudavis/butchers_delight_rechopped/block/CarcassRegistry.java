package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CarcassRegistry {

    private static final List<Supplier<? extends AbstractCarcassBlock>> blocks = new ArrayList<>();
    private static final Map<EntityType<? extends LivingEntity>, Supplier<? extends AbstractCarcassBlock>> byEntity = new HashMap<>();

    public static void register(Supplier<? extends AbstractCarcassBlock> block, EntityType<? extends LivingEntity> entityType) {
        blocks.add(block);
        byEntity.put(entityType, block);
    }

    public static List<AbstractCarcassBlock> getAll() {
        return blocks.stream().<AbstractCarcassBlock>map(Supplier::get).toList();
    }

    @Nullable
    public static AbstractCarcassBlock getForEntity(EntityType<?> entityType) {
        Supplier<? extends AbstractCarcassBlock> s = byEntity.get(entityType);
        return s != null ? s.get() : null;
    }
}
