package com.leon.saintsdragons.neoforge.platform;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.neoforge.NeoForgeModContext;
import com.leon.saintsdragons.platform.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class NeoForgeNetworkHelper implements NetworkHelper {
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
    private PayloadRegistrar registrar;

    public NeoForgeNetworkHelper() {
        // Register payload handlers on the mod event bus
        NeoForgeModContext.getModEventBus().addListener(this::onRegisterPayloads);
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        this.registrar = event.registrar(SaintsDragonsCommon.MOD_ID);
    }

    @Override
    public <T> void registerServerbound(Class<T> type,
                                        ResourceLocation id,
                                        PacketEncoder<T> encoder,
                                        PacketDecoder<T> decoder,
                                        ServerboundHandler<T> handler) {
        bindings.put(type, new Binding<>(id, encoder, Direction.SERVERBOUND));

        // Create a wrapper payload type
        CustomPacketPayload.Type<PayloadWrapper<T>> payloadType = new CustomPacketPayload.Type<>(id);

        // Create the stream codec
        StreamCodec<FriendlyByteBuf, PayloadWrapper<T>> codec = StreamCodec.of(
            (buf, wrapper) -> encoder.encode(wrapper.message, buf),
            buf -> new PayloadWrapper<>(payloadType, decoder.decode(buf))
        );

        // Register using the deferred approach to ensure registrar is available
        NeoForgeModContext.getModEventBus().addListener((RegisterPayloadHandlersEvent e) -> {
            PayloadRegistrar r = e.registrar(SaintsDragonsCommon.MOD_ID);
            r.playToServer(
                payloadType,
                codec,
                (wrapper, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) {
                        handler.handle(wrapper.message, player);
                    }
                })
            );
        });
    }

    @Override
    public <T> void registerClientbound(Class<T> type,
                                        ResourceLocation id,
                                        PacketEncoder<T> encoder,
                                        PacketDecoder<T> decoder,
                                        ClientboundHandler<T> handler) {
        bindings.put(type, new Binding<>(id, encoder, Direction.CLIENTBOUND));

        // Create a wrapper payload type
        CustomPacketPayload.Type<PayloadWrapper<T>> payloadType = new CustomPacketPayload.Type<>(id);

        // Create the stream codec
        StreamCodec<FriendlyByteBuf, PayloadWrapper<T>> codec = StreamCodec.of(
            (buf, wrapper) -> encoder.encode(wrapper.message, buf),
            buf -> new PayloadWrapper<>(payloadType, decoder.decode(buf))
        );

        // Register using the deferred approach to ensure registrar is available
        NeoForgeModContext.getModEventBus().addListener((RegisterPayloadHandlersEvent e) -> {
            PayloadRegistrar r = e.registrar(SaintsDragonsCommon.MOD_ID);
            r.playToClient(
                payloadType,
                codec,
                (wrapper, context) -> context.enqueueWork(() -> handler.handle(wrapper.message))
            );
        });
    }

    @Override
    public void sendToServer(Object message) {
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.SERVERBOUND) {
            throw new IllegalStateException("Attempted to send clientbound packet to server: " + message.getClass());
        }
        CustomPacketPayload.Type<PayloadWrapper<Object>> type = new CustomPacketPayload.Type<>(binding.id);
        PayloadWrapper<Object> wrapper = new PayloadWrapper<>(type, message);
        PacketDistributor.sendToServer(wrapper);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, Object message) {
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.CLIENTBOUND) {
            throw new IllegalStateException("Attempted to send serverbound packet to player: " + message.getClass());
        }
        CustomPacketPayload.Type<PayloadWrapper<Object>> type = new CustomPacketPayload.Type<>(binding.id);
        PayloadWrapper<Object> wrapper = new PayloadWrapper<>(type, message);
        PacketDistributor.sendToPlayer(player, wrapper);
    }

    @Override
    public void sendToTracking(Entity entity, Object message) {
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.CLIENTBOUND) {
            throw new IllegalStateException("Attempted to send serverbound packet to tracking players: " + message.getClass());
        }
        CustomPacketPayload.Type<PayloadWrapper<Object>> type = new CustomPacketPayload.Type<>(binding.id);
        PayloadWrapper<Object> wrapper = new PayloadWrapper<>(type, message);
        PacketDistributor.sendToPlayersTrackingEntity(entity, wrapper);
    }

    @Override
    public void sendToDimension(Level level, Object message) {
        Binding<Object> binding = bindingFor(message);
        if (binding.direction != Direction.CLIENTBOUND) {
            throw new IllegalStateException("Attempted to send serverbound packet to dimension: " + message.getClass());
        }
        CustomPacketPayload.Type<PayloadWrapper<Object>> type = new CustomPacketPayload.Type<>(binding.id);
        PayloadWrapper<Object> wrapper = new PayloadWrapper<>(type, message);
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersInDimension(serverLevel, wrapper);
        }
    }

    private Binding<Object> bindingFor(Object message) {
        @SuppressWarnings("unchecked")
        Binding<Object> binding = (Binding<Object>) bindings.get(message.getClass());
        if (binding == null) {
            throw new IllegalStateException("No network binding registered for " + message.getClass().getName());
        }
        return binding;
    }

    // Wrapper class that implements CustomPacketPayload
    private static final class PayloadWrapper<T> implements CustomPacketPayload {
        private final Type<PayloadWrapper<T>> type;
        private final T message;

        PayloadWrapper(Type<PayloadWrapper<T>> type, T message) {
            this.type = type;
            this.message = message;
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return type;
        }
    }
}
