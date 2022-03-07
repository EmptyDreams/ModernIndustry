package top.kmar.mi.content.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import top.kmar.mi.ModernIndustry
import top.kmar.mi.api.event.GuiRegistryEvent
import top.kmar.mi.api.gui.client.StaticFrameClient
import top.kmar.mi.api.gui.common.IContainerCreater
import top.kmar.mi.api.gui.common.MIFrame
import top.kmar.mi.api.gui.component.group.Group
import top.kmar.mi.api.gui.component.group.Panels
import top.kmar.mi.api.gui.component.interfaces.IComponent
import top.kmar.mi.content.tileentity.user.EUFluidPump

/**
 * 水泵的GUI
 * @author EmptyDreams
 */
@EventBusSubscriber
class FluidPumpFrame {

    companion object {

        @JvmStatic
        val NAME = ResourceLocation(ModernIndustry.MODID, "fluid_pump")
        @JvmStatic
        val LOCATION_NAME = "tile.mi.fluid_pump.name"

        @JvmStatic
        @SubscribeEvent
        fun registry(event: GuiRegistryEvent) {
            event.registry(NAME, object : IContainerCreater {

                override fun createService(
                    world: World, player: EntityPlayer, pos: BlockPos
                ) = init(MIFrame(LOCATION_NAME, player), world, pos)

                override fun createClient(
                    world: World, player: EntityPlayer, pos: BlockPos
                ) = StaticFrameClient(createService(world, player, pos), LOCATION_NAME)

                fun init(frame: MIFrame, world: World, pos: BlockPos): MIFrame {
                    frame.setSize(150, 130)
                    val pump = world.getTileEntity(pos) as EUFluidPump
                    frame.init(world)

                    val align = Group(0, 0, frame.width, frame.height, Panels::horizontalCenter)
                    val group = Group(0, 0, 0, 0, Panels::verticalLeft)
                    group.minDistance = 10
                    group.adds(
                        createGroup(pump.guiEnergyText, pump.guiEnergy),
                        createGroup(pump.guiConsumeText, pump.guiConsume),
                        createGroup(pump.guiText, pump.guiFluid)
                    )
                    align.add(group)
                    return frame.add(align)
                }

                fun createGroup(text: IComponent, progress: IComponent): Group {
                    val result = Group(0, 0, 0, 0, Panels::verticalLeft)
                    result.adds(text, progress)
                    return result
                }

            })
        }

    }

}