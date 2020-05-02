package xyz.emptydreams.mi.api.tools.property;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.api.event.ItemDamageEvent;
import xyz.emptydreams.mi.api.tools.capabilities.PropertyCapability;

/**
 * 劣质属性.
 * 当玩家使用具有该属性的工具时耐久度有一定可能性损耗更多
 * @author EmptyDreams
 * @version V1.0
 */
@Mod.EventBusSubscriber
public class PoorQuality implements IProperty {
	
	public static PoorQuality randProperty(int min, int max) {
		PoorQuality pq = new PoorQuality();
		pq.setLevel(RANDOM_PROPERTY.nextInt(max - min + 1) + min);
		return pq;
	}

	public static final String NAME = "poor_quality";
	private int level = 1;
	
	@Override
	public String getName() {
		return IProperty.createName(NAME);
	}
	
	@Override
	public String getOriginalName() {
		return NAME;
	}
	
	@Override
	public String getValue() {
		if (level >= 0) {
			return String.valueOf(level);
		} else {
			return "鉴定中...";
		}
	}
	
	public int getLevel() { return level; }
	
	public PoorQuality setLevel(int level) {
		this.level = level;
		return this;
	}
	
	@Override
	public void write(NBTTagCompound compound) {
		compound.setInteger("level", level);
	}
	
	@Override
	public void read(NBTTagCompound compound) {
		level = compound.getInteger("level");
	}
	
	@SubscribeEvent
	public static void onItemDamaged(ItemDamageEvent event) {
		PropertyManager manager = event.stack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return;
		IProperty pr = manager.getProperty(NAME);
		if (pr == null) return;
		if (RANDOM_PROPERTY.nextBoolean()) {
			event.damageItem(((PoorQuality) pr).level);
		}
	}
	
}
