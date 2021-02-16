package xyz.emptydreams.mi.api.gui.common;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import xyz.emptydreams.mi.ModernIndustry;

/**
 * GUI的总加载器
 * @author EmptyDremas
 */
public class GuiLoader implements IGuiHandler {
	
	/** 存储每个ID及其对应的构建器 */
	private final static Int2ObjectMap<IContainerCreater> IDS = new Int2ObjectOpenHashMap<>();
	
	public GuiLoader() {
		NetworkRegistry.INSTANCE.registerGuiHandler(ModernIndustry.instance, this);
		IDS.defaultReturnValue(null);
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		IContainerCreater creator = IDS.get(ID);
		return creator == null ? null : creator.createService(world, player, new BlockPos(x, y, z));
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		IContainerCreater creator = IDS.get(ID);
		return creator == null ? null : creator.createClient(world, player, new BlockPos(x, y, z));
	}
	
	/** 存储ID自动分配位点 */
	private static int IDown = 99;
	
	/**
	 * 创建一个ID，由系统自动分配
	 * @return 如果ID分配已经到达数量上限则返回-1
	 */
	public static int register(IContainerCreater creator) {
		int i;
		do {
			i = register(++IDown, creator);
		} while (i == -1 && IDown != Integer.MAX_VALUE);
		return i;
	}
	
	/**
	 * 创建指定ID
	 * @param ID 指定ID
	 * @param creator 构建器
	 * @return 当ID无法创建时返回-1
	 */
	private static int register(int ID, IContainerCreater creator) {
		if (!IDS.containsKey(ID)) {
			IDS.put(ID, creator);
			return ID;
		}
		else return -1;
	}
}
