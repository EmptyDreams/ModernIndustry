package top.kmar.mi.api.graphics.components.interfaces

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

/**
 * 类名列表
 * @author EmptyDreams
 */
class ClassList(
    onEdit: () -> Unit
) {

    private val list = ObjectOpenHashSet<String>()

    operator fun plusAssign(name: String) {
        list += name
    }

    fun remove(name: String) {
        list.remove(name)
    }

    operator fun contains(name: String): Boolean = name in list

}