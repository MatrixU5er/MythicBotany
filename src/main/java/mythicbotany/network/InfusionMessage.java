package mythicbotany.network;

import mythicbotany.MythicBotany;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.moddingx.libx.network.PacketHandler;

public record InfusionMessage(BlockPos pos, double progress, int fromColor, int toColor) implements CustomPacketPayload {

    public static final Type<InfusionMessage> TYPE = new Type<>(MythicBotany.getInstance().resource("infusion"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InfusionMessage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, InfusionMessage::pos,
            ByteBufCodecs.DOUBLE, InfusionMessage::progress,
            ByteBufCodecs.VAR_INT, InfusionMessage::fromColor,
            ByteBufCodecs.VAR_INT, InfusionMessage::toColor,
            InfusionMessage::new
    );
    
    @Override
    public Type<InfusionMessage> type() {
        return TYPE;
    }
    
    public static class Handler extends PacketHandler<InfusionMessage> {

        public Handler() {
            super(PacketFlow.CLIENTBOUND, STREAM_CODEC, TYPE);
        }

        @Override
        public void handle(InfusionMessage msg, IPayloadContext ctx) {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ClientNetworkHandlers.handleInfusion(msg);
            }
        }
    }
}
