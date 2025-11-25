package com.leon.saintsdragons.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

/**
 * Data payload stored on dragon binder items.
 */
public record BinderData(Optional<UUID> dragonUuid,
                         Optional<String> dragonName,
                         Optional<UUID> ownerUuid,
                         Optional<String> ownerName,
                         Optional<String> customName,
                         Optional<CompoundTag> dragonData) {
    public static final BinderData EMPTY = new BinderData(Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    public static final Codec<BinderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(UUID::fromString, UUID::toString).optionalFieldOf("dragon_uuid").forGetter(BinderData::dragonUuid),
            Codec.STRING.optionalFieldOf("dragon_name").forGetter(BinderData::dragonName),
            Codec.STRING.xmap(UUID::fromString, UUID::toString).optionalFieldOf("owner_uuid").forGetter(BinderData::ownerUuid),
            Codec.STRING.optionalFieldOf("owner_name").forGetter(BinderData::ownerName),
            Codec.STRING.optionalFieldOf("custom_name").forGetter(BinderData::customName),
            CompoundTag.CODEC.optionalFieldOf("dragon_data").forGetter(BinderData::dragonData)
    ).apply(instance, BinderData::new));

    public static final StreamCodec<FriendlyByteBuf, BinderData> STREAM_CODEC = StreamCodec.of(
            BinderData::encode,
            BinderData::decode
    );

    public static BinderData bound(UUID dragonUuid,
                                   String dragonName,
                                   UUID ownerUuid,
                                   String ownerName,
                                   Component customName,
                                   CompoundTag dragonData) {
        return new BinderData(
                Optional.ofNullable(dragonUuid),
                Optional.ofNullable(dragonName),
                Optional.ofNullable(ownerUuid),
                Optional.ofNullable(ownerName),
                Optional.ofNullable(customName).map(Component::getString),
                Optional.ofNullable(dragonData)
        );
    }

    public Optional<Component> customNameComponent() {
        return customName.map(Component::literal);
    }

    public boolean isBound() {
        return dragonUuid.isPresent();
    }

    private static void encode(FriendlyByteBuf buf, BinderData data) {
        buf.writeBoolean(data.dragonUuid.isPresent());
        data.dragonUuid.ifPresent(buf::writeUUID);

        buf.writeBoolean(data.dragonName.isPresent());
        data.dragonName.ifPresent(name -> buf.writeUtf(name, 64));

        buf.writeBoolean(data.ownerUuid.isPresent());
        data.ownerUuid.ifPresent(buf::writeUUID);

        buf.writeBoolean(data.ownerName.isPresent());
        data.ownerName.ifPresent(name -> buf.writeUtf(name, 64));

        buf.writeBoolean(data.customName.isPresent());
        data.customName.ifPresent(name -> buf.writeUtf(name, 64));

        buf.writeBoolean(data.dragonData.isPresent());
        data.dragonData.ifPresent(buf::writeNbt);
    }

    private static BinderData decode(FriendlyByteBuf buf) {
        Optional<UUID> dragonUuid = buf.readBoolean() ? Optional.of(buf.readUUID()) : Optional.empty();
        Optional<String> dragonName = buf.readBoolean() ? Optional.of(buf.readUtf(64)) : Optional.empty();
        Optional<UUID> ownerUuid = buf.readBoolean() ? Optional.of(buf.readUUID()) : Optional.empty();
        Optional<String> ownerName = buf.readBoolean() ? Optional.of(buf.readUtf(64)) : Optional.empty();
        Optional<String> customName = buf.readBoolean() ? Optional.of(buf.readUtf(64)) : Optional.empty();
        Optional<CompoundTag> dragonData = buf.readBoolean() ? Optional.ofNullable(buf.readNbt()) : Optional.empty();
        return new BinderData(dragonUuid, dragonName, ownerUuid, ownerName, customName, dragonData);
    }
}
