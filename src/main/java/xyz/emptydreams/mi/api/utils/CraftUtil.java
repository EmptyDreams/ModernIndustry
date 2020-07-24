package xyz.emptydreams.mi.api.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.ULProCraftGuide;

import javax.annotation.Nonnull;

/**
 * 封装了一些常用的关于合成表的操作
 * @author EmptyDreams
 */
public final class CraftUtil {

	/**
	 * 创建一个只有一个输入和一个输出的合成表
	 * @param input 输入
	 * @param output 输出
	 * @return 合成表
	 */
	public static ULProCraftGuide createOneCraft(ItemStack input, ItemStack output) {
		return new ULProCraftGuide(1).addElement(ItemElement.instance(input)).setOut(ItemElement.instance(output));
	}

	/**
	 * 创建一个只有一个输入和一个输出的合成表
	 * @param input 输入
	 * @param output 输出
	 * @return 合成表
	 */
	@Nonnull
	public static ULProCraftGuide createOneCraft(Item input, Item output) {
		return createOneCraft(input, output, 1);
	}

	/**
	 * 创建一个只有一个输入和一个输出的合成表
	 * @param input 输入
	 * @param output 输出
	 * @param outputSize 输出数量
	 * @return 合成表
	 */
	@Nonnull
	public static ULProCraftGuide createOneCraft(Item input, Item output, int outputSize) {
		return new ULProCraftGuide(1).addElement(input).setOut(ItemElement.instance(output, outputSize));
	}

	/**
	 * 创建一个只有一个输入和一个输出的合成表
	 * @param input 输入
	 * @param output 输出
	 * @return 合成表
	 */
	@Nonnull
	public static ULProCraftGuide createOneCraft(Block input, Item output) {
		return createOneCraft(input, output, 1);
	}

	/**
	 * 创建一个只有一个输入和一个输出的合成表
	 * @param input 输入
	 * @param output 输出
	 * @param outputSize 输出数量
	 * @return 合成表
	 */
	@Nonnull
	public static ULProCraftGuide createOneCraft(Block input, Item output, int outputSize) {
		return new ULProCraftGuide(1).addElement(Item.getItemFromBlock(input))
												.setOut(ItemElement.instance(output, outputSize));
	}

}
