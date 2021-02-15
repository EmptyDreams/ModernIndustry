package xyz.emptydreams.mi.api.gui.craft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSol;
import xyz.emptydreams.mi.api.gui.common.ChildFrame;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.net.message.player.IPlayerHandle;
import xyz.emptydreams.mi.api.register.AutoPlayerHandle;
import xyz.emptydreams.mi.api.utils.ItemUtil;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.api.utils.data.enums.OperateResult;

import static xyz.emptydreams.mi.api.utils.data.enums.OperateResult.SUCCESS;

/**
 * @author EmptyDreams
 */
@AutoPlayerHandle("CraftFrame.record")
public class RecordMessage implements IPlayerHandle {
	
	@Override
	public void apply(EntityPlayer player, NBTTagCompound data) {
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
			TileEntity te = ChildFrame.getGuiTileEntity(player);
			String craftName = data.getString("craft");
			int index = data.getInteger("index");
			CraftGuide<?, ?> craft = CraftGuide.getInstance(new ResourceLocation(craftName));
			//noinspection ConstantConditions
			IShape<?, ?> shape = craft.getShape(index);
			ItemSol input = shape.getInput();
			int length = MathUtil.amount2Rec(input.size());
			ItemList list = new ItemList(length, length);
			boolean fill = input.fill(list);
			SlotGroup slots = CraftShower.getSlotGroup(craft, te);
			if (!slots.isEmpty()) {
				//若输入框内已有物品则尝试合并到玩家背包
				for (SlotGroup.Node node : slots) {
					ItemStack stack = node.get().getStack();
					if (stack.isEmpty()) continue;
					OperateResult result = ItemUtil.mergeItemStack(stack,
							player.inventory.mainInventory, 0, 35, true);
					//若玩家背包不能放下输入框内的物品则停止填充
					if (result != SUCCESS) fill = false;
				}
			}
			if (!fill || te == null) {
				MISysInfo.err("---------- RecordMessage Error Log ----------\n"
						+ "\t原因：客户端计算结果异常！可能是由于代码编写错误或玩家修改了客户端代码！\n"
						+ "\t玩家：" + player.getName() + '\n'
						+ "\t合成表：" + craftName + "\t 下标：" + index + '\n'
						+ "\t矩阵：" + length + '\n'
						+ "\tTE：" + te + '\n'
						+ "\t坐标：" + (te == null ? "未知" : te.getPos()) + '\n'
						+ "\t世界：" + (te == null ? "未知" : te.getWorld().getProviderName()) + '\n'
						+ "处理方法：跳过本次合成表运算，服务端不修改玩家数据，客户端由MC内置的网络通信恢复数据。");
				return;
			}
			CraftFrameUtil.removeItemStack(player.inventory.mainInventory, list);
			for (SlotGroup.Node node : slots) {
				node.get().putStack(list.get(node.getX(), node.getY()).getStack());
			}
			te.markDirty();
		});
	}
	
}