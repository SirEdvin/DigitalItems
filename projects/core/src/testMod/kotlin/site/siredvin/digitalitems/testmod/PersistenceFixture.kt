package site.siredvin.digitalitems.testmod

import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.storage.LevelResource
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.digitalitems.common.data.ByteArrayWrapper
import site.siredvin.digitalitems.common.data.DigitalItemsSavedData
import site.siredvin.digitalitems.common.data.DigitizedEnergy
import site.siredvin.digitalitems.common.data.DigitizedFluid
import site.siredvin.digitalitems.common.data.DigitizedItem
import site.siredvin.digitalitems.common.data.DigitizedSomething
import java.nio.file.Files
import java.util.UUID

object PersistenceFixture {
    fun check(helper: GameTestHelper) {
        val level = helper.level.server.overworld()
        val data = DigitalItemsSavedData.getFrom(level)
        val ids = (1..3).map { ByteArrayWrapper("digitalitems-persistence-$it".toByteArray()) }
        val item = DigitizedItem(ids[0], PersistenceVersionFixture.namedStack(), level.gameTime, null)
        val fluid = DigitizedFluid(ids[1], AgnosticFluidStack(Fluids.WATER, 1234.0), level.gameTime, null)
        val energy = DigitizedEnergy(ids[2], AgnosticEnergyStack(Energies.FORGE, 45_678), level.gameTime, null)
        val entries = listOf(item, fluid, energy)
        entries.forEach {
            it.ownerUUID = UUID.fromString("00000000-0000-0000-0000-000000000123")
            it.refresh(level.gameTime + 7)
        }
        val copyDirectory = Files.createTempDirectory(level.server.getWorldPath(LevelResource.ROOT), "digitalitems-persistence-")
        try {
            data.add(item)
            data.add(fluid)
            data.add(energy)
            level.dataStorage.save()
            // Reload only a copy of this disposable test world's native save.
            Files.copy(level.server.getWorldPath(LevelResource.ROOT).resolve("data/digitalitems.dat"), copyDirectory.resolve("digitalitems.dat"))
            val restored = PersistenceVersionFixture.reload(copyDirectory.toFile(), helper)
            val restoredItem = requireNotNull(restored.get(ids[0]))
            assertEntry(item, restoredItem)
            helper.assertTrue(restoredItem.something.hoverName.string == item.something.hoverName.string, "Item name must survive native save/load")
            helper.assertTrue(restoredItem.something.damageValue == 17, "Item damage must survive native save/load")
            val restoredFluid = requireNotNull(restored.getFluid(ids[1]))
            assertEntry(fluid, restoredFluid)
            helper.assertTrue(restoredFluid.something.fluid == Fluids.WATER, "Fluid identity must survive native save/load")
            val restoredEnergy = requireNotNull(restored.getEnergy(ids[2]))
            assertEntry(energy, restoredEnergy)
            helper.assertTrue(restoredEnergy.something.unit == Energies.FORGE, "Energy unit must survive native save/load")
        } finally {
            data.pop(ids[0])
            data.popFluid(ids[1])
            data.popEnergy(ids[2])
            level.dataStorage.save()
            Files.deleteIfExists(copyDirectory.resolve("digitalitems.dat"))
            Files.deleteIfExists(copyDirectory)
        }
    }

    private fun assertEntry(expected: DigitizedSomething<*>, actual: DigitizedSomething<*>) {
        check(expected.id == actual.id && expected.amount == actual.amount) { "Digital identity and quantity must survive native save/load" }
        check(expected.ownerUUID == actual.ownerUUID && expected.digitizedAt == actual.digitizedAt) { "Owner and creation time must survive native save/load" }
        check(expected.lastRefresh == actual.lastRefresh && expected.decaysAt == actual.decaysAt) { "Decay timestamps must survive native save/load" }
    }
}
