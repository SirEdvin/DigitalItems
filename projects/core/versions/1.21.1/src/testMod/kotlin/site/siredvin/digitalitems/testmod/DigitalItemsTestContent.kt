package site.siredvin.digitalitems.testmod

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.broccolium.modules.base.block.GenericBlockEntityBlock
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import site.siredvin.digitalitems.xplat.ModPlatform

object DigitalItemsTestContent {
    val BLOCK: RegistryEntry<Block> = ModPlatform.registerBlock(
        "test_storage",
        { GenericBlockEntityBlock<TestStorageBlockEntity>({ BLOCK_ENTITY }, false) },
    )
    val BLOCK_ENTITY: RegistryEntry<BlockEntityType<TestStorageBlockEntity>> = ModPlatform.registerBlockEntity(
        "test_storage",
    ) { PlatformToolkit.get().createBlockEntityType(::TestStorageBlockEntity, BLOCK.get()) }

    fun register() = Unit
}
