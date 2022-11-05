package top.kmar.mi.api.capabilities.fluid;

import net.minecraft.util.EnumFacing;
import top.kmar.mi.api.fluid.data.FluidQueue;
import top.kmar.mi.api.fluid.data.TransportReport;

import javax.annotation.Nonnull;

/**
 * 流体容器
 * @author EmptyDreams
 */
public interface IFluid {
	
	/** 判断容器是否为空 */
	boolean isEmpty();
	
	/** 判断容器是否已满 */
	boolean isFull();
	
	/**
	 * <p>向容器中插入流体
	 * <p>注意：
	 * <ol>
	 *  <li>该方法仅负责向当前容器插入数据，至于是否会进行额外的运算并没有规定
	 *  <li>该方法不保证流体的插入顺序，如果必须保证插入顺序那么请将队列按需分割成多个队列然后分多次调用
	 *  <li>该方法不保证queue中一定只包含原本的内容，即允许方法的实现向queue中插入原本没有的流体
	 *  <li>该方法的运算结果不具有随机性，即连续两次调用返回的结果是相同的
	 * </ol>
	 * @param queue 要插入的数据，运算结束后queue中存储的数据便是没有插入进去的数据
	 * @param facing 相对于当前流体容器，流体向哪个方向流动
	 * @param simulate 是否为模拟，为true不修改内部数据，但是会修改report
	 * @param report 运算结果
	 * @return 成功插入了多少流体
	 */
	int insert(FluidQueue queue, EnumFacing facing, boolean simulate, TransportReport report);
	
	/**
	 * <p>从容器中取出流体
	 * <ul>
	 *     <li>输入的data参数中，流体类型用来表示要取出的流体类型，如果能取出的和要取出的流体类型不符则不能取出
	 *     <li>该方法的运算结果不应具有随机性，即连续两次调用返回的结果应当是相同的
	 * </ul>
	 * @param amount 要取出的流体量
	 * @param facing 相对于当前流体容器，流体向哪个方向流动
	 * @param simulate 是否为模拟，为true不修改内部数据
	 * @param report 运算结果
	 * @return 取出的数据，方法不保证队列中流体的顺序
	 */
	@Nonnull
	FluidQueue extract(int amount, EnumFacing facing, boolean simulate, TransportReport report);
	
	/** 判断是否可以连接指定方向 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	boolean canLinkFluid(EnumFacing facing);
	
	/**
	 * 连接指定方向上的设备
	 * @param facing 方向
	 * @return 连接是否成功，若该方向原本已经有连接则返回true
	 */
	boolean linkFluid(EnumFacing facing);
	
	/** 断开与指定方向上的设备的连接 */
	void unlink(EnumFacing facing);
	
	/** 是否连接指定方向 */
	boolean isLink(EnumFacing facing);
	
}