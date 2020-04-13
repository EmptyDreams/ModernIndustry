package xyz.emptydreams.mi.api.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.gui.component.IComponent;
import xyz.emptydreams.mi.api.gui.info.TitleModelEnum;
import xyz.emptydreams.mi.api.net.WaitList;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * MI版本窗体，通过该类可以便捷的创建和控制UI界面
 *
 * @author EmptyDreams
 * @version V1.0
 */
@Mod.EventBusSubscriber
public class MIFrame extends Container {
	
	/** 存储窗体的尺寸，可以更改 */
	private int width, height;
	/** 方块坐标 */
	private BlockPos blockPos;
	/** 所在世界 */
	private World world;
	/** 名称 */
	private final String NAME;
	private final String MODID;
	
	/**
	 * 通过该构造函数创建一个指定尺寸的UI
	 *
	 * @param width 宽度
	 * @param height 高度
	 */
	public MIFrame(String modid, String name, int width, int height) {
		WaitList.checkNull(name, "name");
		NAME = name;
		MODID = modid == null ? ModernIndustry.MODID : modid;
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
	/** 获取名称 */
	public String getName() { return NAME; }
	/** 获取MODID */
	public String getModId() { return MODID; }
	
	@SideOnly(Side.CLIENT)
	private Object gui;
	
	public void setGuiContainer(Object gui) {
		if (gui instanceof net.minecraft.client.gui.inventory.GuiContainer) {
			this.gui = gui;
		} else {
			throw new ClassCastException("gui is not instanceof GuiContainer");
		}
	}
	
	/**
	 * 判断玩家是否可以打开UI，默认返回true
	 */
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
	public Slot addSlotToContainer(Slot slotIn) {
		return super.addSlotToContainer(slotIn);
	}
	
	//--------------------关于组件的操作--------------------//
	
	/** 是否绘制默认背景颜色 */
	private boolean isPaintBackGround = true;
	/** 标题 */
	private String title = "by minedreams";
	/** 标题位置 */
	private Point titleLocation = null;
	/** 标题模式 */
	private TitleModelEnum titleModel = TitleModelEnum.CENTRAL;
	/** 标题颜色 */
	private int titleColor = 0x000000;
	/** 保存组件 */
	private final List<IComponent> components = new LinkedList<>();
	
	/** 设置是否绘制默认背景 */
	public void isPaintBackGround(boolean isPaintBackGround) { this.isPaintBackGround = isPaintBackGround; }
	/** 获取是否绘制默认背景 */
	public boolean isPaintBackGround() { return isPaintBackGround; }
	
	/**
	 * 设置标题
	 * @param text 该文本内部通过{@link I18n}转化
	 */
	public void setTitle(String text) {
		if (text == null) title = "by minedreams";
		else title = text;
	}
	/** 获取标题 */
	public String getTitle() { return title; }
	
	/**
	 * 设置标题位置，默认设置为左上角、居中、右上角
	 * @param x 当x小于0时恢复默认设置
	 * @param y 当y小于0时恢复默认设置
	 */
	public void setTitleLocation(int x, int y) {
		if (x < 0 || y < 0) titleLocation = null;
		else titleLocation = new Point(x, y);
	}
	/**
	 * 获取标题位置
	 * @return 若null为空则表示为默认值
	 */
	@Nullable
	public Point getTitleLocation() { return titleLocation; }
	
	/**
	 * 设置标题显示模式，当标题位置不为默认时该设置无效
	 * @param model 指定的模式
	 *
	 * @throws NullPointerException 如果model == null
	 */
	public void setTitleModel(TitleModelEnum model) {
		WaitList.checkNull(model, "model");
		this.titleModel = model;
	}
	/** 获取标题显示模式 */
	@Nonnull
	public TitleModelEnum getTitleModel() { return titleModel; }
	
	/** 设置标题颜色，默认为：0 */
	public void setTitleColor(int color) { titleColor = color; }
	/** 获取标题颜色 */
	public int getTitleColor() { return titleColor; }
	
	/**
	 * 移除一个组件
	 * @param component 要移除的组件
	 */
	public void remove(IComponent component) {
		components.remove(component);
		component.onRemoveFromGUI(this);
	}
	
	private int codeStart = 0;
	/**
	 * 添加一个组件
	 *
	 * @param component 要添加的组件
	 * @throws NullPointerException 如果component == null
	 */
	public void add(IComponent component, EntityPlayer player) {
		WaitList.checkNull(component, "component");
		components.add(component);
		component.onAddToGUI(this, player);
		component.setCodeStart(codeStart);
		codeStart += 100;
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			components.forEach(it -> it.send(this, listener));
		}
	}
	
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		for (IComponent component : components) {
			if (component.update(id, data)) break;
		}
	}
}
