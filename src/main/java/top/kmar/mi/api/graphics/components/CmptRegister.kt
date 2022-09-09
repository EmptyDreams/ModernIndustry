package top.kmar.mi.api.graphics.components

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

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
        componentMap[key] = component
        return true
    }

    /**
     * 强制注册一个控件，如果`key`已被注册则覆盖原有值
     *
     * 注意：非MI内置控件均应采用`modid:name`的格式命名，以防名称冲突
     */
    fun registryForce(key: String, component: Class<Cmpt>) {
        componentMap[key] = component
    }

    /**
     * 构建一个控件的服务端对象
     * @param key 控件的`key`值
     * @param args 参数列表
     * @param T 控件类型
     * @throws NullPointerException 如果`key`没有被注册
     * @throws NoSuchMethodException 如果控件没有包含一个接受`ICmptBuildData`的共有构造函数
     * @throws ClassCastException 如果传入的`T`不是从该控件派生的
     */
    fun <T : Cmpt> buildServiceCmpt(key: String, args: ICmptBuildData = EmptyCmptBuildData): T {
        val clazz = find(key) ?: throw NullPointerException("指定的key[$key]没有被注册")
        val constructor = clazz.getConstructor(Map::class.java)
        @Suppress("UNCHECKED_CAST")
        return constructor.newInstance(args) as T
    }

    /**
     * 构建一个控件的客户端对象
     * @param key 控件的`key`值
     * @param args 参数列表
     * @param T 控件类型
     * @throws NullPointerException 如果`key`没有被注册
     * @throws NoSuchMethodException 如果控件没有包含一个接受`ICmptBuildData`的共有构造函数
     * @throws ClassCastException 如果传入的`T`不是从该控件派生的
     */
    fun <T : CmptClient> buildClientCmpt(key: String, args: ICmptBuildData = EmptyCmptBuildData): T {
        val service = buildServiceCmpt<Cmpt>(key, args)
        @Suppress("UNCHECKED_CAST")
        return service.client as T
    }

    /** 通过Key查找控件的Class对象，没有注册则返回`null` */
    fun find(key: String): Class<Cmpt>? = componentMap[key]

}