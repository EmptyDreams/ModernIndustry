package minedreams.mi.api.net;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.tileentity.TileEntity;

/**
 * 自动化网络传输注册机.<br>
 * 该类使用弱引用存储注册的对象，当对象所有强引用都被删除后注册的对象会被自动销毁，
 * 被销毁的对象会自动取消注册。若方块被卸载，系统会跳过该方块的遍历。
 * @author EmptyDreams
 * @version V1.0
 */
public final class NetworkRegister {
	
	static final List<WeakReference<IAutoNetwork<?>>> NETWORKS = new LinkedList<>();
	
	static void forEach(Consumer<? super IAutoNetwork<?>> consumer) {
		final List<WeakReference<?>> removes = new ArrayList<>();
		NETWORKS.forEach(it -> {
			IAutoNetwork<?> network = it.get();
			if (network == null) {
				removes.add(it);
			} else {
				if (network.getWorld().isBlockLoaded(network.getPos()))
					consumer.accept(network);
			}
		});
		//noinspection SuspiciousMethodCalls
		NETWORKS.removeAll(removes);
	}
	
	/**
	 * 注册一个自动化的网络传输
	 * @param net 要注册的对象，必须继承自TileEntity
	 * @throws ClassCastException 如果net不继承自TileEntity
	 */
	public static void register(IAutoNetwork<?> net) {
		if (!(net instanceof TileEntity)) throw new ClassCastException("net不继承自TileEntity");
		for (WeakReference<IAutoNetwork<?>> network : NETWORKS) {
			if (net.equals(network.get())) return;
		}
		NETWORKS.add(new WeakReference<>(net));
	}
	
}
