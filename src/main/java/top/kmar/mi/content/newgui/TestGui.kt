package top.kmar.mi.content.newgui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.GuiLoader
import top.kmar.mi.api.graphics.components.BackgroundCmpt
import top.kmar.mi.api.graphics.components.MaskCmpt
import top.kmar.mi.api.graphics.utils.FixedSizeMode
import top.kmar.mi.api.utils.applyClient

/**
 *
 * @author EmptyDreams
 */
@EventBusSubscriber
class TestGui : BaseGraphics() {

    override fun init(player: EntityPlayer, pos: BlockPos) {
        val mask = MaskCmpt("mask")
        val background = BackgroundCmpt("background").applyClient {
            client.style.width = FixedSizeMode(230)
            client.style.height = FixedSizeMode(230)
        }
        mask.addChild(background)
        addChild(mask)
    }

    companion object {

        @JvmStatic
        @SubscribeEvent
        fun registry(event: GuiLoader.MIGuiRegistryEvent) {
            event.registry(TestGui::class.java)
        }

    }

}