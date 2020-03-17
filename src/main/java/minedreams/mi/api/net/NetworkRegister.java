package minedreams.mi.api.net;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

import net.minecraft.tileentity.TileEntity;

/**
 * 自动化网络传输注册机.<br>
 * 若TE不在世界加载的范围内，系统会跳过该方块的遍历。<br>
 * 若{@link TileEntity#isInvalid()}返回true，系统会自动取消该TE的注册
 * @author EmptyDreams
 * @version V1.1
 */
public final class NetworkRegister {
	
	/** 服务端 */
	private static final List<IAutoNetwork> NETWORKS_SERVICE = new ArrayList<>(20);
	/** 客户端 */
	private static final List<IAutoNetwork> NETWORKS_CLIENT = new ArrayList<>(20);
	/** 待注册 */
	private static final LinkedList<IAutoNetwork> WAIT_REGIST = new LinkedList<>();
	
	static void forEach(boolean isClient, Consumer<? super IAutoNetwork> consumer) {
		registerAll();
		IAutoNetwork network;
		TileEntity entity;
		if (isClient) {
			for (int i = 0; i < NETWORKS_CLIENT.size(); ++i) {
				network = NETWORKS_CLIENT.get(i);
				entity = (TileEntity) network;
				if (entity.isInvalid()) {
					NETWORKS_CLIENT.remove(i);
					--i;
					continue;
				}
				if (entity.getWorld().isBlockLoaded(entity.getPos()))
					consumer.accept(network);
			}
		} else {
			for (int i = 0; i < NETWORKS_SERVICE.size(); ++i) {
				network = NETWORKS_SERVICE.get(i);
				entity = (TileEntity) network;
				if (entity.isInvalid()) {
					NETWORKS_SERVICE.remove(i);
					--i;
					continue;
				}
				if (entity.getWorld().isBlockLoaded(entity.getPos()))
					consumer.accept(network);
			}
		}
	}
	
	private static void registerAll() {
		ListIterator<IAutoNetwork> it = WAIT_REGIST.listIterator();
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
	
	/**
	 * 注册一个自动化的网络传输
	 * @param net 要注册的对象，必须继承自TileEntity
	 * @throws ClassCastException 如果net不继承自TileEntity
	 */
	public static void register(IAutoNetwork net) {
		if (!(net instanceof TileEntity)) throw new ClassCastException("'net' is not extends TileEntity!");
		for (IAutoNetwork network : NETWORKS_SERVICE)
			if (network == net) return;
		WAIT_REGIST.add(net);
	}
	
}
