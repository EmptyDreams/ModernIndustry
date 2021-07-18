package xyz.emptydreams.mi.data.json.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import xyz.emptydreams.mi.api.exception.TransferException;
import xyz.emptydreams.mi.api.utils.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import static xyz.emptydreams.mi.data.json.block.BlockJsonBuilder.ROOT;
import static xyz.emptydreams.mi.data.json.block.BlockJsonBuilder.TEMPLATE_INFO;

/**
 * 存储模板信息
 * @author EmptyDreams
 */
public class BlockTemplateInfo {
	
	private final String[] name;
	private final BlockPropertyType[] type;
	private final String text;
	private final Method checkMethod;
	
	/**
	 * 构建一个INFO
	 * @param jsonObject check属性的JsonObject对象
	 * @param templateName 当前模板名称
	 */
	public BlockTemplateInfo(JsonObject jsonObject, String templateName) {
		File path = new File(ROOT, "src/main/resources/assets/mi/templates/block/"
												+ jsonObject.get("name").getAsString());
		JsonObject json = jsonObject.getAsJsonObject("check");
		JsonArray jsonName = json.getAsJsonArray("name");
		JsonArray jsonType = json.getAsJsonArray("type");
		name = new String[jsonName.size()];
		type = new BlockPropertyType[jsonType.size()];
		if (name.length != type.length)
			throw new IllegalArgumentException(TEMPLATE_INFO.getName()
					+ "文件中[" + templateName + "]的`check`属性中`name`数量和`type`数量不一致");
		for (int i = 0; i < name.length; ++i) {
			name[i] = jsonName.get(i).getAsString();
			type[i] = BlockPropertyType.from(jsonType.get(i).getAsString());
		}
		try {
			if (json.has("class"))
				checkMethod = StringUtil.getMethod(json.get("class").getAsString());
			else checkMethod = null;
		} catch (Exception e) {
			throw TransferException.instance("方法获取失败", e);
		}
		
		try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
			text = reader.lines().reduce(new StringBuilder(),
					(stringBuilder, str) -> stringBuilder.append(str).append('\n'),
					(arg0, arg1) -> null).toString();
		} catch (IOException e) {
			throw TransferException.instance("模板文件读取失败：" + path, e);
		}
	}
	
	/**
	 * 判断指定的Properties是否符合要求
	 * @param state 方块默认的状态
	 */
	public boolean match(IBlockState state) {
		if (classCheck(state)) return true;
		Set<IProperty<?>> properties = state.getProperties().keySet();
		if (properties.size() != name.length) return false;
		if (name.length == 0) return true;
		IntList index = new IntArrayList(name.length);
		for (int i = 0; i < name.length; ++i) index.add(i);
		o : for (IProperty<?> value : properties) {
			IntIterator it = index.iterator();
			int key;
			while (it.hasNext()) {
				key = it.nextInt();
				if ((name[key].equals("*") || name[key].equals(value.getName()))
						&& type[key].match(value)) {
					it.remove();
					continue o;
				}
			}
			return false;
		}
		return true;
	}
	
	private boolean classCheck(IBlockState state) {
		try {
			if (checkMethod == null) return false;
			return (boolean) checkMethod.invoke(null, state);
		} catch (Exception e) {
			throw TransferException.instance("方法调用异常", e);
		}
	}
	
	public String getText() {
		return text;
	}
	
}