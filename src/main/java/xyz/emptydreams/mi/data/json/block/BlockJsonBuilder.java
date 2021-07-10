package xyz.emptydreams.mi.data.json.block;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.BlockFluidClassic;
import xyz.emptydreams.mi.api.register.machines.BlockRegistryMachine;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.data.json.KeyList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 方块Json生成器
 * @author EmptyDreams
 */
public final class BlockJsonBuilder {
	
	private BlockJsonBuilder() { throw new AssertionError(); }
	
	public static final File ROOT = new File("").getAbsoluteFile().getParentFile();
	public static final List<File> OUT_PATH = Lists.asList(
			new File(ROOT, "src/main/resources/assets/mi/blockstates"),
			new File[] { new File(ROOT, "out/production/ModernIndustry.main/assets/mi/blockstates") }
	);
	public static final File TEMPLATE = new File(ROOT, "src/main/resources/assets/mi/templates");
	public static final File TEMPLATE_INFO = new File(TEMPLATE, "block_info.json");
	
	static final List<BlockTemplateInfo> INFO_LIST = new ObjectArrayList<>();
	
	/**
	 * 构建JSON
	 * @throws IllegalArgumentException 如果文件目录存在问题
	 */
	public static void build() {
		checkPath();
		createOutPath();
		readInfo();
		if (INFO_LIST.isEmpty()) return;
		try {
			writeFile();
		} catch (IOException e) {
			MISysInfo.err("文件写入失败！");
			e.printStackTrace();
		}
	}
	
	private static void writeFile() throws IOException {
		int step = 0, build = 0;
		for (Block block : BlockRegistryMachine.Blocks.blocks) {
			if (isNeedSkip(block)) {
				++step;
				continue;
			}
			String info = formatText(getTemplate(block).getText(), block);
			writeJson(info, block);
			++build;
		}
		MISysInfo.print("Block Json：跳过：" + step + "，生成：" + build + "，总计：" + Integer.sum(step, build));
	}
	
	/**
	 * 将指定的数据写入到文件中
	 * @param text 数据内容
	 * @param block 方块
	 * @throws IOException 如果发生I/O错误
	 */
	private static void writeJson(String text, Block block) throws IOException {
		for (File output : OUT_PATH) {
			File file = getFile(output, block);
			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file)))) {
				writer.write(text);
			}
		}
	}
	
	/** 格式化文本 */
	private static String formatText(String text, Block block) {
		String result = text;
		for (Map.Entry<String, Function<Object, String>> entry : KeyList.entrySet()) {
			result = result.replaceAll(entry.getKey(), entry.getValue().apply(block));
		}
		return result;
	}
	
	/** 获取模板 */
	private static BlockTemplateInfo getTemplate(Block block) {
		for (BlockTemplateInfo info : INFO_LIST) {
			if (info.match(block.getDefaultState())) return info;
		}
		throw new IllegalArgumentException("没有适配的模板[" + block.getRegistryName() + "]");
	}
	
	/**
	 * 判断是否需要跳过写入数据
	 * @param block 方块
	 * @throws IOException 如果发生I/O错误
	 */
	private static boolean isNeedSkip(Block block) throws IOException {
		if (block instanceof BlockFluidClassic) return true;
		File file = getFile(OUT_PATH.get(0), block);
		return !file.createNewFile();
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
	
	/** 读取基本信息 */
	private static void readInfo() {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(TEMPLATE_INFO))) {
			reader.lines().forEach(builder::append);
		} catch (IOException e) {
			MISysInfo.err("BlockJson生成失败", e);
			return;
		}
		
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(builder.toString()).getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			INFO_LIST.add(new BlockTemplateInfo(entry.getValue().getAsJsonObject(), entry.getKey()));
		}
	}
	
	/** 检查目录是否正常 */
	private static void checkPath() {
		if (!ROOT.exists())
			throw new IllegalArgumentException("文件夹路径不存在：" + ROOT.getAbsolutePath());
		if (!ROOT.isDirectory())
			throw new IllegalArgumentException("根目录设置错误，应该指向文件夹：" + ROOT.getAbsolutePath());
		if (!TEMPLATE.exists())
			throw new IllegalArgumentException("文件夹路径不存在：" + TEMPLATE.getAbsolutePath());
		if (!TEMPLATE.isDirectory())
			throw new IllegalArgumentException("根目录设置错误，应该指向文件夹：" + TEMPLATE.getAbsolutePath());
		if (!TEMPLATE_INFO.exists())
			throw new IllegalArgumentException("文件路径不存在：" + TEMPLATE_INFO.getAbsolutePath());
	}
	
	/** 生成输出目录 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static void createOutPath() {
		OUT_PATH.stream().filter(file -> !file.exists()).forEach(File::mkdirs);
	}
	
	static final List<EnumFacing> HOR = new ObjectArrayList<>(
			new EnumFacing[] {
					EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST });
	static final List<EnumFacing> VER = new ObjectArrayList<>(
			new EnumFacing[]{ EnumFacing.UP, EnumFacing.DOWN });
	
}