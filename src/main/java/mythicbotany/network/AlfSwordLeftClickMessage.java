package mythicbotany.network;

import mythicbotany.MythicBotany;
import mythicbotany.alftools.AlfsteelSword;
import mythicbotany.register.ModItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.moddingx.libx.network.PacketHandler;

public record AlfSwordLeftClickMessage() implements CustomPacketPayload {

    public static final Type<AlfSwordLeftClickMessage> TYPE = new Type<>(MythicBotany.getInstance().resource("alf_sword_left_click"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AlfSwordLeftClickMessage> STREAM_CODEC = StreamCodec.unit(new AlfSwordLeftClickMessage());
    
    @Override
    public Type<AlfSwordLeftClickMessage> type() {
        return TYPE;
    }
    
    public static class Handler extends PacketHandler<AlfSwordLeftClickMessage> {

        public Handler() {
            super(PacketFlow.SERVERBOUND, STREAM_CODEC, TYPE);
        }

        @Override
        public void handle(AlfSwordLeftClickMessage msg, IPayloadContext ctx) {
            var sender = ctx.player();
            if (sender != null && (sender.getItemBySlot(EquipmentSlot.MAINHAND).getItem() == ModItems.alfsteelSword || sender.getItemBySlot(EquipmentSlot.OFFHAND).getItem() == ModItems.alfsteelSword)) {
                ((AlfsteelSword) ModItems.alfsteelSword).trySpawnAlfBurst(sender);
            }
        }
    }
}
