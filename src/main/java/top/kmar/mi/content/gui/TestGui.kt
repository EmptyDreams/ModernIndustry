package top.kmar.mi.content.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.items.ItemStackHandler
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.GuiLoader
import top.kmar.mi.api.graphics.components.*
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes.Companion.valueOfID
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.utils.DisplayModeEnum
import top.kmar.mi.api.graphics.utils.FixedSizeMode
import top.kmar.mi.api.graphics.utils.VerticalAlignModeEnum
import top.kmar.mi.api.utils.MISysInfo
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.applyClient

/**
 *
 * @author EmptyDreams
 */
@EventBusSubscriber
class TestGui : BaseGraphics() {

    override fun init(player: EntityPlayer, pos: BlockPos) {
        super.init(player, pos)
        val mask = MaskCmpt(valueOfID("mask"))
        val background = BackgroundCmpt(valueOfID("background")).applyClient {
            with(client.style) {
                width = FixedSizeMode(200)
                height = FixedSizeMode(230)
                alignVertical = VerticalAlignModeEnum.TOP
            }
        }
        val slots = Array(5) {
            SlotCmpt(valueOfID("slot-$it")).apply {
                inventory = ItemStackHandler(1).apply {
                    insertItem(0, ItemStack(Items.APPLE, it + 1), false)
                }
            }.applyClient {
                client.style.marginTop = 20
                client.style.display = DisplayModeEnum.INLINE
            }
        }
        val backpack = BackpackCmpt(valueOfID("backpack")).apply {
            this.player = player
        }
        val button = ButtonCmpt(valueOfID("button")).applyClient {
            with(client.style) {
                width = FixedSizeMode(20)
                height = FixedSizeMode(15)
            }
        }
        button.addEventListener(IGraphicsListener.mouseClick) {
            it?.send2Service = true
            MISysInfo.print(WorldUtil.isServer())
        }
        slots.forEach { background.addChild(it) }
        background.addChild(backpack)
        background.addChild(button)
        mask.addChild(background)
        addChild(mask)
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
    }

    companion object {

        @JvmStatic
        @SubscribeEvent
        fun registry(event: GuiLoader.MIGuiRegistryEvent) {
            event.registry(TestGui::class.java)
        }

    }

}