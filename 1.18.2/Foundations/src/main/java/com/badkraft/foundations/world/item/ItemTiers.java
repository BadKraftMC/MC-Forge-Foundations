package com.badkraft.foundations.world.item;

import com.badkraft.foundations.tags.FoundationsItemTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public enum ItemTiers implements Tier {
    PERMANENT(5, 9999, 10.0F, 10.0F, 15, () -> null),
    FLINT(1, 131, 4.0F, 3.0F, 5, () -> Ingredient.of(FoundationsItemTags.FLINT_TOOL_MATERIALS));

    private final Supplier<Ingredient> repair;
    private final int enchant;
    private final float damage;
    private final float speed;
    private final int uses;
    private final int level;

    ItemTiers(int harvestLevel, int maxUses, float harvestSpeed, float attackDamage, int enchantValue, Supplier<Ingredient> repairMaterial) {
        level = harvestLevel;
        uses = maxUses;
        speed = harvestSpeed;
        damage = attackDamage;
        enchant = enchantValue;
        repair = repairMaterial;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return damage;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getEnchantmentValue() {
        return enchant;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repair.get();
    }
}
