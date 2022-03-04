package top.kmar.mi.api.gui.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.event.GuiRegistryEvent;
import top.kmar.mi.api.register.others.AutoLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI的总加载器
 * @author EmptyDremas
 */
@AutoLoader
public class GuiLoader implements IGuiHandler {
	
	/** 是否允许注册 */
	@SuppressWarnings("UnusedAssignment")
	private static boolean canRegistry = true;
	/** 存储每个ID及其对应的构建器 */
	private final static List<Node> INSTANCE = new ArrayList<>();
	
	static {
		MinecraftForge.EVENT_BUS.post(new GuiRegistryEvent());
		INSTANCE.sort(GuiLoader::compareNode);
		canRegistry = false;
	}
	
	public GuiLoader() {
		NetworkRegistry.INSTANCE.registerGuiHandler(ModernIndustry.instance, this);
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		IContainerCreater creator = INSTANCE.get(ID).getCreater();
		return creator == null ? null : creator.createService(world, player, new BlockPos(x, y, z));
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		IContainerCreater creator = INSTANCE.get(ID).getCreater();
		return creator == null ? null : creator.createClient(world, player, new BlockPos(x, y, z));
	}
	
	/**
	 * 通过KEY值获取GUI对应的ID
	 */
	public static int getID(ResourceLocation key) {
		if (canRegistry) throw new AssertionError("不应该在此时获取GUI的ID");
		for (int i = 0; i < INSTANCE.size(); i++) {
			if (INSTANCE.get(i).getKey().equals(key)) return i;
		}
		throw new IllegalArgumentException("key[" + key + "]值不存在");
	}
	
	/**
	 * 注册一个GUI.
	 * <b>切勿使用该方法手动注册，不然可能导致出现异常</b>
	 * @param key GUI的名称，用于打开GUI
	 * @param creator GUI构造器
	 * @return 传入的Key值
	 * @deprecated 请使用GuiRegistryEvent事件进行注册
	 */
	@Deprecated
	public static ResourceLocation registry(ResourceLocation key, IContainerCreater creator) {
		if (!canRegistry) throw new AssertionError("不应该在此时注册GUI");
		Node node = new Node(key, creator);
		INSTANCE.remove(node);
		INSTANCE.add(node);
		return key;
	}
	
	private static int compareNode(Node arg0, Node arg1) {
		return arg0.compareTo(arg1);
	}
	
	private static final class Node implements Comparable<Node> {
		
		private final IContainerCreater creater;
		private final ResourceLocation key;
		
		Node(ResourceLocation key, IContainerCreater creater) {
			this.key = key;
			this.creater = creater;
		}
		
		public IContainerCreater getCreater() { return creater; }
		public ResourceLocation getKey() { return key; }
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Node node = (Node) o;
			return key.equals(node.key);
		}
		
		@Override
		public int hashCode() {
			return key.hashCode();
		}
		
		@Override
		public int compareTo(Node o) {
			return key.compareTo(o.key);
		}
		
		@Override
		public String toString() {
			return "key=" + key;
		}
	}
	
}