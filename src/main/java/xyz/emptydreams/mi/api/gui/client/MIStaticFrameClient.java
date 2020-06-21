package xyz.emptydreams.mi.api.gui.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.MIFrame;
import xyz.emptydreams.mi.api.gui.component.IComponent;
import xyz.emptydreams.mi.api.gui.component.ImageData;
import xyz.emptydreams.mi.api.gui.component.MBackpack;
import xyz.emptydreams.mi.api.gui.info.TitleModelEnum;
import xyz.emptydreams.mi.api.net.WaitList;
import xyz.emptydreams.mi.api.utils.WorldUtil;

/**
 * 静态GUI，注意：该类只能用于静态GUI的显示
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class MIStaticFrameClient extends GuiContainer {
	
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
	/** 保存字符串组件 */
	private final List<IComponent> stringComponents = new LinkedList<>();
	/** 材质 */
	private final ResourceLocation RL;
	
	public MIStaticFrameClient(MIFrame inventorySlotsIn) {
		super(inventorySlotsIn);
		xSize = inventorySlotsIn.getWidth();
		ySize = inventorySlotsIn.getHeight();
		RL = new ResourceLocation(inventorySlotsIn.getModId(), inventorySlotsIn.getName());
	}
	
	private static int getSize(int size) {
		int k;
		for (int i = 1; i <= 32; ++i) {
			k = 1 << i;
			if (k >= size) return k;
		}
		return -1;
	}
	
	private boolean isInit = true;
	private void init() {
		if (isInit) {
			isInit = false;
			if (maps.containsKey(RL)) return;
			
			int size = getSize(Math.max(xSize, ySize));
			BufferedImage image = new BufferedImage(size, size, 6);
			Graphics g = image.getGraphics();
			drawBackground(g, xSize, ySize);
			
			for (IComponent component : components) {
				component.paint(g.create(
						component.getX(), component.getY(), component.getWidth(), component.getHeight()));
			}
			
			try {
				MITexture.writeFile(RL, image);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private final Map<ResourceLocation, MITexture> maps = new HashMap<>();
	/** 装载材质，切勿使用原版自带的方法装载 */
	public boolean bindTexture() {
		if (isInit) return false;
		MITexture texture = maps.getOrDefault(RL, null);
		if (texture == null) {
			try {
				texture = new MITexture(RL);
				texture.loadTexture(null);
				maps.put(RL, texture);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		GlStateManager.bindTexture(texture.getGlTextureId());
		return true;
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
	 * 添加一个组件
	 *
	 * @param component 要添加的组件
	 * @throws NullPointerException 如果component == null
	 */
	public void add(IComponent component) {
		WaitList.checkNull(component, "component");
		components.add(component);
		if (component.isString()) stringComponents.add(component);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String temp = I18n.format(title);
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
		
		for (IComponent component : stringComponents) {
			fontRenderer.drawString(
					component.getString(), component.getX(), component.getY(), component.getStringColor());
		}
	}
	
	@Override
	protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		if (isPaintBackGround) drawDefaultBackground();
		init();
		if (bindTexture()) {
			int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
			drawTexturedModalRect(offsetX, offsetY, 0, 0, xSize, ySize);
			for (IComponent component : components) {
				component.realTimePaint(this);
			}
		}
	}
	
	public static void drawBackground(Graphics g, int width, int height) {
		if (WorldUtil.isClient(null)) {
			g.drawImage(ImageData.getImage("background").getScaledInstance(
					width, height, Image.SCALE_DEFAULT), 0, 0, null);
		}
	}
	
}
