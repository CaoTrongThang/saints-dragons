package com.leon.saintsdragons.fabric.platform;

import com.leon.saintsdragons.platform.NetworkHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FabricNetworkHelper implements NetworkHelper {
    private enum Direction {
        SERVERBOUND,
        CLIENTBOUND
    }

    private static final class PayloadWrapper<T> implements CustomPacketPayload {
        private final Type<PayloadWrapper<T>> type;
        private final T message;

        private PayloadWrapper(Type<PayloadWrapper<T>> type, T message) {
            this.type = type;
            this.message = message;
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return type;
        }
    }

    private static final class Binding<T> {
        final CustomPacketPayload.Type<PayloadWrapper<T>> type;
        final PacketEncoder<T> encoder;
        final Direction direction;

        Binding(CustomPacketPayload.Type<PayloadWrapper<T>> type, PacketEncoder<T> encoder, Direction direction) {
            this.type = type;
            this.encoder = encoder;
            this.direction = direction;
        }
    }

    private final Map<Class<?>, Binding<?>> bindings = new ConcurrentHashMap<>();

    @Override
    public <T> void registerServerbound(Class<T> type,
                                        ResourceLocation id,
                                        PacketEncoder<T> encoder,
                                        PacketDecoder<T> decoder,
                                        ServerboundHandler<T> handler) {
        CustomPacketPayload.Type<PayloadWrapper<T>> payloadType = new CustomPacketPayload.Type<>(id);
        bindings.put(type, new Binding<>(payloadType, encoder, Direction.SERVERBOUND));

        StreamCodec<FriendlyByteBuf, PayloadWrapper<T>> codec = StreamCodec.of(
            (buf, wrapper) -> encoder.encode(wrapper.message, buf),
            buf -> new PayloadWrapper<>(payloadType, decoder.decode(buf))
        );

        // Register payload type/codec before wiring handlers so Fabric knows about the channel
        PayloadTypeRegistry.playC2S().register(payloadType, codec);

        ServerPlayNetworking.registerGlobalReceiver(payloadType,
            (payload, context) -> context.server().execute(() -> handler.handle(payload.message, context.player())));
    }

    @Override
    public <T> void registerClientbound(Class<T> type,
                                        ResourceLocation id,
                                        PacketEncoder<T> encoder,
                                        PacketDecoder<T> decoder,
                                        ClientboundHandler<T> handler) {
        CustomPacketPayload.Type<PayloadWrapper<T>> payloadType = new CustomPacketPayload.Type<>(id);
        bindings.put(type, new Binding<>(payloadType, encoder, Direction.CLIENTBOUND));

        StreamCodec<FriendlyByteBuf, PayloadWrapper<T>> codec = StreamCodec.of(
            (buf, wrapper) -> encoder.encode(wrapper.message, buf),
            buf -> new PayloadWrapper<>(payloadType, decoder.decode(buf))
        );

        // Must be registered on both logical sides
        PayloadTypeRegistry.playS2C().register(payloadType, codec);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientAccess.register(payloadType, handler);
        }
    }

    @Override
    public void sendToServer(Object message) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            throw new IllegalStateException("Client-only networking method invoked on a non-client environment");
        }
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.SERVERBOUND) {
            throw new IllegalStateException("Attempted to send clientbound packet to server: " + message.getClass());
        }
        ClientAccess.send(new PayloadWrapper<>(binding.type, message));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, Object message) {
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.CLIENTBOUND) {
            throw new IllegalStateException("Attempted to send serverbound packet to player: " + message.getClass());
        }
        ServerPlayNetworking.send(player, new PayloadWrapper<>(binding.type, message));
    }

    @Override
    public void sendToTracking(Entity entity, Object message) {
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.CLIENTBOUND) {
            throw new IllegalStateException("Attempted to send serverbound packet to tracking players: " + message.getClass());
        }
        PayloadWrapper<Object> payload = new PayloadWrapper<>(binding.type, message);
        for (ServerPlayer tracking : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(tracking, payload);
        }
    }

    @Override
    public void sendToDimension(Level level, Object message) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.CLIENTBOUND) {
            throw new IllegalStateException("Attempted to send serverbound packet to dimension: " + message.getClass());
        }
        PayloadWrapper<Object> payload = new PayloadWrapper<>(binding.type, message);
        for (ServerPlayer player : PlayerLookup.world(serverLevel)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    private Binding<Object> bindingFor(Object message) {
        Class<?> messageClass = message.getClass();
        @SuppressWarnings("unchecked")
        Binding<Object> binding = (Binding<Object>) bindings.get(messageClass);
        if (binding == null) {
            throw new IllegalStateException("No network binding registered for " + messageClass.getName());
        }
        return binding;
    }

    @Environment(EnvType.CLIENT)
    private static final class ClientAccess {
        private ClientAccess() {}

        private static <T> void register(CustomPacketPayload.Type<PayloadWrapper<T>> payloadType,
                                         ClientboundHandler<T> handler) {
            ClientPlayNetworking.registerGlobalReceiver(payloadType,
                (payload, context) -> context.client().execute(() -> handler.handle(payload.message)));
        }

        private static void send(CustomPacketPayload payload) {
            ClientPlayNetworking.send(payload);
        }
    }
}
