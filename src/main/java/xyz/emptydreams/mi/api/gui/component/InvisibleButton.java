package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.dor.IDataReader;
import xyz.emptydreams.mi.api.dor.IDataWriter;
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
	protected void init(MIFrame frame, EntityPlayer player) {
		super.init(frame, player);
		registryListener((MouseEnteredListener) (mouseX, mouseY) -> mouse = true);
		registryListener((MouseExitedListener) (mouseX, mouseY) -> mouse = false);
		registryListener(new MouseActionListener() {
			float mouseX, mouseY;
			
			@Override
			public void mouseAction(float mouseX, float mouseY) {
				this.mouseX = mouseX;
				this.mouseY = mouseY;
				onAction.accept(frame, WorldUtil.isClient());
				if (WorldUtil.isClient()) {
					playSound();
				}
			}
			
			@Nullable
			@Override
			public boolean writeTo(IDataWriter writer) {
				return true;
			}
			
			@Override
			public void readFrom(IDataReader data) {
				mouseAction(-1, -1);
			}
		});
	}
	
	@SideOnly(Side.CLIENT)
	private static void playSound() {
		Minecraft.getMinecraft().getSoundHandler().playSound(
				PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
	
}