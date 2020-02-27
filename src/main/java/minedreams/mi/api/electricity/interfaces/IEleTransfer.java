package minedreams.mi.api.electricity.interfaces;

import javax.annotation.Nullable;

import minedreams.mi.api.electricity.cache.EleLineCache;
import minedreams.mi.api.electricity.info.PathInfo;
import net.minecraft.tileentity.TileEntity;

/**
 * 电力传输工具的托管
 * @author EmptyDreams
 * @version V1.0
 */
public interface IEleTransfer extends IRegister {
	
	/**
	 * <p>从指定位置开始寻找最近的最合适的发电机.
	 * 计算结果通常需要缓存，缓存任务交给用户完成。</p>
	 * <b>
	 *     <p>当前线路中可能存在当前托管不支持的方块，遇到这种情况时用户应该以不支持的方块为起点，
	 *     将计算转交给其它托管进行，最后当前托管负责将自己和其它托管的计算结果整合到一起。</p>
	 *     <p>若当前托管不支持电缆方块连接托管名单外的电缆方块则无需进行此设计。</p>
	 * </b>
	 * @param start 起点
	 * @param user 需求电能的机器
	 * @param inputer 机器的托管
	 * @return 路径信息
	 * @throws ClassCastException 如果不支持输入的TE
	 */
	PathInfo findPath(TileEntity start, TileEntity user, IEleInputer inputer);
	
	/**
	 * 让指定方块运输电能. 该方法中需要处理以下内容：<br>
	 * <b>
	 *  1.更新内部数据<br>
	 *  2.当电缆过载时进行有关操作
	 * </b>
	 * @param now 指定方块
	 * @param energy 电能
	 * @param voltage 电压
	 * @param info 上一根与当前电线相连的电线返回的信息
	 * @return 要传递给下一根电线的信息，该信息只会传递给托管支持的线缆
	 */
	Object transfer(TileEntity now, int energy, IVoltage voltage, Object info);
	
	/**
	 * 连接指定方块.
	 * 目标方块可能是：电能输出/输入/传输设备中的任意一个，
	 * 同时目标方块可能来自其它模组。
	 * @param now 当前方块
	 * @param target 目标方块
	 * @return 是否连接成功
	 */
	boolean link(TileEntity now, TileEntity target);
	
	/**
	 * 判断是否已经连接指定方块. 方法传入的tgte可能在托管名单之外，
	 * 若自己的线缆方块支持连接其它线缆方块可增强模组的兼容性。
	 * @param now 当前方块
	 * @param target 指定方块
	 */
	boolean isLink(TileEntity now, TileEntity target);
	
	/**
	 * 判断能否连接指定方块. 方法传入的tgte可能在托管名单之外，
	 * 若自己的线缆方块支持连接其它线缆方块可增强模组的兼容性。
	 * @param now 当前方块
	 * @param tgte 指定方块的TE
	 */
	boolean canLink(TileEntity now, TileEntity tgte);
	
	/**
	 * 获取当前线缆所在的线路的缓存信息.
	 * 系统不会自动触发该方法，一般由{@link #findPath(TileEntity, TileEntity, IEleInputer)}调用
	 * @param now 当前方块
	 * @return 若没有缓存信息则返回null
	 */
	@Nullable
	EleLineCache getLineCache(TileEntity now);
	
}
