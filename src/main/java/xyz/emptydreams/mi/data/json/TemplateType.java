package xyz.emptydreams.mi.data.json;

/**
 * 模板类型
 * @author EmptyDreams
 */
public enum TemplateType {
	
	BLOCK("block"),
	ITEM("item");
	
	private final String name;
	
	TemplateType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static TemplateType from(String name) {
		for (TemplateType value : TemplateType.values()) {
			if (value.getName().equals(name)) return value;
		}
		throw new IllegalArgumentException("输入的名称不存在：" + name);
	}
	
}