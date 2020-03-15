package minedreams.mi.api.net;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.tileentity.TileEntity;

/**
 * 自动化网络传输注册机.<br>
 * 若TE不在世界加载的范围内，系统会跳过该方块的遍历。<br>
 * 若{@link TileEntity#isInvalid()}返回true，系统会自动取消该TE的注册
 * @author EmptyDreams
 * @version V1.0
 */
public final class NetworkRegister {
	
	/** 服务端 */
	private static final List<IAutoNetwork> NETWORKS_SERVICE = new ArrayList<>(20);
	/** 客户端 */
	private static final List<IAutoNetwork> NETWORKS_CLIENT = new ArrayList<>(20);
	
	static synchronized void forEach(boolean isClient, Consumer<? super IAutoNetwork> consumer) {
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
	
	/**
	 * 注册一个自动化的网络传输
	 * @param net 要注册的对象，必须继承自TileEntity
	 * @throws ClassCastException 如果net不继承自TileEntity
	 */
	public static synchronized void register(IAutoNetwork net) {
		if (net.getWorld().isRemote) {
			for (IAutoNetwork network : NETWORKS_CLIENT)
				if (network == net) return;
			NETWORKS_CLIENT.add(net);
		} else {
			for (IAutoNetwork network : NETWORKS_SERVICE)
				if (network == net) return;
			NETWORKS_SERVICE.add(net);
		}
	}
	
}
