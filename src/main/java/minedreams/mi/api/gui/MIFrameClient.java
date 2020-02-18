package minedreams.mi.api.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import minedreams.mi.api.gui.component.MIComponent;
import minedreams.mi.api.gui.info.TitleModelEnum;
import minedreams.mi.api.net.WaitList;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class MIFrameClient extends GuiContainer {
	
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
	private final List<MIComponent> components = new LinkedList<>();
	
	public MIFrameClient(Container inventorySlotsIn) {
		super(inventorySlotsIn);
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
	 * 移除一个组件
	 * @param component 要移除的组件
	 */
	public void remove(MIComponent component) {
		components.remove(component);
	}
	
	/**
	 * 添加一个组件
	 *
	 * @param component 要添加的组件
	 * @throws NullPointerException 如果component == null
	 */
	public void add(MIComponent component) {
		WaitList.checkNull(component, "component");
		
		components.add(component);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		/* 绘制标题 */
		String temp = I18n.format(title);
		int length = fontRenderer.getStringWidth(temp);
		Point location = null;
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
			}
		} else {
			location = titleLocation;
		}
		fontRenderer.drawString(temp, location.x, location.y, titleColor);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		if (isPaintBackGround) drawDefaultBackground();
		
	}
	
	private void drawComponent() {
		MIComponent.RenderTexture render;
		for (MIComponent component : components) {
			render = component.getRender();
			mc.getTextureManager().bindTexture(render.getTexture());
			drawTexturedModalRect(component.getX(), component.getY(),
					render.getX(), render.getY(), component.getWidth(), component.getHeight());
		}
	}
	
}
