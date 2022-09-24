package top.kmar.mi.api.net.message.graphics

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.Side
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.net.ParseResultEnum.EXCEPTION
import top.kmar.mi.api.net.ParseResultEnum.SUCCESS
import top.kmar.mi.api.net.message.IMessageHandle
import top.kmar.mi.api.net.message.ParseAddition

/**
 * GUI控件双端通信
 * @author EmptyDreams
 */
object GraphicsMessage : IMessageHandle<GraphicsAddition, ParseAddition> {

    override fun parseOnClient(message: IDataReader, result: ParseAddition): ParseAddition {
        val container = Minecraft.getMinecraft().player.openContainer
        if (container !is BaseGraphics) return result.setParseResult(EXCEPTION)
        val addition = GraphicsAddition().apply { readFrom(message) }
        val element = container.getElementByID(addition.id) ?: return result.setParseResult(EXCEPTION)
        element.client.receive(message.readData())
        return result.setParseResult(SUCCESS)
    }

    override fun parseOnServer(message: IDataReader, result: ParseAddition): ParseAddition {
        val container = result.servicePlayer.openContainer
        if (container !is BaseGraphics) return result.setParseResult(EXCEPTION)
        val addition = GraphicsAddition().apply { readFrom(message) }
        val element = container.getElementByID(addition.id) ?: return result.setParseResult(EXCEPTION)
        element.receive(message.readData())
        return result.setParseResult(SUCCESS)
    }

    override fun match(side: Side) = true

}