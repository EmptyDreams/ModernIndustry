package xyz.emptydreams.mi.api.gui.craft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSol;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.gui.client.LocalChildFrame;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.ButtonComponent;
import xyz.emptydreams.mi.api.gui.component.CommonProgress;
import xyz.emptydreams.mi.api.gui.component.group.Group;
import xyz.emptydreams.mi.api.gui.component.group.Panels;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.gui.craft.handle.CraftHandle;
import xyz.emptydreams.mi.api.net.handler.MessageSender;
import xyz.emptydreams.mi.api.net.message.player.PlayerAddition;
import xyz.emptydreams.mi.api.net.message.player.PlayerMessage;
import xyz.emptydreams.mi.api.utils.ItemUtil;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.data.enums.OperateResult;

import java.util.List;

import static xyz.emptydreams.mi.api.gui.component.ButtonComponent.Style.TRIANGLE_LEFT;
import static xyz.emptydreams.mi.api.gui.component.ButtonComponent.Style.TRIANGLE_RIGHT;
import static xyz.emptydreams.mi.api.gui.craft.CraftFrameUtil.removeItemStack;
import static xyz.emptydreams.mi.api.utils.data.enums.OperateResult.FAIL;
import static xyz.emptydreams.mi.api.utils.data.enums.OperateResult.SUCCESS;

/**
 * 用于显示合成表
 * @author EmptyDreams
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CraftFrame extends MIFrame {

	/** 目标合成表 */
	private final CraftGuide craft;
	/** 当前合成表下标 */
	private int index = -1;
	/** 对应的{@link CraftHandle.Node} */
	private CraftHandle.Node node;
	/** 玩家对象 */
	private final EntityPlayer player;
	/** 盛放原料的SlotGroup */
	private final SlotGroup slots;
	
	/**
	 * 创建一个显示合成表的GUI
	 * @param craft 要显示的合成表
	 * @param player 打开窗体的玩家
	 * @param slots 玩家当前打开的GUI中用于盛放原料的SlotGroup，为空表示不支持填充
	 */
	public CraftFrame(CraftGuide craft, EntityPlayer player, SlotGroup slots) {
		super(craft.getName() + ".gui");
		this.craft = StringUtil.checkNull(craft, "craft");
		this.player = StringUtil.checkNull(player, "player");
		this.slots = slots;
		int width = (craft.getShapeWidth() + craft.getProtectedWidth()) * 18
						+ CommonProgress.Style.ARROW.getWidth() + 15 * 4;
		int height = Math.max(craft.getProtectedHeight(), craft.getShapeHeight()) * 18 + 50;
		setSize(width, height);
		init();
	}

	/** 解析合成表 */
	private void init() {
		if (index != -1) return;
		removeAllComponent();
		index = -1;
		CraftHandle handle = HandleRegister.get(craft);
		if (handle == null) {
			MISysInfo.err("[CraftFrame]合成表显示暂时不支持该类型的合成表：" + craft.getClass().getName());
			return;
		}
		node = handle.createGroup();
		CommonProgress progress = new CommonProgress();
		
		Group group = new Group(0, 20, getWidth(), 0, Panels::horizontalCenter);
		group.adds(node.raw, progress, node.pro);
		add(group, null);
		
		//下方按钮
		Group buttonGroup = new Group(0, group.getHeight() + group.getY() + 7,
								getWidth(), 0, Panels::horizontalCenter);
		ButtonComponent prevButton = new ButtonComponent(10, 10, TRIANGLE_LEFT);
		ButtonComponent nextButton = new ButtonComponent(10, 10, TRIANGLE_RIGHT);
		ButtonComponent fillButton = new ButtonComponent(10, 10, ButtonComponent.Style.REC);
		prevButton.setAction((frame, isClient) -> preShape());
		nextButton.setAction((frame, isClient) -> nextShape());
		fillButton.setAction((frame, isClient) -> fill());
		buttonGroup.adds(prevButton, fillButton, nextButton);
		add(buttonGroup, null);
		
		handle.update(node, craft.getShape(++index));
	}
	
	/**
	 * 尝试把当前显示的合成表填充到SlotGroup中
	 * @return 运算结果
	 */
	@SuppressWarnings("UnusedReturnValue")
	public OperateResult fill() {
		if (slots == null) return FAIL;
		List<ItemStack> inventory = player.inventory.mainInventory;
		if (!slots.isEmpty()) {
			//若输入框内已有物品则尝试合并到玩家背包
			for (SlotGroup.Node node : slots) {
				ItemStack stack = node.get().getStack();
				if (stack.isEmpty()) continue;
				OperateResult result = ItemUtil.mergeItemStack(stack,
						inventory, 0, 35, true);
				//若玩家背包不能放下输入框内的物品则停止填充
				if (result != SUCCESS) return FAIL;
			}
		}
		//将合成表转换为ItemList类型
		ItemSol input = getShape().getInput();
		ItemList list = new ItemList(craft.getShapeWidth(), craft.getShapeHeight());
		if (!input.fill(list)) return FAIL;
		//尝试用玩家背包中的物品填充合成表
		OperateResult result = removeItemStack(inventory, list, null);
		if (result == FAIL) return FAIL;
		sendToServer();
		LocalChildFrame.closeGUI();
		return result;
	}
	
	private void sendToServer() {
		PlayerAddition addition = new PlayerAddition(player,
				new ResourceLocation(ModernIndustry.MODID, "CraftFrame.record"));
		ByteDataOperator operator = new ByteDataOperator(50);
		operator.writeString(craft.getName());
		operator.writeVarint(index);
		IMessage message = PlayerMessage.instance().create(operator, addition);
		MessageSender.sendToServer(message);
	}
	
	/** 强制重新初始化缓存 */
	public void reInit() {
		index = -1;
		init();
	}
	
	/** 切换到下一个合成表并刷新显示 */
	public void nextShape() {
		int now = ++index;
		if (now >= craft.size()) now = index = 0;
		CraftHandle handle = HandleRegister.get(craft);
		//noinspection ConstantConditions
		handle.update(node, craft.getShape(now));
	}
	
	/** 切换到上一个合成表并刷新显示 */
	public void preShape() {
		int now = --index;
		if (now < 0) now = index = craft.size() - 1;
		CraftHandle handle = HandleRegister.get(craft);
		//noinspection ConstantConditions
		handle.update(node, craft.getShape(now));
	}
	
	/** 获取当前显示的合成表 */
	public IShape getShape() {
		return craft.getShape(index);
	}
	
	/** 重新绘制当前合成表 */
	public void repaint() {
		CraftHandle handle = HandleRegister.get(craft);
		//noinspection ConstantConditions
		handle.update(node, getShape());
	}
	
}