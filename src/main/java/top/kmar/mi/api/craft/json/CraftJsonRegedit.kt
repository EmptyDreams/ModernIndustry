package top.kmar.mi.api.craft.json

import com.google.gson.*
import net.minecraft.item.ItemStack
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import org.apache.commons.io.FilenameUtils
import top.kmar.mi.api.craft.CraftGuide
import top.kmar.mi.api.craft.elements.CraftOutput
import top.kmar.mi.api.craft.shapes.DisorderlyShape
import top.kmar.mi.api.craft.shapes.IShape
import top.kmar.mi.api.craft.shapes.OrderlyShape
import top.kmar.mi.api.utils.MISysInfo
import java.nio.file.Files
import java.util.*

/**
 * JSON解析器注册机
 * @author EmptyDreams
 */
@EventBusSubscriber
object CraftJsonRegedit {

    /** 解析所有JSON */
    fun parserAll() {
        val gson = JsonParser()
        Loader.instance().activeModList.forEach { mod ->
            CraftingHelper.findFiles(
                mod, "assets/${mod.modId}/mi_files/recipes", { true },
                { root, file ->
                    Loader.instance().setActiveModContainer(mod)
                    val relative = root.relativize(file).toString()
                    if ("json" != FilenameUtils.getExtension(relative)) return@findFiles true
                    try {
                        @Suppress("BlockingMethodInNonBlockingContext")
                        val json = Files.newBufferedReader(file).use { gson.parse(it) }.asJsonObject
                        parserTarget(json, FilenameUtils.getBaseName(relative))
                    } catch (e: Exception) {
                        MISysInfo.err("在解析指定JSON文件[$file]时遇到异常", e)
                        return@findFiles false
                    }
                    return@findFiles true
                }, true, true
            )
        }
    }

    /** 解析指定JSON对象并将结果传递到注册机中 */
    private fun parserTarget(json: JsonObject, defID: String) {
        val group = json["group"].asString
        val id = json["id"]?.asString ?: defID
        val keyMap = getKeyMap(json)
        val shape = getShape(json["input"], keyMap)
        val output = getOutput(json["output"], keyMap)
        CraftGuide.registry(group, id, shape, output)
    }

    private fun getKeyMap(json: JsonObject): ItemPredicateMap {
        val result = ItemPredicateMap()
        if (json.has("key")) {
            val keys = json.getAsJsonObject("key")
            keys.entrySet().forEach { (key, value) ->
                require(key.length == 1) { "包含错误的键：$key，key的键只能为长度为 1 的字符串" }
                result[key.first()] = value.asJsonObject
            }
        }
        return result
    }

    /** 获取一个合成表的输入规则 */
    private fun getShape(json: JsonElement, keyMap: ItemPredicateMap): IShape {
        return if (json.isJsonPrimitive) {    // 无序
            val builder = DisorderlyShape.Builder()
            json.asString.forEach { builder.add(keyMap.getPredicate(it)) }
            builder.build()
        } else {
            json as JsonArray
            if (json[0].isJsonObject) {   // 无序
                val builder = DisorderlyShape.Builder()
                json.asSequence()
                    .map { it.asJsonObject }
                    .forEach {
                        builder.add(ItemPredicateMap.parserPredicate(it))
                    }
                builder.build()
            } else {    // 有序
                val builder = OrderlyShape.Builder()
                json.asSequence()
                    .map { it.asString }
                    .forEach {
                        builder.newLine()
                        for (key in it)
                            builder.insert(keyMap.getPredicate(key))
                    }
                builder.build()
            }
        }
    }

    /** 获取合成表的输出 */
    private fun getOutput(json: JsonElement, keyMap: ItemPredicateMap): CraftOutput {
        val output = CraftOutput()
        if (json.isJsonObject) {
            json as JsonObject
            json.entrySet().forEach { (key, value) ->
                when (key) {
                    "stacks" -> {
                        val list = LinkedList<ItemStack>()
                        if (value.isJsonObject) {
                            list.add(ItemPredicateMap.parserStack(value.asJsonObject))
                        } else if (value.isJsonArray) {
                            value as JsonArray
                            value.forEach { list.add(ItemPredicateMap.parserStack(it.asJsonObject)) }
                        } else {
                            val string = value.asString
                            string.forEach { list.add(keyMap.getStack(it)) }
                        }
                        output.stacks = list
                    }
                    else -> {
                        value as JsonPrimitive
                        when {
                            value.isNumber -> output.setInt(key, value.asInt)
                            value.isBoolean -> output.setBoolean(key, value.asBoolean)
                            else -> output.setString(key, value.asString)
                        }
                    }
                }
            }
        } else {
            val string = json.asString
            val list = LinkedList<ItemStack>()
            string.forEach { list.add(keyMap.getStack(it)) }
            output.stacks = list
        }
        return output
    }

}