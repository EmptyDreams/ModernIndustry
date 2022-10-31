/** 与玩家有关的操作的封装 */
package top.kmar.mi.api.utils.expands

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import top.kmar.mi.ModernIndustry
import top.kmar.mi.api.graphics.GuiLoader
import kotlin.math.abs
import kotlin.math.sqrt

@field:SideOnly(Side.CLIENT)
private var oldGui: GuiScreen? = null

@field:SideOnly(Side.CLIENT)
private var isOpenClientGui = false

/** 打开一个客户端GUI */
@Suppress("ControlFlowWithEmptyBody")
fun EntityPlayer.openClientGui(key: ResourceLocation, x: Int, y: Int, z: Int) {
    if (world.isServer()) return
    val id = GuiLoader.getID(key)
    if (id > 0) throw IllegalArgumentException("指定GUI[$key]不是客户端GUI")
    // 清除键盘和鼠标输入
    KeyBinding.unPressAllKeys()
    while (Mouse.next()) { }
    while (Keyboard.next()) { }
    // 打开GUI
    val mc = Minecraft.getMinecraft()
    if (isOpenClientGui()) mc.currentScreen!!.onGuiClosed()
    else oldGui = mc.currentScreen
    isOpenClientGui = true
    val newGui = GuiLoader.getClientGuiElement(id, this, world, x, y, z)
    val scaled = ScaledResolution(mc)
    val i = scaled.scaledWidth
    val j = scaled.scaledHeight
    newGui.setWorldAndResolution(mc, i, j)
    mc.currentScreen = newGui
    mc.setIngameNotInFocus()
}

/**
 * 关闭客户端GUI
 * @return 是否成功关闭客户端GUI
 */
fun EntityPlayer.closeClientGui(): Boolean {
    if (world.isServer()) return false
    if (isOpenClientGui) {
        isOpenClientGui = false
        val mc = Minecraft.getMinecraft()
        mc.currentScreen!!.onGuiClosed()
        mc.currentScreen = oldGui
        return true
    }
    return false
}

/** 判断指定玩家是否打开了一个客户端GUI */
@Suppress("UnusedReceiverParameter")
fun EntityPlayer.isOpenClientGui(): Boolean = isOpenClientGui

fun EntityPlayer.openGui(key: ResourceLocation, x: Int, y: Int, z: Int) {
    openGui(ModernIndustry.instance, GuiLoader.getID(key), world, x, y, z)
}

/**
 * 当玩家放置方块时判断方块的朝向
 * @param pos 放置的方块的坐标
 * @receiver [EntityPlayer]
 */
fun EntityPlayer.getPlacingDirection(pos: BlockPos): EnumFacing {
    val x: Double = posX - pos.x
    val y: Double = posZ - pos.z
    if (pos.y < posY || posY + height < pos.y) {
        return if (sqrt(x * x + y * y) <= 1.8) {
            //如果玩家和方块间的水平距离小于1.8
            if (pos.y < posY) EnumFacing.DOWN else EnumFacing.UP
        } else {
            //如果玩家和方块间的水平距离大于1.8
            horizontalFacing
        }
    } else if (pos.y == posY.toInt() || pos.y == posY.toInt() + 1) {
        //如果玩家和方块在同一水平面上
        if (sqrt(x * x + y * y) > 1.8 || abs(rotationPitch) < 40) {
            return horizontalFacing
        }
        if (rotationPitch < -8.3) return EnumFacing.UP
        if (rotationPitch > 8.3) return EnumFacing.DOWN
    }
    //如果玩家和方块大致处于同一平面
    return horizontalFacing
}