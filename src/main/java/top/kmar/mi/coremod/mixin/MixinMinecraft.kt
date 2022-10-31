package top.kmar.mi.coremod.mixin

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.GuiScreen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import top.kmar.mi.api.utils.expands.closeClientGui

/**
 * 提供客户端GUI支持
 * @author EmptyDreams
 */
@Mixin(Minecraft::class)
abstract class MixinMinecraft {

    @field:Shadow
    var player: EntityPlayerSP? = null

    /**
     * 在玩家打开/关闭GUI时先对客户端GUI进行操作，如果关闭了一个客户端GUI则不关闭原本打开的GUI
     */
    @Inject(
        method = ["displayGuiScreen"],
        at = [At(value = "HEAD")],
        cancellable = true
    )
    fun displayGuiScreen(guiScreenIn: GuiScreen?, ci: CallbackInfo) {
        player?.apply {
            if (closeClientGui() && guiScreenIn == null && health > 0) {
                ci.cancel()
            }
        }
    }

}