package mythicbotany.network;

import mythicbotany.MythicBotany;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.moddingx.libx.network.PacketHandler;

public record ParticleMessage(ResourceLocation particleId, double x, double y, double z, int amount, double xm, double ym, double zm, double xd, double yd, double zd, boolean randomizePosition) implements CustomPacketPayload {

    public static final Type<ParticleMessage> TYPE = new Type<>(MythicBotany.getInstance().resource("particle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleMessage> STREAM_CODEC = CustomPacketPayload.codec(ParticleMessage::write, ParticleMessage::read);
    
    private static void write(ParticleMessage msg, RegistryFriendlyByteBuf buffer) {
        ResourceLocation.STREAM_CODEC.encode(buffer, msg.particleId());
        buffer.writeDouble(msg.x());
        buffer.writeDouble(msg.y());
        buffer.writeDouble(msg.z());
        buffer.writeVarInt(msg.amount());
        buffer.writeDouble(msg.xm());
        buffer.writeDouble(msg.ym());
        buffer.writeDouble(msg.zm());
        buffer.writeDouble(msg.xd());
        buffer.writeDouble(msg.yd());
        buffer.writeDouble(msg.zd());
        buffer.writeBoolean(msg.randomizePosition());
    }

    private static ParticleMessage read(RegistryFriendlyByteBuf buffer) {
        ResourceLocation particleId = ResourceLocation.STREAM_CODEC.decode(buffer);
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        int amount = buffer.readVarInt();
        double xm = buffer.readDouble();
        double ym = buffer.readDouble();
        double zm = buffer.readDouble();
        double xd = buffer.readDouble();
        double yd = buffer.readDouble();
        double zd = buffer.readDouble();
        boolean randomizePosition = buffer.readBoolean();
        return new ParticleMessage(particleId, x, y, z, amount, xm, ym, zm, xd, yd, zd, randomizePosition);
    }

    @Override
    public Type<ParticleMessage> type() {
        return TYPE;
    }
    
    public static class Handler extends PacketHandler<ParticleMessage> {

        public Handler() {
            super(TYPE, PacketFlow.CLIENTBOUND, STREAM_CODEC, HandlerThread.MAIN);
        }

        @Override
        public void handle(ParticleMessage msg, IPayloadContext ctx) {
            ClientNetworkDispatch.dispatch("handleParticle", msg);
        }
    }
}
