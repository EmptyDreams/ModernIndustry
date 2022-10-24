package top.kmar.mi.coremod.mixin

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.GuiScreen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import top.kmar.mi.api.utils.closeClientGui

/**
 *
 * @author EmptyDreams
 */
@Mixin(Minecraft::class)
abstract class MixinMinecraft {

    @field:Shadow
    var player: EntityPlayerSP? = null

    @Inject(
        method = ["displayGuiScreen"],
        at = [At(value = "HEAD")]
    )
    fun displayGuiScreen(guiScreenIn: GuiScreen?, ci: CallbackInfo) {
        player?.closeClientGui()
    }

}