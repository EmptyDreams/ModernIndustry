package xyz.emptydreams.mi.api.register.json;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import xyz.emptydreams.mi.api.register.AutoRegister;
import xyz.emptydreams.mi.api.tools.item.IToolMaterial;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.EnumMap;
import java.util.Map;

import static xyz.emptydreams.mi.api.register.json.BlockJsonBuilder.readFile;

/**
 * 物品Json生成器
 * @author EmptyDreams
 */
public final class ItemJsonBuilder {

	/** 根目录 */
	private static final File FATHER = BlockJsonBuilder.FATHER;
	/** 模板所在目录 */
	private static final File TEMPLATE = new File(FATHER, "src/main/resources/assets/mi/templates/item");
	/** 输出目录 */
	private static final File[] OUTPUTS = new File[2];
	/** 模板 */
	private static final Map<Type, String> TEMPLATE_DATA;

	static {
		OUTPUTS[0] = new File(FATHER, "src/main/resources/assets/mi/models/item");
		OUTPUTS[1] = new File(FATHER, "out/production/ModernIndustry.main/assets/mi/models/item");
		try {
			TEMPLATE_DATA = getTemplates();
		} catch (IOException e) {
			throw new RuntimeException("读取模板文件时发生异常", e);
		}
	}

	public static void build() throws IOException {
		int step = 0, build = 0;
		for (Item item : AutoRegister.Items.items) {
			if (isNeedSkip(item)) {
				++step;
				continue;
			}

			Type type = getType(item);
			String template = TEMPLATE_DATA.get(type).replaceAll(
							"template:name", item.getRegistryName().getResourcePath());
			writeJson(template, item);
			++build;
		}
		MISysInfo.print("Item Json：跳过：" + step + "，生成：" + build + "，总计：" + Integer.sum(step, build));
	}

	private static void writeJson(String text, Item item) throws IOException {
		for (File output : OUTPUTS) {
			File file = getFile(output, item);
			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file)))) {
				writer.write(text);
			}
		}
	}

	private static Type getType(Item item) {
		if (item instanceof ItemTool || item instanceof ItemSword ||
				item instanceof ItemHoe || item instanceof IToolMaterial ||
				item.getRegistryName().getResourcePath().contains("debug")) return Type.HANDHELD;
		if (item instanceof ItemArmor) return Type.ARMOR;
		return Type.COMMON;
	}

	private static boolean isNeedSkip(Item item) throws IOException {
		File file = getFile(OUTPUTS[0], item);
		return !file.createNewFile();
	}

	private static File getFile(File father, Item item) {
		return new File(father, item.getRegistryName().getResourcePath() + ".json");
	}

	/**
	 * 获取模板信息
	 * @return 包含模板信息的Map
	 * @throws IOException 如果发生I/O错误
	 */
	private static Map<Type, String> getTemplates() throws IOException {
		Map<Type, String> templates = new EnumMap<>(Type.class);
		for (Type type : Type.values()) {
			String data = readFile(new File(TEMPLATE, type.getName()));
			templates.put(type, data);
		}
		return templates;
	}

	private enum Type {

		ARMOR("armor.json"),
		COMMON("common.json"),
		HANDHELD("handheld.json");

		private final String NAME;

		Type(String name) {
			NAME = name;
		}

		String getName() {
			return NAME;
		}

	}

}
