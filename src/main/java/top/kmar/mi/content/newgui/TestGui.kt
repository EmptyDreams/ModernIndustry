package top.kmar.mi.content.newgui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.items.ItemStackHandler
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.GuiLoader
import top.kmar.mi.api.graphics.components.BackgroundCmpt
import top.kmar.mi.api.graphics.components.MaskCmpt
import top.kmar.mi.api.graphics.components.SlotCmpt
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.utils.DisplayModeEnum
import top.kmar.mi.api.graphics.utils.FixedSizeMode
import top.kmar.mi.api.utils.applyClient
import java.util.*

/**
 *
 * @author EmptyDreams
 */
@EventBusSubscriber
class TestGui : BaseGraphics() {

    override fun init(player: EntityPlayer, pos: BlockPos) {
        val mask = MaskCmpt("mask")
        val background = BackgroundCmpt("background").applyClient {
            with(client.style) {
                width = FixedSizeMode(200)
                height = FixedSizeMode(230)
            }
        }
        val slots = Array(5) {
            SlotCmpt("slot").apply {
                inventory = ItemStackHandler(1).apply {
                    insertItem(0, ItemStack(Items.APPLE, it + 1), false)
                }
                index = 0
            }.applyClient {
                client.style.display = DisplayModeEnum.INLINE
            }
        }
        slots.forEach { background.addChild(it) }
        mask.addChild(background)
        addChild(mask)

        val list = LinkedList<Cmpt>()
        list.add(document)
        do {
            val parent = list.pop()
            parent.eachAllChildren {
                if (!it.isInstallParent) {
                    it.isInstallParent = true
                    it.installParent(parent)
                }
                list.add(it)
            }
        } while (list.isNotEmpty())
    }

    companion object {

        @JvmStatic
        @SubscribeEvent
        fun registry(event: GuiLoader.MIGuiRegistryEvent) {
            event.registry(TestGui::class.java)
        }

    }

}