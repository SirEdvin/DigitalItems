package site.siredvin.digitalitems.data

import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import site.siredvin.digitalitems.modId
import java.util.concurrent.CompletableFuture

class ModTurtleUpgradeModelProvider(output: PackOutput) : DataProvider {
    private val paths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "computercraft/turtle_upgrade")

    override fun getName(): String = "DigitalItems turtle upgrade models"

    override fun run(output: CachedOutput): CompletableFuture<*> = CompletableFuture.allOf(
        *listOf("digitizer", "advanced_digitizer").map { name ->
            val model = JsonObject().apply {
                addProperty("type", "computercraft:sided")
                addProperty("left", "digitalitems:turtle/${name}_left")
                addProperty("right", "digitalitems:turtle/${name}_right")
            }
            DataProvider.saveStable(output, model, paths.json(modId(name)))
        }.toTypedArray(),
    )
}
