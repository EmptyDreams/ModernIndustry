package xyz.emptydreams.mi.utils;

import java.util.Collection;

import xyz.emptydreams.mi.api.electricity.src.tileentity.EleSrcCable;
import xyz.emptydreams.mi.api.net.WaitList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static xyz.emptydreams.mi.api.net.WaitList.checkNull;

/**
 * 用于各个类的调试
 *
 * @author EmptyDreams
 * @version V1.0
 */
public final class DebugHelper {
	
	/**
	 * 打印客户端等待列表的信息
	 */
	@SideOnly(Side.CLIENT)
	public static void printWaitListClientMessage() {
		StringBuilder sb = new StringBuilder("WaitList{ size=").append(WaitList.getAmount()).append('\n');
		WaitList.toString(sb);
		sb.append('}');
		MISysInfo.print(sb);
	}
	
	/**
	 * 打印电线连接的上一个及下一个电线，只在服务端打印
	 */
	public static void printETLink(EleSrcCable et) {
		if (et.getWorld().isRemote) return;
		StringBuilder sb = new StringBuilder();
		sb.append("\n位于[")
		  .append(et.getPos())
		  .append(']')
		  .append("的电缆连接信息：\n")
		  .append("\tnext:[")
		  .append(et.getNext())
		  .append("]\n\tprev:[")
		  .append(et.getPrev())
		  .append("]");
		MISysInfo.print(sb);
	}
	
	/**
	 * 检查数组中是否含有true
	 *
	 * @throws NullPointerException 如果bools==null
	 */
	public static boolean hasTrue(Collection<Boolean> bools) {
		WaitList.checkNull(bools, "bools");
		
		for (boolean b : bools)
			if (b) return true;
		return false;
	}
	
	/**
	 * 检查数组中是否含有true
	 *
	 * @throws NullPointerException 如果bools==null
	 */
	public static boolean hasTrue(boolean... bools) {
		WaitList.checkNull(bools, "bools");
		
		for (boolean b : bools)
			if (b) return true;
		return false;
	}
	
	/**
	 * 打印et中连接的方向
	 * @param transfer 要打印的对象
	 * @param isPrintFalse 是否打印没有连接的方向
	 *
	 * @throws NullPointerException 如果et==null
	 */
	public static void printETShow(EleSrcCable transfer, boolean isPrintFalse) {
		checkNull(transfer, "transfer");
		
		StringBuilder sb = new StringBuilder();
		if (isPrintFalse) {
			sb.append("up=").append(transfer.getUp()).append(';')
			  .append("down=").append(transfer.getDown()).append(';')
			  .append("east=").append(transfer.getEast()).append(';')
			  .append("west=").append(transfer.getEast()).append(';')
			  .append("north=").append(transfer.getNorth()).append(';')
			  .append("south=").append(transfer.getSouth()).append(';');
		} else {
			if (transfer.getUp()) sb.append("up=true;");
			if (transfer.getDown()) sb.append("down=true;");
			if (transfer.getEast()) sb.append("east=true;");
			if (transfer.getWest()) sb.append("west=true;");
			if (transfer.getNorth()) sb.append("north=true;");
			if (transfer.getSouth()) sb.append("south=true;");
		}
		MISysInfo.print(sb);
	}
	
}
