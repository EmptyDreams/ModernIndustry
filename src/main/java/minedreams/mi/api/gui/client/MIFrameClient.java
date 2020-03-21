package minedreams.mi.api.gui.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import minedreams.mi.api.gui.info.TitleModelEnum;
import minedreams.mi.api.net.WaitList;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@SideOnly(Side.CLIENT)
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
	/** 背景 */
	private BufferedImage backgroundImage;
	/** 保存组件 */
	private final List<Component> components = new LinkedList<>();
	
	public MIFrameClient(Container inventorySlotsIn) { super(inventorySlotsIn); }
	
	private void init() {
		if (backgroundImage == null) {
			backgroundImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			drawBackground(backgroundImage.getGraphics(), width, height);
			
			components.clear();
			String temp = I18n.format(title);
			int length = fontRenderer.getStringWidth(temp);
			JLabel titleLable = new JLabel(temp);
			titleLable.setForeground(new Color(titleColor));
			titleLable.setSize(length, titleLable.getFont().getSize() + 1);
			if (titleLocation == null) {
				Point location;
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
				titleLable.setLocation(location);
			} else {
				titleLable.setLocation(titleLocation);
			}
			add(titleLable);
		}
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
	public void remove(Component component) {
		components.remove(component);
	}
	
	/**
	 * 添加一个组件
	 *
	 * @param component 要添加的组件
	 * @throws NullPointerException 如果component == null
	 */
	public void add(Component component) {
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
	protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		if (isPaintBackGround) drawDefaultBackground();
		init();
		drawComponent();
	}
	
	public void drawComponent() {
		MITexture text = MITexture.getInstance(width, height);
		text.getGraphics().drawImage(backgroundImage, 0, 0, null);
		
		
		//drawHorizontalLine();
		
		
		
		for (Component component : components) {
			component.paint(text.getGraphics(
					component.getX(), component.getY(), component.getWidth(), component.getHeight()));
		}
		
		text.loadTexture(null);
		GlStateManager.bindTexture(text.getGlTextureId());
		drawTexturedModalRect(0, 0, 0, 0, text.getWidth(), text.getHeight());
		text.invalidate();
	}
	
	private static final Color GRAY = new Color(85, 85, 85);
	private static final Color GRAY_CENTER = new Color(198, 198, 198);
	
	public static void drawBackground(Graphics g, int width, int height) {
		//绘制黑色边框
		g.setColor(Color.BLACK);
		g.drawLine(2, 0, width - 4, 0);
		g.drawLine(0,2, 0, height - 4);
		g.drawLine(3, height - 1, width - 3, height - 1);
		g.drawLine(width - 2, height - 2, width - 2, height - 2);
		g.drawLine(width - 1, height - 3, width - 1, 3);
		
		//绘制白色区域
		g.setColor(Color.WHITE);
		g.fillRect(1, 1, width - 4, 2);
		g.fillRect(1, 3, 2, height - 7);
		
		//绘制灰色区域
		g.setColor(GRAY);
		g.fillRect(width - 4, 3, 2, height - 4);
		g.fillRect(3, height - 4, width - 7, 2);
		
		//绘制中心区域
		g.setColor(GRAY_CENTER);
		g.fillRect(3, 3, width - 6, height - 6);
		g.drawLine(2, height - 4, 2, height - 4);
		g.drawLine(width - 4, 2, width - 4, 2);
		
		//绘制白点
		g.setColor(Color.WHITE);
		g.drawLine(3, 3, 3, 3);
		
		//绘制灰点
		g.setColor(GRAY);
		g.drawLine(width - 5, height - 5, width - 5, height - 5);
		
		//绘制黑点
		g.setColor(Color.BLACK);
		g.drawLine(1, 1, 1, 1);
		g.drawLine(width - 2, 2, width -2, 2);
		g.drawLine(width - 3, 1, width - 3, 1);
		g.drawLine(1, height - 3, 1, height - 3);
		g.drawLine(2, height - 2, 2, height - 2);
		
	}
	
}
