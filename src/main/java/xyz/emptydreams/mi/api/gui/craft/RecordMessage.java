package xyz.emptydreams.mi.api.gui.craft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSol;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.gui.common.ChildFrame;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.net.message.player.IPlayerHandle;
import xyz.emptydreams.mi.api.register.others.AutoPlayerHandle;
import xyz.emptydreams.mi.api.utils.ItemUtil;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.api.utils.data.enums.OperateResult;
import xyz.emptydreams.mi.api.utils.data.math.Mar2D;

import java.util.ArrayList;

import static xyz.emptydreams.mi.api.utils.data.enums.OperateResult.SUCCESS;

/**
 * @author EmptyDreams
 */
@AutoPlayerHandle("CraftFrame.record")
public class RecordMessage implements IPlayerHandle {
	
	@Override
	public void apply(EntityPlayer player, IDataReader reader) {
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
			TileEntity te = ChildFrame.getGuiTileEntity(player);
			if (te == null) {
				printErrorForTENull(player);
				return;
			}
			String craftName = reader.readString();                 //管理器的名称
			int index = reader.readVarint();                        //合成表在管理器中的下标
			CraftGuide<?, ?> craft = CraftGuide.getInstance(new ResourceLocation(craftName));
			//noinspection ConstantConditions
			IShape<?, ?> shape = craft.getShape(index);             //合成表对象
			ItemSol input = shape.getInput();                       //合成表的输入框
			int length = MathUtil.amount2Rec(input.size());         //无序集合转化为矩阵
			ItemList list = new ItemList(length, length);
			boolean fill = input.fill(list);                        //将输入栏转化为二维矩阵
			SlotGroup slots = CraftShower.getSlotGroup(craft, te);  //获取当前TE中输入框中的物品
			if (!slots.isEmpty()) {                                 //若输入框内已有物品则尝试合并到玩家背包
				ArrayList<ItemStack> old = new ArrayList<>(player.inventory.mainInventory);
				for (SlotGroup.Node node : slots) {
					ItemStack stack = node.get().getStack();
					if (stack.isEmpty()) continue;
					OperateResult result = ItemUtil.mergeItemStack(stack,
							old, 0, 35, true);
					//若玩家背包不能放下输入框内的物品则停止填充
					if (result != SUCCESS) fill = false;
				}
				if (fill) {
					for (int i = 0; i < old.size(); i++) {
						player.inventory.mainInventory.set(i, old.get(i));
					}
				}
			}
			if (!fill) {
				//正常情况下无法填充的话客户端不会发送请求到服务端
				//如果出现了无法填充时服务端依然收到的请求说明客户端计算异常
				printError(player, craftName, index, length, te);
				return;
			}
			CraftFrameUtil.Record record = new CraftFrameUtil.Record(length, length);
			CraftFrameUtil.removeItemStack(player.inventory.mainInventory, list, record);
			for (Mar2D.Node node : record) {
				ItemStack put = list.get(node.getX(), node.getY()).getStack();
				put.setCount(node.getValue());
				slots.getSlot(node.getX(), node.getY()).putStack(put);
			}
			te.markDirty();
			NonNullList<ItemStack> copy = NonNullList.create();
			for (int i = 0; i < player.openContainer.inventorySlots.size(); ++i) {
				copy.add((player.openContainer.inventorySlots.get(i)).getStack());
			}
			((EntityPlayerMP) player).sendAllContents(player.openContainer, copy);
		});
	}
	
	/**
	 * 打印TE为空的错误信息
	 * @param player 进行操作的玩家
	 */
	private static void printErrorForTENull(EntityPlayer player) {
		MISysInfo.err("---------- RecordMessage Error Log ----------\n"
				+ "\t原因：服务端抓取到的TileEntity为空\n"
				+ "\t玩家：" + player.getName() + '\n'
				+ "\t处理方法：跳过本次合成表运算，服务端不修改玩家数据，客户端由MC内置的网络通信恢复数据");
	}
	
	/**
	 * 输出错误信息
	 * @param player 进行操作的玩家
	 * @param craftName 合成表管理器的名称
	 * @param index 合成表在管理器中的下标
	 * @param length 合成表真实尺寸
	 * @param te TE对象
	 */
	private static void printError(EntityPlayer player,
	                               String craftName, int index, int length, TileEntity te) {
		MISysInfo.err("---------- RecordMessage Error Log ----------\n"
				+ "\t原因：客户端计算结果异常，可能是由于代码编写错误或玩家修改了客户端代码\n"
				+ "\t玩家：" + player.getName() + '\n'
				+ "\t合成表：" + craftName + "\t 下标：" + index + '\n'
				+ "\t矩阵：" + length + '\n'
				+ "\tTE：" + te + '\n'
				+ "\t坐标：" + (te == null ? "未知" : te.getPos()) + '\n'
				+ "\t世界：" + (te == null ? "未知" : te.getWorld().getProviderName()) + '\n'
				+ "处理方法：跳过本次合成表运算，服务端不修改玩家数据，客户端由MC内置的网络通信恢复数据");
	}
	
}