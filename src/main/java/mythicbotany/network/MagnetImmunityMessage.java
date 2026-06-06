package mythicbotany.network;

import mythicbotany.MythicBotany;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.moddingx.libx.network.PacketHandler;

public record MagnetImmunityMessage(int entityId, boolean immune, double x, double y, double z) implements CustomPacketPayload {

    public static final Type<MagnetImmunityMessage> TYPE = new Type<>(MythicBotany.getInstance().resource("magnet_immunity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MagnetImmunityMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MagnetImmunityMessage::entityId,
            ByteBufCodecs.BOOL, MagnetImmunityMessage::immune,
            ByteBufCodecs.DOUBLE, MagnetImmunityMessage::x,
            ByteBufCodecs.DOUBLE, MagnetImmunityMessage::y,
            ByteBufCodecs.DOUBLE, MagnetImmunityMessage::z,
            MagnetImmunityMessage::new
    );

    @Override
    public Type<MagnetImmunityMessage> type() {
        return TYPE;
    }
    
    public static class Handler extends PacketHandler<MagnetImmunityMessage> {

        public Handler() {
            super(PacketFlow.CLIENTBOUND, STREAM_CODEC, TYPE);
        }

        @Override
        public void handle(MagnetImmunityMessage msg, IPayloadContext ctx) {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ClientNetworkHandlers.handleMagnetImmunity(msg);
            }
        }
    }
}
