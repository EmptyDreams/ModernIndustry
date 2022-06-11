package top.kmar.mi.api.net.message.panel

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.Side
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.net.ParseResultEnum
import top.kmar.mi.api.net.message.IMessageHandle
import top.kmar.mi.api.net.message.ParseAddition
import top.kmar.mi.api.utils.MISysInfo

/**
 * [IPanelContainer]的网络通信
 * @author EmptyDreams
 */
object PanelMessage : IMessageHandle<PanelAddition, ParseAddition> {

    override fun parseOnClient(message: IDataReader, result: ParseAddition): ParseAddition {
        val player = Minecraft.getMinecraft().player
        val gui = player.openContainer
        if (gui !is IPanelContainer) {
            MISysInfo.err("玩家[${player.name}]打开的窗体[${gui::class.simpleName}]没有实现[IPanelContainer]")
            return result.setParseResult(ParseResultEnum.THROW)
        }
        val addition = PanelAddition()
        addition.readFrom(message)
        gui.receive(addition.type!!, message.readData())
        return result.setParseResult(ParseResultEnum.SUCCESS)
    }

    override fun parseOnServer(message: IDataReader, result: ParseAddition): ParseAddition {
        val addition = PanelAddition()
        addition.readFrom(message)
        val player = addition.player!!
        val gui = player.openContainer
        if (gui !is IPanelContainer) {
            MISysInfo.err("玩家[${player.name}]打开的窗体[${gui::class.simpleName}]没有实现[IPanelContainer]")
            return result.setParseResult(ParseResultEnum.THROW)
        }
        gui.receive(addition.type!!, message.readData())
        return result.setParseResult(ParseResultEnum.SUCCESS)
    }

    override fun match(side: Side) = true

}