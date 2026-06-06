package mythicbotany.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import javax.annotation.Nonnull;
import java.util.Optional;

public class MjoellnirTrigger extends SimpleCriterionTrigger<MjoellnirTrigger.Instance> {

    @Nonnull
    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack item, Entity entity) {
        LootContext ctx = EntityPredicate.createContext(player, entity);
        this.trigger(player, instance -> instance.matches(item, ctx));
    }

    public record Instance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<ContextAwarePredicate> entity) implements SimpleInstance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Instance::item),
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(Instance::entity)
        ).apply(instance, Instance::new));

        public Instance(ItemPredicate item, ContextAwarePredicate entity) {
            this(Optional.empty(), Optional.of(item), Optional.of(entity));
        }

        public boolean matches(ItemStack itemStack, LootContext entityContext) {
            return this.item.map(predicate -> predicate.test(itemStack)).orElse(true)
                    && this.entity.map(predicate -> predicate.matches(entityContext)).orElse(true);
        }
    }
}
