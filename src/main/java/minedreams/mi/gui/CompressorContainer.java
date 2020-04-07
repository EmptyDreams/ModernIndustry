package minedreams.mi.gui;

import minedreams.mi.ModernIndustry;
import minedreams.mi.blocks.te.user.EUCompressor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 压缩机的GUI
 * @author EmptyDremas
 * @version V1.0
 */
public class CompressorContainer extends Container {

	/** UI材质路径 */
	private static final String TEXTURE_PATH = ModernIndustry.MODID + ":" + "textures/gui/gui.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);
    
	/**
	 * 能否被玩家打开
	 */
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	/** 方块的NBT标签对象 */
	protected EUCompressor nbt;
	
	/**
	 * @param player 打开UI的玩家
	 * @param world 方块所在世界
	 * @param pos 方块坐标
	 */
	public CompressorContainer(EntityPlayer player, World world, BlockPos pos) {
		nbt = (EUCompressor)world.getTileEntity(pos);
		
		/* 注册输(入/出)栏和物品栏 */
		addSlotToContainer(nbt.getSolt(0));
		addSlotToContainer(nbt.getSolt(1));
		addSlotToContainer(nbt.getSolt(2));
		//玩家背包
		for (int i = 0; i < 3; ++i) {
			for (int k = 0; k < 9; ++k) {
				addSlotToContainer(new Slot(player.inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
			}
		}
		for (int k = 0; k < 9; ++k) {
			addSlotToContainer(new Slot(player.inventory, k, 8 + k * 18, 142));
		}
	}
	
	/**
	 * shift+左键物品
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = inventorySlots.get(index);
        
		ItemStack newStack = slot.getStack(), oldStack = newStack.copy();
		boolean isMerged;
		
		if (index == 0 || index == 1 || index == 2) {
			isMerged = mergeItemStack(newStack, 3, 39, true);
		} else if (index > 2 && index < 30 && newStack.getMaxStackSize() <= 64) {
			isMerged = mergeItemStack(newStack, 0, 3, false) || mergeItemStack(newStack, 31, 39, false);
		} else if (index > 29 && index < 39 && newStack.getMaxStackSize() <= 64) {
			isMerged = mergeItemStack(newStack, 0, 30, false);
		} else {
			isMerged = false;
		}
		if (!isMerged) return ItemStack.EMPTY;
		else if (newStack.isEmpty()) slot.putStack(ItemStack.EMPTY);
		else slot.onSlotChanged();
		
		return oldStack;
	}
	
	private int[] Information = { 0, 0 };
	
	/** 
	 * 向客户端发送信息：<br>
	 * 0-已经工作时间，1-需要工作时间
	 */
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener i : listeners) {
			i.sendWindowProperty(this, 0, nbt.getWorkingTime());
			i.sendWindowProperty(this, 1, nbt.getNeedTime());
		}
	}
	
	/** 
	 * 接服务端发送的信息
	 * @see #detectAndSendChanges
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		Information[id] = data;
	}
	
	/**
	 * 客户端
	 * @author EmptyDremas
	 */
	public final static class ContainerGui extends GuiContainer {
		
		/** 服务端对象 */
		CompressorContainer c;
		
		/**
		 * @param inventorySlotsIn 服务端对象
		 */
		public ContainerGui(CompressorContainer inventorySlotsIn) {
			super(inventorySlotsIn);
			//初始化UI大小
			xSize = 176;
			ySize = 166;
			c = inventorySlotsIn;
		}
		
		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		    this.drawDefaultBackground();
		    super.drawScreen(mouseX, mouseY, partialTicks);
		    this.renderHoveredToolTip(mouseX, mouseY);
		}
		
		/**
		 * 绘制文字
		 */
		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			//从lang文件中获取文字
			String i = I18n.format("tile.compressor_tblock.name");
			fontRenderer.drawString(i, (this.xSize - fontRenderer.getStringWidth(i)) / 2, 6, 0x000000);
		}
		
		/**
		 * 绘制图形
		 */
		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			drawDefaultBackground();
		    mc.getTextureManager().bindTexture(TEXTURE);
		    int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		    drawTexturedModalRect(offsetX, offsetY, 0, 0, xSize, ySize);
		    
		    int textWidth = (int) Math.ceil(22.0 * c.Information[0] / c.Information[1]);
		    drawTexturedModalRect(offsetX + 80, offsetY + 35, 0, 166, textWidth, 16);
		}
		
	}
	
}
