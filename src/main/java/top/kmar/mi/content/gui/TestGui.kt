package top.kmar.mi.content.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.items.ItemStackHandler
import top.kmar.mi.ModernIndustry
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.components.*
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes.Companion.valueOfID
import top.kmar.mi.api.graphics.utils.DisplayModeEnum
import top.kmar.mi.api.graphics.utils.FixedSizeMode
import top.kmar.mi.api.graphics.utils.VerticalAlignModeEnum
import top.kmar.mi.api.utils.applyClient
import top.kmar.mi.api.utils.data.enums.Direction2DEnum

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
        val progress = ProgressBarCmpt(valueOfID("progress")).applyClient {
            with(client.style) {
                width = FixedSizeMode(20)
                height = FixedSizeMode(15)
                progress.direction = Direction2DEnum.RIGHT
            }
        }
        progress.maxProgress = 100
        val button = ButtonCmpt(valueOfID("button")).applyClient {
            with(client.style) {
                width = FixedSizeMode(20)
                height = FixedSizeMode(15)
            }
        }
        val title = TextCmpt(valueOfID("title").applyClient { this["value"] = "水泵" }).applyClient {
            client.style.marginTop = 10
        }
        val output = SlotOutputCmpt(valueOfID("output")).apply {
            inventory = ItemStackHandler(1).apply {
                insertItem(0, ItemStack(Items.BED), false)
            }
        }
        val burn = BurnCmpt(valueOfID("burn")).apply {
            this.maxProcess = 100
        }
        background.addChild(title)
        slots.forEach { background.addChild(it) }
        background.addChild(backpack)
        background.addChild(progress)
        background.addChild(button)
        background.addChild(output)
        background.addChild(burn)
        mask.addChild(background)
        addChild(mask)
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
        val progress = document.queryCmpt("#progress") as ProgressBarCmpt
        if (progress.progress++ == progress.maxProgress) progress.progress = 0
        val burn = document.queryCmpt("#burn") as BurnCmpt
        if (burn.progress++ == burn.maxProcess) burn.progress = 0
    }

    companion object {

        val key = ResourceLocation(ModernIndustry.MODID, "test")

    }

}