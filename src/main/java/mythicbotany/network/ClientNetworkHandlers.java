package mythicbotany.network;

import mythicbotany.MythicBotany;
import mythicbotany.alfheim.teleporter.AlfheimPortalHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vazkii.botania.client.fx.WispParticleData;

@OnlyIn(Dist.CLIENT)
public class ClientNetworkHandlers {

    public static void handleParticle(ParticleMessage msg) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        ParticleType<?> particle = BuiltInRegistries.PARTICLE_TYPE.get(msg.particleId());
        if (particle instanceof SimpleParticleType simpleParticle) {
            MythicBotany.getNetwork().spawnParticle(level, simpleParticle, msg.amount(), msg.x(), msg.y(), msg.z(), msg.xm(), msg.ym(), msg.zm(), msg.xd(), msg.yd(), msg.zd(), msg.randomizePosition());
        }
    }

    public static void handleInfusion(InfusionMessage msg) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        int ticks = (int) (100.0 * msg.progress());

        int totalSpiritCount = 3;
        double tickIncrement = 360D / totalSpiritCount;

        int speed = 5;
        double wticks = ticks * speed - tickIncrement;

        double r = Math.sin((ticks - 100) / 10D) * 2;
        double g = Math.sin(wticks * Math.PI / 180 * 0.55);

        for (int i = 0; i < totalSpiritCount; i++) {
            double x = msg.pos().getX() + Math.sin(wticks * Math.PI / 180) * r + 0.5;
            double y = msg.pos().getY() + 0.25 + Math.abs(r) * 0.7;
            double z = msg.pos().getZ() + Math.cos(wticks * Math.PI / 180) * r + 0.5;

            wticks += tickIncrement;
            float fromR = ((msg.fromColor() >> 16) & 0xFF) / 255f;
            float fromG = ((msg.fromColor() >> 8) & 0xFF) / 255f;
            float fromB = ((msg.fromColor()) & 0xFF) / 255f;
            float toR = ((msg.toColor() >> 16) & 0xFF) / 255f;
            float toG = ((msg.toColor() >> 8) & 0xFF) / 255f;
            float toB = ((msg.toColor()) & 0xFF) / 255f;
            float[] colorsfx = new float[] {
                    fromR + ((toR - fromR) * (float) msg.progress()),
                    fromG + ((toG - fromG) * (float) msg.progress()),
                    fromB + ((toB - fromB) * (float) msg.progress())
            };
            WispParticleData data = WispParticleData.wisp(0.85F, colorsfx[0], colorsfx[1], colorsfx[2], 0.25F);
            level.addParticle(data, x, y, z, 0, (float) (-g * 0.05), 0);
            data = WispParticleData.wisp((float) Math.random() * 0.1F + 0.1F, colorsfx[0], colorsfx[1], colorsfx[2], 0.9F);
            level.addParticle(data, x, y, z, (float) (Math.random() - 0.5) * 0.05F, (float) (Math.random() - 0.5) * 0.05F, (float) (Math.random() - 0.5) * 0.05F);

            if (ticks == 100) {
                for (int j = 0; j < 15; j++) {
                    data = WispParticleData.wisp((float) Math.random() * 0.15F + 0.15F, colorsfx[0], colorsfx[1], colorsfx[2]);
                    level.addParticle(data, msg.pos().getX() + 0.5, msg.pos().getY() + 0.5, msg.pos().getZ() + 0.5, (float) (Math.random() - 0.5F) * 0.125F, (float) (Math.random() - 0.5F) * 0.125F, (float) (Math.random() - 0.5F) * 0.125F);
                }
            }
        }
    }

    public static void handlePylon(PylonMessage msg) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        WispParticleData data = WispParticleData.wisp(0.85f, 1f, 0.6f, 0f, 0.25f);
        level.addParticle(data, msg.pos().getX() + 0.25 + (level.random.nextFloat() / 2), msg.pos().getY() + 0.75 + (level.random.nextFloat() / 4), msg.pos().getZ() + 0.25 + (level.random.nextFloat() / 2), 0, 0.3, 0);
    }

    public static void handleMagnetImmunity(MagnetImmunityMessage msg) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            Entity entity = level.getEntity(msg.entityId());
            if (entity != null) {
                entity.getPersistentData().putBoolean("PreventRemoteMovement", msg.immune());
                entity.setPos(msg.x(), msg.y(), msg.z());
            }
        }
    }

    public static void handlePortalTime(UpdatePortalTimeMessage msg) {
        AlfheimPortalHandler.clientInPortalTime = msg.portalTime();
    }
}
