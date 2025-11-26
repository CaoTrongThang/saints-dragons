package com.leon.saintsdragons.neoforge.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * NeoForge config using ModConfigSpec.
 * This config will automatically be editable via NeoForge's built-in config screen.
 *
 * Spawn weights/group sizes are configured via JSON biome modifiers in datapacks.
 * This config focuses on dragon attributes (health, damage, speed, etc.).
 */
public class SaintsDragonsNeoForgeConfig {

    public static final ModConfigSpec COMMON_SPEC;

    // ===== RAEVYX ATTRIBUTES =====
    public static final ModConfigSpec.DoubleValue RAEVYX_MAX_HEALTH;
    public static final ModConfigSpec.DoubleValue RAEVYX_ATTACK_DAMAGE;
    public static final ModConfigSpec.DoubleValue RAEVYX_FLYING_SPEED;
    public static final ModConfigSpec.DoubleValue RAEVYX_TAMING_CHANCE_BASE;
    public static final ModConfigSpec.DoubleValue RAEVYX_TAMING_CHANCE_HEARTY;
    public static final ModConfigSpec.BooleanValue RAEVYX_LEGACY_TAMING;

    // ===== STEGONAUT ATTRIBUTES =====
    public static final ModConfigSpec.DoubleValue STEGONAUT_MAX_HEALTH;
    public static final ModConfigSpec.DoubleValue STEGONAUT_ATTACK_DAMAGE;
    public static final ModConfigSpec.DoubleValue STEGONAUT_ARMOR;

    // ===== CINDERVANE ATTRIBUTES =====
    public static final ModConfigSpec.DoubleValue CINDERVANE_MAX_HEALTH;
    public static final ModConfigSpec.DoubleValue CINDERVANE_ATTACK_DAMAGE;
    public static final ModConfigSpec.DoubleValue CINDERVANE_FLYING_SPEED;
    public static final ModConfigSpec.DoubleValue CINDERVANE_TAMING_CHANCE_BASE;
    public static final ModConfigSpec.DoubleValue CINDERVANE_TAMING_CHANCE_HEARTY;

    // ===== NULLJAW ATTRIBUTES =====
    public static final ModConfigSpec.DoubleValue NULLJAW_MAX_HEALTH;
    public static final ModConfigSpec.DoubleValue NULLJAW_ATTACK_DAMAGE;
    public static final ModConfigSpec.DoubleValue NULLJAW_SWIM_SPEED;
    public static final ModConfigSpec.DoubleValue NULLJAW_TAMING_CHANCE;
    public static final ModConfigSpec.BooleanValue NULLJAW_LEGACY_TAMING;

    // ===== IGNIVORUS ATTRIBUTES =====
    public static final ModConfigSpec.DoubleValue IGNIVORUS_MAX_HEALTH;
    public static final ModConfigSpec.DoubleValue IGNIVORUS_ATTACK_DAMAGE;
    public static final ModConfigSpec.DoubleValue IGNIVORUS_FLYING_SPEED;
    public static final ModConfigSpec.DoubleValue IGNIVORUS_TAMING_CHANCE_BASE;
    public static final ModConfigSpec.DoubleValue IGNIVORUS_TAMING_CHANCE_HEARTY;
    public static final ModConfigSpec.BooleanValue IGNIVORUS_LEGACY_TAMING;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Dragon attributes configuration. To customize spawning (weights, biomes, group sizes), edit the JSON files in data/saintsdragons/neoforge/biome_modifier/ or use a datapack.");

        // ===== RAEVYX ATTRIBUTES =====
        builder.push("raevyx_attributes");
        builder.comment("Raevyx (Lightning Wyvern) attributes");

        RAEVYX_MAX_HEALTH = builder
                .comment("Maximum health")
                .defineInRange("maxHealth", 100.0, 1.0, 1000.0);

        RAEVYX_ATTACK_DAMAGE = builder
                .comment("Attack damage")
                .defineInRange("attackDamage", 12.0, 1.0, 100.0);

        RAEVYX_FLYING_SPEED = builder
                .comment("Flying speed")
                .defineInRange("flyingSpeed", 0.4, 0.0, 2.0);

        RAEVYX_TAMING_CHANCE_BASE = builder
                .comment("Taming chance with base food (lower = easier, 1 = 100% per feed, 100 = 1% per feed)")
                .defineInRange("tamingChanceBase", 5.0, 1.0, 100.0);

        RAEVYX_TAMING_CHANCE_HEARTY = builder
                .comment("Taming chance with hearty meal (lower = easier, 1 = 100% per feed, 100 = 1% per feed)")
                .defineInRange("tamingChanceHearty", 3.0, 1.0, 100.0);

