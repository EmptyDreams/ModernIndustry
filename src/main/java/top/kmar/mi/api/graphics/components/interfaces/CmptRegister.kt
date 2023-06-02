package top.kmar.mi.api.graphics.components.interfaces

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.utils.MISysInfo
import top.kmar.mi.api.utils.expands.applyClient

/**
 * 控件注册机
 * @author EmptyDreams
 */
object CmptRegister {

    /** 存储已注册的控件 */
    private val componentMap = Object2ObjectOpenHashMap<String, Class<Cmpt>>()

    /**
     * 注册一个控件
     *
     * 注意：非MI内置控件均应采用`modid:name`的格式命名，以防名称冲突
     *
     * @return 是否注册成功，当输入的key已经被注册时会返回false
     */
    fun registry(key: String, component: Class<Cmpt>): Boolean {
        if (key in componentMap) return false
        applyClient {
            if (key in StyleNode) {
                MISysInfo.err("警告：禁止将属性名称作为控件名称注册！该控件[$key]已被跳过注册。")
                return false
            }
        }
        componentMap[key] = component
        return true
    }

    /**
     * 强制注册一个控件，如果`key`已被注册则覆盖原有值
     *
     * 注意：非MI内置控件均应采用`modid:name`的格式命名，以防名称冲突
     *
     * @throws IllegalArgumentException 如果 [key] 的值属于样式的属性名称
     */
    fun registryForce(key: String, component: Class<Cmpt>) {
        applyClient {
            if (key in StyleNode)
                throw IllegalArgumentException("警告：禁止将属性名称作为控件名称注册！")
        }
        componentMap[key] = component
    }

    /**
     * 构建一个控件的服务端对象
     * @param key 控件的`key`值
     * @param T 控件类型
     * @throws NullPointerException 如果`key`没有被注册
     * @throws NoSuchElementException 如果控件没有包含共有的接收一个`String`的构造函数
     * @throws ClassCastException 如果传入的`T`不是从该控件派生的
     */
    fun <T : Cmpt> buildServiceCmpt(key: String, attributes: CmptAttributes): T {
        val clazz = find(key) ?: throw NullPointerException("指定的key[$key]没有被注册")
        val builder = clazz.getConstructor(attributes.javaClass)
        @Suppress("UNCHECKED_CAST")
        return builder.newInstance(attributes) as T
    }

    /**
     * 构建一个控件的客户端对象
     * @param key 控件的`key`值
     * @param attributes 属性列表
     * @param T 控件类型
     * @throws NullPointerException 如果`key`没有被注册
     * @throws NoSuchElementException 如果控件没有包含共有的接收一个`String`的构造函数
     * @throws ClassCastException 如果传入的`T`不是从该控件派生的
     */
    fun <T : CmptClient> buildClientCmpt(key: String, attributes: CmptAttributes): T {
        val service = buildServiceCmpt<Cmpt>(key, attributes)
        @Suppress("UNCHECKED_CAST")
        return service.client as T
    }

    /** 通过Key查找控件的Class对象，没有注册则返回`null` */
    fun find(key: String): Class<Cmpt>? = componentMap[key]

}