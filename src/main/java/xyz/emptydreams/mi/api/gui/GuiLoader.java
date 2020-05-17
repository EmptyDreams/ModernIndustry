package xyz.emptydreams.mi.api.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import xyz.emptydreams.mi.ModernIndustry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * GUI的总加载器
 * @author EmptyDremas
 * @version V1.0
 */
public class GuiLoader implements IGuiHandler {
	
	public static final int GUI_COMPRESSOR = 1;
	
	/** 存储每个ID及其对应的构建器 */
	private final static Map<Int, IContainerCreater> IDS = new LinkedHashMap<>();
	
	public GuiLoader() {
		NetworkRegistry.INSTANCE.registerGuiHandler(ModernIndustry.instance, this);
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		IContainerCreater creater = IDS.getOrDefault(new Int(ID), null);
		return creater == null ? null : creater.createService(world, player, new BlockPos(x, y, z));
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		IContainerCreater creater = IDS.getOrDefault(new Int(ID), null);
		return creater == null ? null : creater.createClient(world, player, new BlockPos(x, y, z));
		
	}
	
	/** 存储ID自动分配位点 */
	private static int IDnow = 99;
	
	/**
	 * 创建一个ID，由系统自动分配
	 * @return 如果ID分配已经到达数量上限则返回-1
	 */
	public static int register(IContainerCreater creater) {
		int i;
		do {
			i = register(++IDnow, creater);
		} while (i == -1 && IDnow != Integer.MAX_VALUE);
		return i;
	}
	
	/**
	 * 创建指定ID
	 * @param ID 指定ID
	 * @param creater 构建器
	 * @return 当ID无法创建时返回-1
	 */
	private static int register(int ID, IContainerCreater creater) {
		Int id = new Int(ID);
		if (!IDS.containsKey(id)) {
			IDS.put(id, creater);
			return ID;
		}
		else return -1;
	}
	
	private static final class Int implements Comparable<Int> {
		public final int i;
		
		public Int(int i) {
			this.i = i;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Int anInt = (Int) o;
			return i == anInt.i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
		
		@Override
		public int compareTo(GuiLoader.Int o) {
			return Integer.compare(i, o.i);
		}
	}
	
}
