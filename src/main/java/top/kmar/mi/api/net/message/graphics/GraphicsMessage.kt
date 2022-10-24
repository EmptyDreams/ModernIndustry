package top.kmar.mi.api.net.message.graphics

import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.relauncher.Side
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

    override fun parseOnClient(message: NBTTagCompound, result: ParseAddition): ParseAddition {
        val container = Minecraft.getMinecraft().player.openContainer
        if (container !is BaseGraphics) return result.setParseResult(EXCEPTION)
        val addition = GraphicsAddition().apply { readFrom(message.getTag("add")) }
        val element = container.getElementByID(addition.id) ?: return result.setParseResult(EXCEPTION)
        element.client.receive(message.getTag("data"))
        return result.setParseResult(SUCCESS)
    }

    override fun parseOnServer(message: NBTTagCompound, result: ParseAddition): ParseAddition {
        val container = result.servicePlayer.openContainer
        if (container !is BaseGraphics) return result.setParseResult(EXCEPTION)
        val addition = GraphicsAddition().apply { readFrom(message.getTag("add")) }
        val element = container.getElementByID(addition.id) ?: return result.setParseResult(EXCEPTION)
        element.receiveNetworkMessage(message.getCompoundTag("data"))
        return result.setParseResult(SUCCESS)
    }

    override fun match(side: Side) = true

}