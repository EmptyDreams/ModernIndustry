package xyz.emptydreams.mi.register.json;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.blocks.base.MIProperty;
import xyz.emptydreams.mi.register.AutoRegister;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.EnumMap;
import java.util.Map;

/**
 * 方块Json生成器
 * @author EmptyDreams
 */
public final class BlockJsonBuilder {

	/** 根目录 */
	static final File FATHER;
	/** 模板所在目录 */
	static final File TEMPLATE;
	/** 输出目录 */
	static final File[] OUTPUTS = new File[2];
	/** 模板 */
	static final Map<Type, String> TEMPLATE_DATA;

	static {
		FATHER = Minecraft.getMinecraft().mcDataDir.getAbsoluteFile().getParentFile().getParentFile();
		OUTPUTS[0] = new File(FATHER,"src/main/resources/assets/mi/blockstates");
		OUTPUTS[1] = new File(FATHER, "out/production/ModernIndustry.main/assets/mi/blockstates");
		TEMPLATE = new File(FATHER, "src/main/resources/assets/mi/templates/block");
		
		for (File output : OUTPUTS) {
			output.mkdirs();
		}
		
		try {
			TEMPLATE_DATA = getTemplates();
		} catch (IOException e) {
			throw new RuntimeException("读取模板文件时出现异常", e);
		}
	}

	/**
	 * 生成Json文件
	 * @throws IOException 如果发生I/O错误
	 */
	public static void build() throws IOException {
		int step = 0, build = 0;
		for (Block block : AutoRegister.Blocks.blocks) {
			if (isNeedSkip(block)) {
				++step;
				continue;
			}
			String out = formatData(TEMPLATE_DATA.get(getTemplateType(block)),
										block.getRegistryName().getResourcePath());
			writeJson(out, block);
			++build;
		}

		MISysInfo.print("Block Json：跳过：" + step + "，生成：" + build + "，总计：" + Integer.sum(step, build));
	}

	/**
	 * 获取方块的模板
	 * @param block 方块
	 * @return 模板类型
	 */
	private static Type getTemplateType(Block block) {
		ImmutableMap<IProperty<?>, Comparable<?>> properties = block.getDefaultState().getProperties();
		if (properties.isEmpty()) return Type.NON;
		if (properties.containsKey(MIProperty.EMPTY)) return Type.EMPTY;
		if (properties.containsKey(MIProperty.WORKING)) return Type.WORKING;
		if (properties.containsKey(MIProperty.HORIZONTAL)) return Type.FACING;
		throw new IllegalArgumentException("输入了不支持的方块：" + block.getRegistryName());
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

	/**
	 * 读取文件内容
	 * @param file 文件目录
	 * @return 读取的数据
	 * @throws IOException 如果发生I/O错误
	 */
	static String readFile(File file) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			StringBuilder stringBuilder = new StringBuilder();
			String temp;
			while ((temp = reader.readLine()) != null) {
				stringBuilder.append(temp);
				stringBuilder.append('\n');
			}
			return stringBuilder.toString();
		}
	}

	/**
	 * 格式化数据
	 * @param template 模板
	 * @param name 方块名称
	 * @return 格式化后的数据
	 */
	private static String formatData(String template, String name) {
		return template.replaceAll("template:src",
				"mi:blocks/machine/" + name + "/src")
				.replaceAll("template:working",
						"mi:blocks/machine/" + name + "/working");
	}

	/**
	 * 判断是否需要跳过写入数据
	 * @param block 方块
	 * @throws IOException 如果发生I/O错误
	 */
	private static boolean isNeedSkip(Block block) throws IOException {
		File file = getFile(OUTPUTS[0], block);
		return !file.createNewFile();
	}

	/**
	 * 将指定的数据写入到文件中
	 * @param text 数据内容
	 * @param block 方块
	 * @throws IOException 如果发生I/O错误
	 */
	private static void writeJson(String text, Block block) throws IOException {
		for (File output : OUTPUTS) {
			File file = getFile(output, block);
			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file)))) {
				writer.write(text);
			}
		}
	}

	/**
	 * 获取具体名称
	 * @param father 目标文件夹
	 * @param block 方块
	 * @return 具体名称
	 */
	private static File getFile(File father, Block block) {
		return new File(father, block.getRegistryName().getResourcePath() + ".json");
	}

	private enum Type {

		NON("template_non.json"),
		FACING("template_horizontal.json"),
		WORKING("template_working.json"),
		EMPTY("template_empty.json");

		private final String name;

		Type(String name) {
			this.name = name;
		}

		public String getName() { return name; }

	}

}
