package mythicbotany.network;

import mythicbotany.MythicBotany;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.moddingx.libx.network.PacketHandler;

public record PylonMessage(BlockPos pos) implements CustomPacketPayload {

    public static final Type<PylonMessage> TYPE = new Type<>(MythicBotany.getInstance().resource("pylon"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PylonMessage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PylonMessage::pos,
            PylonMessage::new
    );
    
    @Override
    public Type<PylonMessage> type() {
        return TYPE;
    }
    
    public static class Handler extends PacketHandler<PylonMessage> {

        public Handler() {
            super(TYPE, PacketFlow.CLIENTBOUND, STREAM_CODEC, HandlerThread.MAIN);
        }

        @Override
        public void handle(PylonMessage msg, IPayloadContext ctx) {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ClientNetworkHandlers.handlePylon(msg);
            }
        }
    }
}
