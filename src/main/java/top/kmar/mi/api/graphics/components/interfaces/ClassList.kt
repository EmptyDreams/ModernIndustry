package top.kmar.mi.api.graphics.components.interfaces

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

/**
 * 类名列表
 * @author EmptyDreams
 */
class ClassList(
    private val onEdit: () -> Unit
) {

    private val list = ObjectOpenHashSet<String>()

    operator fun plusAssign(name: String) {
        list += name
        onEdit()
    }

    fun remove(name: String) {
        list.remove(name)
        onEdit()
    }

    operator fun contains(name: String): Boolean = name in list

}