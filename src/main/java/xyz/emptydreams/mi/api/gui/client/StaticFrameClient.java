package xyz.emptydreams.mi.api.gui.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.gui.common.IFrame;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.common.TitleModelEnum;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static xyz.emptydreams.mi.api.gui.listener.mouse.MouseListenerTrigger.*;
import static xyz.emptydreams.mi.api.utils.StringUtil.checkNull;

/**
 * 静态GUI，注意：该类只能用于静态GUI的显示
 * @author EmptyDreams
 */
@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class StaticFrameClient extends GuiContainer implements IFrame {
	
	/** GUI背景的资源名称 */
	public static final String SOURCE_NAME = "background";
	/** 玩家背包的本地名称 */
	private static final String INVENTORY = I18n.format("container.inventory");
	
	/** 是否绘制默认背景颜色 */
	private boolean isPaintBackGround = true;
	/** 标题 */
	private String title;
	/** 标题位置 */
	private Point titleLocation = null;
	/** 标题模式 */
	private TitleModelEnum titleModel = TitleModelEnum.CENTRAL;
	/** 标题颜色 */
	private int titleColor = 0x000000;
	/** 保存组件 */
	private final List<IComponent> components;
	/** 资源名称 */
	private String name;
	
	public StaticFrameClient(MIFrame inventorySlotsIn, String title) {
		super(inventorySlotsIn);
		xSize = inventorySlotsIn.getWidth();
		ySize = inventorySlotsIn.getHeight();
		this.title = title;
		components = inventorySlotsIn.cloneComponent();
		setResourceName(ModernIndustry.MODID, checkNull(title, "title"));
	}
	
	/** 设置GUI使用的资源名称，默认使用"<b>{@link ModernIndustry#MODID}:{@link #getTitle()}</b>" */
	public void setResourceName(String modid, String name) {
		this.name = modid + ":" + name;
	}
	
	/**
	 * 初始化材质
	 * @param isReset 当材质已存在时是否重置材质
	 */
	@SuppressWarnings("SameParameterValue")
	private void init(boolean isReset) {
		if (title == null) throw new NoSuchElementException("GUI的名称不存在！");
		if (name == null) throw new NoSuchElementException("资源名称不存在！");
		RuntimeTexture texture = RuntimeTexture.getInstance(name);
		if (texture == null || isReset) {
			BufferedImage image = new BufferedImage(xSize, ySize, 6);
			Graphics g = image.getGraphics();
			drawBackground(g, xSize, ySize);
			for (IComponent component : components) {
				Graphics graphics = g.create(
						component.getX(), component.getY(), component.getWidth(), component.getHeight());
				component.paint(graphics);
				graphics.dispose();
			}
			RuntimeTexture.setInstance(name, image);
		}
	}
	
	/** 获取并装载材质，切勿使用原版自带的方法装载 */
	public RuntimeTexture getTexture() {
		//init(true);
		RuntimeTexture texture = RuntimeTexture.getInstance(name);
		if (texture == null) {
			init(false);
			texture = RuntimeTexture.getInstance(name);
		}
		texture.bindTexture();
		return texture;
	}
	
	/** 设置是否绘制默认背景 */
	public void isPaintBackGround(boolean isPaintBackGround) { this.isPaintBackGround = isPaintBackGround; }
	/** 获取是否绘制默认背景 */
	public boolean isPaintBackGround() { return isPaintBackGround; }
	
	/**
	 * 设置标题
	 * @param text 该文本内部通过{@link I18n}转化
	 */
	public void setTitle(String text) {
		title = checkNull(text, "text");
	}

	@Override
	public int getWidth() { return xSize; }
	@Override
	public int getHeight() { return ySize; }
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
	
	/** 设置标题显示模式 */
	public void setTitleModel(TitleModelEnum model) {
		this.titleModel = checkNull(model, "model");
	}
	/** 获取标题显示模式 */
	@Nonnull
	public TitleModelEnum getTitleModel() { return titleModel; }
	
	/** 设置标题颜色，默认为：0 */
	public void setTitleColor(int color) { titleColor = color; }
	/** 获取标题颜色 */
	public int getTitleColor() { return titleColor; }
	
	/**
	 * 添加一个组件
	 *
	 * @param component 要添加的组件
	 * @throws NullPointerException 如果component == null
	 */
	@Override
	public void add(IComponent component, EntityPlayer player) {
		components.add(checkNull(component, "component"));
		component.onAddToGUI(this, player);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String temp = calculateTitle();
		int length = fontRenderer.getStringWidth(temp);
		Point location;
		if (titleLocation == null) {
			switch (titleModel) {
				case LEFT:
					location = new Point(0, 6);
					break;
				case CENTRAL:
					location = new Point((xSize - length) / 2, 6);
					break;
				case RIGHT:
					location = new Point(xSize - length, 6);
					break;
				default: throw new AssertionError();
			}
		} else {
			location = titleLocation;
		}
		fontRenderer.drawString(temp, location.x, location.y, 0);
	}
	
	/** 计算要显示的名称 */
	protected String calculateTitle() {
		return I18n.format(getTitle());
	}
	
	private IComponent preComponent = null;
	
	@Override
	protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mouseX -= getGuiLeft();     mouseY -= getGuiTop();
		if (isPaintBackGround) drawDefaultBackground();
		else GlStateManager.color(1.0F, 1.0F, 1.0F);
		RuntimeTexture texture = getTexture();
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		texture.drawToFrame(offsetX, offsetY, 0, 0, xSize, ySize);
		
		if (mouseX >= 0 && mouseY >= 0 && mouseX <= getXSize() && mouseY <= getYSize()) {
			IComponent onComponent = getComponentFromMouse(mouseX, mouseY);
			if (preComponent != onComponent) {
				if (preComponent != null) activateExited(preComponent, mouseX, mouseY);
				if (onComponent != null) activateEntered(onComponent, mouseX, mouseY);
			}
			preComponent = onComponent;
		} else if (preComponent != null) {
			activateExited(preComponent, mouseX, mouseY);
			preComponent = null;
		}
		
		for (IComponent component : components) {
			component.realTimePaint(this);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		mouseX -= getGuiLeft();     mouseY -= getGuiTop();
		IComponent component = getComponentFromMouse(mouseX, mouseY);
		if (component != null) {
			if (mouseButton == 0) activateAction(component, mouseX, mouseY);
			activateClick(component, mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		mouseX -= getGuiLeft();     mouseY -= getGuiTop();
		IComponent component = getComponentFromMouse(mouseX, mouseY);
		if (component != null) {
			activateReleased(component, mouseX, mouseY, state);
		}
	}
	
	/**
	 * 绘制GUI背景，若GUI包含玩家背包则同时绘制玩家背包
	 * @param g 画笔
	 * @param width 图像宽度
	 * @param height 图像高度
	 */
	public void drawBackground(Graphics g, int width, int height) {
		g.drawImage(ImageData.getImage(SOURCE_NAME, width, height), 0, 0, null);
		if (!(inventorySlots instanceof MIFrame)) return;
		MIFrame frame = (MIFrame) inventorySlots;
		if (frame.hasBackpack()) {
			g.drawImage(ImageData.getImage("backpack"),
					frame.getBackpackX(), frame.getBackpackY(), null);
		}
	}
	
	/**
	 * 根据鼠标坐标查找控件
	 * @return 若没有则返回null
	 */
	@Nullable
	public IComponent getComponentFromMouse(float mouseX, float mouseY) {
		for (IComponent component : components) {
			if (component.getX() <= mouseX && component.getY() <= mouseY
					&& component.getX() + component.getWidth() >= mouseX
					&& component.getY() + component.getHeight() >= mouseY) {
				IComponent c = component.getMouseTarget(mouseX, mouseY);
				if (c != null) return c;
			}
		}
		return null;
	}
	
	@Override
	public void init(World world) { }

}
