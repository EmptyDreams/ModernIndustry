package top.kmar.mi.api.utils.container

import top.kmar.mi.api.utils.expands.isServer

/**
 * 双端对象容器
 * @author EmptyDreams
 */
class SideContainer<T>(
    private var service: T, private var client: T
) {
    
    var value: T
        get() = if (isServer()) service else client
        set(value) {
            if (isServer()) service = value
            else client = value
        }
    
    constructor(supplier: () -> T) : this(supplier(), supplier())
    
}