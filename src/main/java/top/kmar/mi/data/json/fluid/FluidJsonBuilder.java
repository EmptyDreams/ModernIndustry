package top.kmar.mi.data.json.fluid;

import net.minecraftforge.fluids.Fluid;
import top.kmar.mi.api.exception.TransferException;
import top.kmar.mi.api.utils.MISysInfo;
import top.kmar.mi.data.json.KeyList;
import top.kmar.mi.api.regedits.machines.FluidRegistryMachine;
import top.kmar.mi.data.json.block.BlockJsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author EmptyDreams
 */
public class FluidJsonBuilder {

    /** 根目录 */
    private static final File FATHER = BlockJsonBuilder.ROOT;
    /** 模板所在目录 */
    private static final File TEMPLATE = new File(FATHER, "src/main/resources/assets/mi/templates/fluid");
    /** 输出目录 */
    private static final File[] OUTPUTS = new File[2];
    /** 模板 */
    private static final Map<Type, String> TEMPLATE_DATA;

    static {
        OUTPUTS[0] = new File(FATHER, "src/main/resources/assets/mi/blockstates");
        OUTPUTS[1] = new File(FATHER, "out/production/ModernIndustry.main/assets/mi/blockstates");
        try {
            TEMPLATE_DATA = getTemplates();
        } catch (IOException e) {
            throw TransferException.instance("读取模板文件时发生异常", e);
        }
    }

    public static void build() {
        int step = 0, build = 0;
        try {
            for (Fluid fluid : FluidRegistryMachine.Fluids.fluids) {
                if (isNeedSkip(fluid)) {
                    ++step;
                    continue;
                }

                Type type = getType(fluid);
                String template = TEMPLATE_DATA.get(type).replaceAll("template::name", fluid.getName());
                writeJson(template, fluid);
                ++build;
            }
        } catch (IOException e) {
            throw TransferException.instance("FluidJson生成失败", e);
        }
        MISysInfo.print("Fluid Json：跳过：" + step + "，生成：" + build + "，总计：" + Integer.sum(step, build));
    }

    private static void writeJson(String text, Fluid fluid) throws IOException {
        for (File output : OUTPUTS) {
            File file = getFile(output, fluid);
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file)))) {
                writer.write(text);
            }
        }
    }

    private static Type getType(Fluid fluid) {
        return Type.SRC;
    }

    /** 格式化文本 */
    private static String formatText(String text, Fluid fluid) {
        String result = text;
        for (Map.Entry<String, Function<Object, String>> entry : KeyList.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue().apply(fluid));
        }
        return result;
    }

    private static boolean isNeedSkip(Fluid fluid) throws IOException {
        File file = getFile(OUTPUTS[0], fluid);
        return !file.createNewFile();
    }

    private static File getFile(File father, Fluid fluid) {
        return new File(father, fluid.getName() + ".json");
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
    private static String readFile(File file) throws IOException {
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

    enum Type {

        SRC("fluid.json");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}