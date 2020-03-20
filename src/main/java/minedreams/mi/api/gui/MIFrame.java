package minedreams.mi.api.gui;

import minedreams.mi.api.net.WaitList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * MI版本窗体，通过该类可以便捷的创建和控制UI界面
 *
 * @author EmptyDreams
 * @version V1.0
 */
public class MIFrame extends Container {
	
	/** 存储窗体的尺寸，可以更改 */
	private int width, height;
	/** 方块坐标 */
	private BlockPos blockPos;
	/** 所在世界 */
	private World world;
	
	/**
	 * 通过该构造函数创建一个指定尺寸的UI
	 *
	 * @param width 宽度
	 * @param height 高度
	 */
	public MIFrame(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * 初始化内部数据
	 * @param world 当前世界
	 * @param pos 方块所在地点
	 *
	 * @throws NullPointerException 如果world == null || pos == null
	 * @throws IllegalArgumentException 如果第二次调用该函数
	 */
	public void init(World world, BlockPos pos) {
		WaitList.checkNull(world, "world");
		WaitList.checkNull(pos, "pos");
		if (world != null) throw new IllegalArgumentException("数据只允许初始化一次！");
		
		this.world = world;
		blockPos = pos;
	}
	
	/**
	 * 重新设置UI大小
	 * @param width 宽度
	 * @param height 高度
	 *
	 * @throws IllegalArgumentException 如果width <= 0 || height <= 0
	 */
	public void setSize(int width, int height) {
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException("width/height应该大于0，而这里为[" + width + "/" + height + "]");
		
		this.width = width;
		this.height = height;
	}
	
	/** 获取宽度 */
	public int getWidth() { return width; }
	/** 获取高度 */
	public int getHeight() { return height; }
	/** 获取所在世界 */
	public World getWorld() { return world; }
	/** 获取方块坐标 */
	public BlockPos getBlockPos() { return blockPos; }
	
	/**
	 * 判断玩家是否可以打开UI，默认返回true
	 */
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}
