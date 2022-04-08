package top.kmar.mi.api.electricity.interfaces;

import net.minecraft.tileentity.TileEntity;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.info.PathInfo;

/**
 * 电力传输工具的托管
 * @author EmptyDreams
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
	 * <p>让指定方块运输电能. 该方法中需要处理以下内容：
	 * <ol>
	 *  <li> 更新内部数据
	 *  <li> 当电缆过载时进行有关操作
	 * </ol>
	 * @param now 指定方块
	 * @param energy 能量
	 */
	void transfer(TileEntity now, EleEnergy energy);
	
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
	 * 判断是否已经连接指定方块. 方法传入的target可能在托管名单之外，
	 * 若自己的线缆方块支持连接其它线缆方块可增强模组的兼容性。
	 * @param now 当前方块
	 * @param target 指定方块
	 */
	boolean isLink(TileEntity now, TileEntity target);
	
	/**
	 * 判断能否连接指定方块. 方法传入的target可能在托管名单之外，
	 * 若自己的线缆方块支持连接其它线缆方块可增强模组的兼容性。
	 * @param now 当前方块
	 * @param target 指定方块的TE
	 */
	boolean canLink(TileEntity now, TileEntity target);
	
	/**
	 * 获取指定电缆的电能损耗值
	 * @param now 指定线缆
	 * @param energy 运输的总能量（不包括损耗能量）
	 * @return 损耗的能量
	 */
	int getEnergyLoss(TileEntity now, EleEnergy energy);
	
}