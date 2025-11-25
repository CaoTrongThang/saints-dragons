package com.leon.saintsdragons.neoforge.platform;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.platform.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.SimpleChannel;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class NeoForgeNetworkHelper implements NetworkHelper {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(SaintsDragonsCommon.MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    private enum Direction { SERVERBOUND, CLIENTBOUND }

    private static final class Binding<T> {
        final ResourceLocation id;
        final PacketEncoder<T> encoder;
        final Direction direction;

        Binding(ResourceLocation id, PacketEncoder<T> encoder, Direction direction) {
            this.id = id;
            this.encoder = encoder;
            this.direction = direction;
        }
    }

    private final Map<Class<?>, Binding<?>> bindings = new ConcurrentHashMap<>();
    private final AtomicInteger discriminator = new AtomicInteger();

    @Override
    public <T> void registerServerbound(Class<T> type,
                                        ResourceLocation id,
                                        PacketEncoder<T> encoder,
                                        PacketDecoder<T> decoder,
                                        ServerboundHandler<T> handler) {
        bindings.put(type, new Binding<>(id, encoder, Direction.SERVERBOUND));
        CHANNEL.messageBuilder(type, discriminator.getAndIncrement(), net.neoforged.neoforge.network.registration.NetworkDirection.PLAY_TO_SERVER)
            .encoder(encoder::encode)
            .decoder(buffer -> decoder.decode(buffer))
            .consumerNetworkThread((message, context) -> {
                ServerPlayer player = context.getSender();
                if (player != null) {
                    handler.handle(message, player);
                }
            })
            .add();
    }

    @Override
    public <T> void registerClientbound(Class<T> type,
                                        ResourceLocation id,
                                        PacketEncoder<T> encoder,
                                        PacketDecoder<T> decoder,
                                        ClientboundHandler<T> handler) {
        bindings.put(type, new Binding<>(id, encoder, Direction.CLIENTBOUND));
        CHANNEL.messageBuilder(type, discriminator.getAndIncrement(), net.neoforged.neoforge.network.registration.NetworkDirection.PLAY_TO_CLIENT)
            .encoder(encoder::encode)
            .decoder(buffer -> decoder.decode(buffer))
            .consumerMainThread((message, context) -> handler.handle(message))
            .add();
    }

    @Override
    public void sendToServer(Object message) {
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.SERVERBOUND) {
            throw new IllegalStateException("Attempted to send clientbound packet to server: " + message.getClass());
        }
        CHANNEL.sendToServer(message);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, Object message) {
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.CLIENTBOUND) {
            throw new IllegalStateException("Attempted to send serverbound packet to player: " + message.getClass());
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(player), message);
    }

    @Override
    public void sendToTracking(Entity entity, Object message) {
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.CLIENTBOUND) {
            throw new IllegalStateException("Attempted to send serverbound packet to tracking players: " + message.getClass());
        }
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(entity), message);
    }

    @Override
    public void sendToDimension(Level level, Object message) {
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.CLIENTBOUND) {
            throw new IllegalStateException("Attempted to send serverbound packet to dimension: " + message.getClass());
        }
        CHANNEL.send(PacketDistributor.DIMENSION.with(level.dimension()), message);
    }

    private Binding<Object> bindingFor(Object message) {
        @SuppressWarnings("unchecked")
        Binding<Object> binding = (Binding<Object>) bindings.get(message.getClass());
        if (binding == null) {
            throw new IllegalStateException("No network binding registered for " + message.getClass().getName());
        }
        return binding;
    }
}