        RAEVYX_LEGACY_TAMING = builder
                .comment("Legacy taming: true = simple food taming, false = special mechanics (rodeo/low-health)")
                .define("legacyTaming", false);

        builder.pop();

        // ===== STEGONAUT ATTRIBUTES =====
        builder.push("stegonaut_attributes");
        builder.comment("Stegonaut (Armored Dinosaur) attributes");

        STEGONAUT_MAX_HEALTH = builder
                .comment("Maximum health")
                .defineInRange("maxHealth", 80.0, 1.0, 1000.0);

        STEGONAUT_ATTACK_DAMAGE = builder
                .comment("Attack damage")
                .defineInRange("attackDamage", 8.0, 1.0, 100.0);

        STEGONAUT_ARMOR = builder
                .comment("Armor value")
                .defineInRange("armor", 10.0, 0.0, 30.0);

        builder.pop();

        // ===== CINDERVANE ATTRIBUTES =====
        builder.push("cindervane_attributes");
        builder.comment("Cindervane (Fire Dragon) attributes");

        CINDERVANE_MAX_HEALTH = builder
                .comment("Maximum health")
                .defineInRange("maxHealth", 120.0, 1.0, 1000.0);

        CINDERVANE_ATTACK_DAMAGE = builder
                .comment("Attack damage")
                .defineInRange("attackDamage", 14.0, 1.0, 100.0);

        CINDERVANE_FLYING_SPEED = builder
                .comment("Flying speed")
                .defineInRange("flyingSpeed", 0.35, 0.0, 2.0);

        CINDERVANE_TAMING_CHANCE_BASE = builder
                .comment("Taming chance with base food (lower = easier, 1 = 100% per feed, 100 = 1% per feed)")
                .defineInRange("tamingChanceBase", 4.0, 1.0, 100.0);

        CINDERVANE_TAMING_CHANCE_HEARTY = builder
                .comment("Taming chance with hearty meal (lower = easier, 1 = 100% per feed, 100 = 1% per feed)")
                .defineInRange("tamingChanceHearty", 2.0, 1.0, 100.0);

        builder.pop();

        // ===== NULLJAW ATTRIBUTES =====
        builder.push("nulljaw_attributes");
        builder.comment("Nulljaw (Amphibious Dragon) attributes");

        NULLJAW_MAX_HEALTH = builder
                .comment("Maximum health")
                .defineInRange("maxHealth", 90.0, 1.0, 1000.0);

        NULLJAW_ATTACK_DAMAGE = builder
                .comment("Attack damage")
                .defineInRange("attackDamage", 10.0, 1.0, 100.0);

        NULLJAW_SWIM_SPEED = builder
                .comment("Swimming speed")
                .defineInRange("swimSpeed", 0.3, 0.0, 2.0);

        NULLJAW_TAMING_CHANCE = builder
                .comment("Taming chance per rodeo attempt (lower = easier, 1 = 100% per attempt, 100 = 1% per attempt)")
                .defineInRange("tamingChance", 6.0, 1.0, 100.0);

        NULLJAW_LEGACY_TAMING = builder
                .comment("Legacy taming: true = simple food taming, false = special mechanics (rodeo/low-health)")
                .define("legacyTaming", false);

        builder.pop();

        // ===== IGNIVORUS ATTRIBUTES =====
        builder.push("ignivorus_attributes");
        builder.comment("Ignivorus (Giant Fire Dragon) attributes");

        IGNIVORUS_MAX_HEALTH = builder
                .comment("Maximum health")
                .defineInRange("maxHealth", 150.0, 1.0, 1000.0);

        IGNIVORUS_ATTACK_DAMAGE = builder
                .comment("Attack damage")
                .defineInRange("attackDamage", 16.0, 1.0, 100.0);

        IGNIVORUS_FLYING_SPEED = builder
                .comment("Flying speed")
                .defineInRange("flyingSpeed", 0.3, 0.0, 2.0);

        IGNIVORUS_TAMING_CHANCE_BASE = builder
                .comment("Taming chance with base food (lower = easier, 1 = 100% per feed, 100 = 1% per feed)")
                .defineInRange("tamingChanceBase", 7.0, 1.0, 100.0);

        IGNIVORUS_TAMING_CHANCE_HEARTY = builder
                .comment("Taming chance with hearty meal (lower = easier, 1 = 100% per feed, 100 = 1% per feed)")
                .defineInRange("tamingChanceHearty", 4.0, 1.0, 100.0);

        IGNIVORUS_LEGACY_TAMING = builder
                .comment("Legacy taming: true = simple food taming, false = special mechanics (rodeo/low-health)")
                .define("legacyTaming", false);

        builder.pop();

        COMMON_SPEC = builder.build();
    }
}
