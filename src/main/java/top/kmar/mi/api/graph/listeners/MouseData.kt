package top.kmar.mi.api.graph.listeners

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter

/**
 * 鼠标事件数据
 */
data class MouseData(
    /** X轴坐标，小于`0`表明数据无效 */
    private var mouseX: Float = -1f,
    /** Y轴坐标，小于`0`表明数据无效 */
    private var mouseY: Float = -1f,
    /** 鼠标按键种类，为[Int.MIN_VALUE]表明数据无效] */
    private var code: Int = Int.MIN_VALUE,
    /** 鼠标滚轮滚动，为[Int.MIN_VALUE]表明数据无效 */
    private var wheel: Int = Int.MIN_VALUE,
    override val isNeedSync: Boolean = true
) : IListenerData {

    companion object {

        @JvmStatic
        val EMPTY_DATA = MouseData(-1f, -1f,
                            Int.MIN_VALUE, Int.MIN_VALUE, false)

    }

    fun getMouseX(): Float {
        if (!hasMouseX()) throw IllegalArgumentException("该项不存在")
        return mouseX
    }

    fun getMouseY(): Float {
        if (!hasMouseY()) throw IllegalArgumentException("该项不存在")
        return mouseY
    }

    fun getCode(): Int {
        if (!hasCode()) throw IllegalArgumentException("该项不存在")
        return code
    }

    fun getWheel(): Int {
        if (!hasWheel()) throw IllegalArgumentException("该项不存在")
        return wheel
    }

    fun hasMouseX() = mouseX >= 0
    fun hasMouseY() = mouseY >= 0
    fun hasCode() = code != Int.MIN_VALUE
    fun hasWheel() = wheel != Int.MIN_VALUE

    override fun wirte(writer: IDataWriter) {
        var flag = 0
        if (hasMouseX()) flag = flag or 1
        if (hasMouseY()) flag = flag or 2
        if (hasCode()) flag = flag or 4
        if (hasWheel()) flag = flag or 8
        writer.writeByte(flag.toByte())
        if (hasMouseX()) writer.writeFloat(mouseX)
        if (hasMouseY()) writer.writeFloat(mouseY)
        if (hasCode()) writer.writeVarInt(code)
        if (hasWheel()) writer.writeVarInt(wheel)
    }

    override fun read(reader: IDataReader) {
        val flag = reader.readByte().toInt()
        mouseX = if (flag.and(1) == 1) reader.readFloat() else -1f
        mouseY = if (flag.and(2) == 1) reader.readFloat() else -1f
        code = if (flag.and(4) == 1) reader.readVarInt() else Int.MIN_VALUE
        wheel = if (flag.and(8) == 1) reader.readVarInt() else Int.MIN_VALUE
    }

}