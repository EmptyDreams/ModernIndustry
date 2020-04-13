package xyz.emptydreams.mi.api.net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

import xyz.emptydreams.mi.api.net.guinet.IAutoGuiNetWork;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * 自动化网络传输注册机.<br>
 * 若TE不在世界加载的范围内，系统会跳过该方块的遍历。<br>
 * 若{@link TileEntity#isInvalid()}返回true，系统会自动取消该TE的注册
 * @author EmptyDreams
 * @version V1.1
 */
public final class NetworkRegister {
	
	/** 服务端-方块 */
	private static final List<IAutoNetwork> NETWORKS_SERVICE = new ArrayList<>(20);
	/** 客户端-方块 */
	private static final List<IAutoNetwork> NETWORKS_CLIENT = new ArrayList<>(20);
	/** 待注册-方块 */
	private static final LinkedList<IAutoNetwork> WAIT_BLOCK = new LinkedList<>();
	/** 列表-GUI */
	private static final List<IAutoGuiNetWork> GUINET = new ArrayList<>(10);
	
	/**
	 * 注册一个GUI的自动化网络传输，客户端无需注册
	 * @param net 要注册的对象，必须继承自Container或GuiContainer
	 * @throws ClassCastException 如果net不继承自Container
	 */
	public static void register(IAutoGuiNetWork net) {
		synchronized (GUINET) {
			if (net instanceof Container) {
				if (!net.isClient()) GUINET.add(net);
			} else {
				throw new ClassCastException("'net' is not extends Container!");
			}
		}
	}
	
	/**
	 * 注册一个自动化的网络传输
	 * @param net 要注册的对象，必须继承自TileEntity
	 * @throws ClassCastException 如果net不继承自TileEntity
	 */
	public static void register(IAutoNetwork net) {
		if (!(net instanceof TileEntity)) throw new ClassCastException("'net' is not extends TileEntity!");
		World world = ((TileEntity) net).getWorld();
		if (world == null) {
			synchronized (WAIT_BLOCK) {
				WAIT_BLOCK.add(net);
			}
		} else if (world.isRemote) {
			synchronized (NETWORKS_CLIENT) {
				NETWORKS_CLIENT.add(net);
			}
		} else {
			synchronized (NETWORKS_SERVICE) {
				NETWORKS_SERVICE.add(net);
			}
		}
	}
	
	static void forEachGui(boolean isClient, Consumer<? super IAutoGuiNetWork> consumer) {
		synchronized (GUINET) {
			IAutoGuiNetWork network;
			Iterator<IAutoGuiNetWork> it = GUINET.iterator();
			while (it.hasNext()) {
				network = it.next();
				if (network.isLive()) {
					if (network.isClient() == isClient) {
						consumer.accept(network);
					}
				} else {
					it.remove();
				}
			}
		}
	}
	
	static void forEachBlock(boolean isClient, Consumer<? super IAutoNetwork> consumer) {
		registerAllBlock();
		IAutoNetwork network;
		TileEntity entity;
		if (isClient) {
			synchronized (NETWORKS_CLIENT) {
				Iterator<IAutoNetwork> it = NETWORKS_CLIENT.iterator();
				while (it.hasNext()) {
					network = it.next();
					entity = (TileEntity) network;
					if (entity.isInvalid()) {
						it.remove();
						continue;
					}
					if (entity.getWorld().isBlockLoaded(entity.getPos()))
						consumer.accept(network);
				}
			}
		} else {
			synchronized (NETWORKS_SERVICE) {
				Iterator<IAutoNetwork> it = NETWORKS_SERVICE.iterator();
				while (it.hasNext()) {
					network = it.next();
					entity = (TileEntity) network;
					if (entity.isInvalid()) {
						it.remove();
						continue;
					}
					if (entity.getWorld().isBlockLoaded(entity.getPos()))
						consumer.accept(network);
				}
			}
		}
	}
	
	private static void registerAllBlock() {
		synchronized (WAIT_BLOCK) {
			ListIterator<IAutoNetwork> it = WAIT_BLOCK.listIterator();
			IAutoNetwork network;
			TileEntity entity;
			while (it.hasNext()) {
				network = it.next();
				entity = (TileEntity) network;
				if (entity.getWorld() != null) {
					if (entity.getWorld().isRemote) {
						NETWORKS_CLIENT.add(network);
					} else {
						NETWORKS_SERVICE.add(network);
					}
					it.remove();
				}
			}
		}
	}
	
}
