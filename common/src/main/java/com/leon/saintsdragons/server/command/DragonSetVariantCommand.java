package com.leon.saintsdragons.server.command;

import com.leon.saintsdragons.server.entity.base.DragonEntity;
import com.leon.saintsdragons.server.entity.dragons.cindervane.Cindervane;
import com.leon.saintsdragons.server.entity.dragons.ignivorus.Ignivorus;
import com.leon.saintsdragons.server.entity.dragons.raevyx.Raevyx;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Command to change dragon texture variants.
 * Supported:
 * Ignivorus: default|crimson
 * Cindervane: default|albino
 * Raevyx: default|night_gold
 */
public final class DragonSetVariantCommand {
    private static final double HIT_RANGE = 64.0D;

    private static final SuggestionProvider<CommandSourceStack> DRAGON_UUID_SUGGESTIONS = (context, builder) -> {
        CommandSourceStack source = context.getSource();
        Set<DragonEntity> ordered = new LinkedHashSet<>();

        DragonEntity lookedAt = findLookedAtSupportedDragon(source);
        if (lookedAt != null) {
            ordered.add(lookedAt);
        }

        for (DragonEntity dragon : ordered) {
            builder.suggest(dragon.getUUID().toString(), dragon.getDisplayName());
        }

        return builder.buildFuture();
    };

    private static final SuggestionProvider<CommandSourceStack> VARIANT_SUGGESTIONS = (context, builder) ->
        SharedSuggestionProvider.suggest(new String[]{"default", "crimson", "albino", "night_gold"}, builder);

    private static final DynamicCommandExceptionType ERROR_UNKNOWN_DRAGON =
        new DynamicCommandExceptionType(id -> Component.translatable("saintsdragons.command.setvariant.not_found", id));

    private static final SimpleCommandExceptionType ERROR_INVALID_VARIANT =
        new SimpleCommandExceptionType(Component.translatable("saintsdragons.command.setvariant.invalid_variant"));

    private static final SimpleCommandExceptionType ERROR_NOT_SUPPORTED_DRAGON =
        new SimpleCommandExceptionType(Component.translatable("saintsdragons.command.setvariant.not_supported"));

    private DragonSetVariantCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setvariant")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("dragon", uuidArgument())
                .suggests(DRAGON_UUID_SUGGESTIONS)
                .then(Commands.argument("variant", StringArgumentType.word())
                    .suggests(VARIANT_SUGGESTIONS)
                    .executes(DragonSetVariantCommand::setVariant))));
    }

    private static ArgumentType<UUID> uuidArgument() {
        return UuidArgument.uuid();
    }

    private static int setVariant(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        UUID dragonId = UuidArgument.getUuid(context, "dragon");
        String variantStr = StringArgumentType.getString(context, "variant").toLowerCase();
        CommandSourceStack source = context.getSource();

        // Find dragon
        DragonEntity dragon = findDragon(source, dragonId);
        if (dragon == null) {
            throw ERROR_UNKNOWN_DRAGON.create(dragonId.toString());
        }

        int oldVariant;
        int newVariant;
        if (dragon instanceof Ignivorus ignivorus) {
            oldVariant = ignivorus.getTextureVariant();
            newVariant = parseVariantForIgnivorus(variantStr);
            ignivorus.setTextureVariant(newVariant);
        } else if (dragon instanceof Cindervane cindervane) {
            oldVariant = cindervane.getTextureVariant();
            newVariant = parseVariantForCindervane(variantStr);
            cindervane.setTextureVariant(newVariant);
        } else if (dragon instanceof Raevyx raevyx) {
            oldVariant = raevyx.getTextureVariant();
            newVariant = parseVariantForRaevyx(variantStr);
            raevyx.setTextureVariant(newVariant);
        } else {
            throw ERROR_NOT_SUPPORTED_DRAGON.create();
        }

        // Send success message
        Component successMessage = Component.translatable(
            "saintsdragons.command.setvariant.success",
            dragon.getDisplayName(),
            Component.translatable("saintsdragons.variant." + variantStr)
        );
        source.sendSuccess(() -> successMessage, false);

        // Info message if variant didn't change
        if (oldVariant == newVariant) {
            Component infoMessage = Component.translatable(
                "saintsdragons.command.setvariant.unchanged",
                dragon.getDisplayName()
            );
            source.sendSuccess(() -> infoMessage, false);
        }

        return 1;
    }

    private static DragonEntity findDragon(CommandSourceStack source, UUID id) {
        Entity entity = source.getLevel().getEntity(id);
        if (entity instanceof DragonEntity dragon) {
            return dragon;
        }
        return null;
    }

    private static DragonEntity findLookedAtSupportedDragon(CommandSourceStack source) {
        Entity sourceEntity = source.getEntity();
        if (!(sourceEntity instanceof LivingEntity living)) {
            return null;
        }

        Vec3 start = living.getEyePosition();
        Vec3 look = living.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(HIT_RANGE));
        AABB box = living.getBoundingBox().expandTowards(look.scale(HIT_RANGE)).inflate(1.0D);

        EntityHitResult result = ProjectileUtil.getEntityHitResult(
            living.level(),
            living,
            start,
            end,
            box,
            target -> isSupportedDragon(target) && target.isPickable()
        );

        if (result != null && result.getEntity() instanceof DragonEntity dragon) {
            return dragon;
        }
        return null;
    }

    private static boolean isSupportedDragon(Entity entity) {
        return entity instanceof Ignivorus || entity instanceof Cindervane || entity instanceof Raevyx;
    }

    private static int parseVariantForIgnivorus(String variant) throws CommandSyntaxException {
        return switch (variant) {
            case "default" -> 0;
            case "crimson" -> 1;
            default -> throw ERROR_INVALID_VARIANT.create();
        };
    }

    private static int parseVariantForCindervane(String variant) throws CommandSyntaxException {
        return switch (variant) {
            case "default" -> Cindervane.VARIANT_DEFAULT;
            case "albino" -> Cindervane.VARIANT_ALBINO;
            default -> throw ERROR_INVALID_VARIANT.create();
        };
    }

    private static int parseVariantForRaevyx(String variant) throws CommandSyntaxException {
        return switch (variant) {
            case "default" -> Raevyx.VARIANT_DEFAULT;
            case "night_gold" -> Raevyx.VARIANT_NIGHT_GOLD;
            default -> throw ERROR_INVALID_VARIANT.create();
        };
    }
}
