package xyz.emptydreams.mi.api.capabilities.fluid;

import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.fluid.data.FluidData;
import xyz.emptydreams.mi.api.fluid.data.TransportReport;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 流体容器
 * @author EmptyDreams
 */
public interface IFluid {
	
	/**
	 * <p>获取可存储的最大量
	 * <p>该方法可能会在对象构造函数中调用，必须保证构造过程中也可以返回正确的值
	 */
	default int getMaxAmount() {
		return 1000;
	}
	
	/** 判断容器是否为空 */
	boolean isEmpty();
	
	/**
	 * <p>向容器中插入流体
	 * <p>注意：
	 * <ol>
	 *  <li><b>该方法仅负责向当前容器插入数据，至于是否会进行额外的运算并没有规定</b>
	 *  <li>该方法保证不会修改输入的data
	 * </ol>
	 * @param data 要插入的数据
	 * @param facing 相对于当前流体容器，流体向哪个方向流动
	 * @param simulate 是否为模拟，为true不修改内部数据，但是会修改report
	 * @param report 运算结果
	 * @return 成功插入了多少流体
	 */
	int insert(FluidData data, EnumFacing facing, boolean simulate, TransportReport report);
	
	/**
	 * <p>从容器中取出流体
	 * <p>补充：输入的data参数中，流体类型用来表示要取出的流体类型，如果能取出的和要取出的流体类型不符则不能取出
	 * @param data 要取出的数据，若data的流体类型为null则表明不关心取出什么类型的流体
	 * @param facing 相对于当前流体容器，流体向哪个方向流动
	 * @param simulate 是否为模拟，为true不修改内部数据
	 * @param report 运算结果
	 * @return 取出了多少数据
	 */
	int extract(FluidData data, EnumFacing facing, boolean simulate, TransportReport report);
	
	/**
	 * 获取下一个可用的流体去向
	 * @param facing 来源方向
	 * @throws IllegalArgumentException 如果来源没有与管道连接
	 * @return 返回结果无序且允许更改（更改返回结果不影响内部数据）
	 */
	@Nonnull
	List<EnumFacing> next(EnumFacing facing);
	
	/** 判断是否可以连接指定方向 */
	boolean canLink(EnumFacing facing);
	
	/**
	 * 连接指定方向上的设备
	 * @param facing 方向
	 * @return 连接是否成功，若该方向原本已经有连接则返回true
	 */
	boolean link(EnumFacing facing);
	
	/** 断开与指定方向上的设备的连接 */
	void unlink(EnumFacing facing);
	
	/** 是否连接指定方向 */
	boolean isLinked(EnumFacing facing);
	
}