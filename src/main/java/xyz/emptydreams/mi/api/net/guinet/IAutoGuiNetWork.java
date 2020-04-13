package xyz.emptydreams.mi.api.net.guinet;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public interface IAutoGuiNetWork {
	
	/**
	 * <p>获取需要发送的信息</p>
	 * <b><p>注意：</p><p>标签中不得含有以下元素："_code(int)"。</p>
	 * <p>若是服务器向玩家发送信息，需要设置以下信息：
	 * 1.int类型的"playerAmount"：标记玩家数量</p><p>
	 * 2.String类型的"player_"：其中"_"由数字代替，从0开始，到playerAmount - 1结束，存储玩家的名称</p></b>
	 * @return 若不需要传递信息返回null
	 */
	NBTTagCompound send();
	
	/** 处理接收的信息 */
	@SideOnly(Side.CLIENT)
	void receive(NBTTagCompound compound);
	
	/**
	 * 生成身份校验码.<br>
	 * 校验码用于保证客户端打开的GUI界面与预期的一致
	 */
	int getAuthCode();
	
	/**
	 * 检查校验码是否正确
	 * @return 返回FALSE表示校验码错误
	 */
	default boolean checkAutoCode(int code) {
		return getAuthCode() == code;
	}
	
	boolean isClient();
	
	/**
	 * 判断对象是否存在，返回false时会自动取消注册
	 */
	boolean isLive();
	
	@SideOnly(Side.CLIENT)
	@Nonnull
	net.minecraft.client.gui.inventory.GuiContainer getGuiContainer();
	
	/**
	 * 获取当前所在世界，客户端可返回null
	 */
	World getWorld();
	
}
