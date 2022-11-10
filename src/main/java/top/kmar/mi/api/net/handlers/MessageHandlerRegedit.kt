package top.kmar.mi.api.net.handlers

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import top.kmar.mi.api.utils.expands.isServer

/**
 * 信息处理器注册机
 * @author EmptyDreams
 */
object MessageHandlerRegedit {

    private val registries = Object2ObjectOpenHashMap<String, IAutoNetworkHandler>()

    /** 注册一个 [IAutoNetworkHandler] */
    fun registry(key: String, handler: IAutoNetworkHandler) {
        if (key in registries) throw AssertionError("key[$key]重复注册")
        registries[key] = handler
    }

    /** 注册一个客户端的 [IAutoNetworkHandler] */
    fun registryClient(key: String, handler: IAutoNetworkHandler) {
        if (isServer()) return
        registry(key, handler)
    }

    /** 查找指定的 handler */
    fun find(key: String) =
        registries[key] ?: throw AssertionError("key[$key]没有被注册")

}