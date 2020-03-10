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
	
	static final List<IAutoNetwork> NETWORKS = new ArrayList<>();
	
	static synchronized void forEach(Consumer<? super IAutoNetwork> consumer) {
		IAutoNetwork network;
		for (int i = 0; i < NETWORKS.size(); ++i) {
			network = NETWORKS.get(i);
			if (network.isInvalid()) {
				NETWORKS.remove(i);
				--i;
				continue;
			}
			if (network.getWorld().isBlockLoaded(network.getPos()))
				consumer.accept(network);
		}
	}
	
	/**
	 * 注册一个自动化的网络传输
	 * @param net 要注册的对象，必须继承自TileEntity
	 * @throws ClassCastException 如果net不继承自TileEntity
	 */
	public static synchronized void register(IAutoNetwork net) {
		if (!(net instanceof TileEntity)) throw new ClassCastException("net不继承自TileEntity");
		for (IAutoNetwork network : NETWORKS)
			if (network == net) return;
		NETWORKS.add(net);
	}
	
}
