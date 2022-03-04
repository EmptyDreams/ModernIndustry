package top.kmar.mi.coremod.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FilenameUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.kmar.mi.api.craftguide.json.JsonCraftRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraftforge.common.crafting.CraftingHelper.findFiles;

/**
 * @author EmptyDreams
 */
@Mixin(CraftingHelper.class)
public class MixinCraftingHelper {
	
	@Shadow(remap = false)
	private static Gson GSON;
	
	/**
	 * @reason 为了实现MI注册表的自动注册
	 * @author EmptyDreams
	 */
	@Overwrite(remap = false)
	private static boolean loadRecipes(ModContainer mod) {
		JsonContext ctx = new JsonContext(mod.getModId());
		
		return findFiles(mod, "assets/" + mod.getModId() + "/recipes",
				root -> {
					Path fPath = root.resolve("_constants.json");
					if (Files.exists(fPath)) {
						try (BufferedReader reader = Files.newBufferedReader(fPath)) {
							JsonObject[] json = JsonUtils.fromJson(GSON, reader, JsonObject[].class);
							Method constants = ctx.getClass().getDeclaredMethod(
									"loadConstants", JsonObject[].class);
							constants.setAccessible(true);
							constants.invoke(ctx, (Object) json);
						} catch (IOException e) {
							FMLLog.log.error("Error loading _constants.json: ", e);
							return false;
						} catch (NoSuchMethodException |
								IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
					return true;
				},
				(root, file) -> {
					Loader.instance().setActiveModContainer(mod);
					
					String relative = root.relativize(file).toString();
					if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
						return true;
					
					String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
					ResourceLocation key = new ResourceLocation(ctx.getModId(), name);
					
					try (BufferedReader reader = Files.newBufferedReader(file)) {
						JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
						//noinspection ConstantConditions
						if (json.has("conditions") &&
								!CraftingHelper.processConditions(
										JsonUtils.getJsonArray(json, "conditions"), ctx))
							return true;
						/* 插入开始 */
						if (!JsonCraftRegistry.registryJson(json)) {
							IRecipe recipe = CraftingHelper.getRecipe(json, ctx);
							ForgeRegistries.RECIPES.register(recipe.setRegistryName(key));
						}
						/* 插入结束 */
					} catch (JsonParseException e) {
						FMLLog.log.error("Parsing error loading recipe {}", key, e);
						return false;
					} catch (IOException e) {
						FMLLog.log.error("Couldn't read recipe {} from {}", key, file, e);
						return false;
					}
					return true;
				},
				true, true
		);
	}
	
}