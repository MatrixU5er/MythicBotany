package mythicbotany.alftools;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.moddingx.libx.creativetab.CreativeTabItemProvider;
import vazkii.botania.common.item.equipment.bauble.BandOfManaItem;

import java.util.stream.Stream;

public class GreatestManaRing extends BandOfManaItem implements CreativeTabItemProvider {

    public static final int MAX_MANA = 4000000;

    public GreatestManaRing(Properties props) {
        super(props);
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public Stream<ItemStack> makeCreativeTabStacks() {
        ItemStack full = new ItemStack(this);
        setMana(full, MAX_MANA);
        return Stream.of(new ItemStack(this), full);
    }
}
