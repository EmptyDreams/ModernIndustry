package xyz.emptydreams.mi.api.utils;

import xyz.emptydreams.mi.blocks.tileentity.EleSrcCable;

import java.util.Collection;

import static xyz.emptydreams.mi.api.utils.StringUtil.checkNull;

/**
 * 用于各个类的调试
 * @author EmptyDreams
 */
@SuppressWarnings("SpellCheckingInspection")
public final class DebugHelper {
	
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
		StringUtil.checkNull(bools, "bools");
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
		StringUtil.checkNull(bools, "bools");
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
