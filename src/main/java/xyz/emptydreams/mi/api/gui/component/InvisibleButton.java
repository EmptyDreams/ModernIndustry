package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.IFrame;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseActionListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseEnteredListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseExitedListener;
import xyz.emptydreams.mi.api.interfaces.ObjBooleanConsumer;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * 隐形按钮
 * @author EmptyDreams
 */
public class InvisibleButton extends MComponent {
	
	@SideOnly(Side.CLIENT)
	private static final ObjBooleanConsumer<IFrame> SRC_ACTION = (frame, isClient) -> {};
	
	/** 鼠标是否在控件中 */
	private boolean mouse = false;
	/** 点击时执行的操作 */
	private ObjBooleanConsumer<IFrame> onAction = SRC_ACTION;
	
	public InvisibleButton(int width, int height) {
		setSize(width, height);
	}
	
	/** 判断鼠标是否在控件中 */
	protected boolean isMouseIn() { return mouse; }
	
	/** 设置按钮被点击时的操作，默认为无任何操作 */
	public void setAction(ObjBooleanConsumer<IFrame> consumer) {
		onAction = StringUtil.checkNull(consumer, "consumer");
	}
	
	@Override
	public void paint(@Nonnull Graphics g) { }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onAddToGUI(StaticFrameClient con, EntityPlayer player) {
		super.onAddToGUI(con, player);
		onAddToGUI((MIFrame) null, player);
	}
	
	@Override
	public void onAddToGUI(MIFrame con, EntityPlayer player) {
		super.onAddToGUI(con, player);
		registryListener((MouseEnteredListener) (mouseX, mouseY) -> mouse = true);
		registryListener((MouseExitedListener) (mouseX, mouseY) -> mouse = false);
		registryListener(new MouseActionListener() {
			float mouseX, mouseY;
			@Override
			public void mouseAction(float mouseX, float mouseY) {
				this.mouseX = mouseX;
				this.mouseY = mouseY;
				onAction.accept(con, WorldUtil.isClient());
				if (WorldUtil.isClient()) {
					Minecraft.getMinecraft().getSoundHandler().playSound(
							PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				}
			}
			
			@Nullable
			@Override
			public NBTTagCompound writeTo() {
				if (WorldUtil.isClient()) {
					NBTTagCompound data = new NBTTagCompound();
					data.setFloat("x", mouseX);
					data.setFloat("y", mouseY);
					return data;
				} else {
					return null;
				}
			}
			
			@Override
			public void readFrom(NBTTagCompound data) {
				if (WorldUtil.isServer()) {
					mouseAction(data.getFloat("x"), data.getFloat("y"));
				}
			}
			
		});
	}
}
