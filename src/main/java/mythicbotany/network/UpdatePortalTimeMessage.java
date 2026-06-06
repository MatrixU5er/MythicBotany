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

public record UpdatePortalTimeMessage(int portalTime) implements CustomPacketPayload {

    public static final Type<UpdatePortalTimeMessage> TYPE = new Type<>(MythicBotany.getInstance().resource("update_portal_time"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdatePortalTimeMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, UpdatePortalTimeMessage::portalTime,
            UpdatePortalTimeMessage::new
    );
    
    @Override
    public Type<UpdatePortalTimeMessage> type() {
        return TYPE;
    }
    
    public static class Handler extends PacketHandler<UpdatePortalTimeMessage> {

        public Handler() {
            super(PacketFlow.CLIENTBOUND, STREAM_CODEC, TYPE);
        }

        @Override
        public void handle(UpdatePortalTimeMessage msg, IPayloadContext ctx) {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ClientNetworkHandlers.handlePortalTime(msg);
            }
        }
    }
}
