package site.siredvin.digitalitems.testmod

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.broccolium.modules.base.block.GenericBlockEntityBlock
import site.siredvin.broccolium.modules.base.util.BlockUtil
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import site.siredvin.digitalitems.modId
import site.siredvin.digitalitems.xplat.ModPlatform

object DigitalItemsTestContent {
    val BLOCK: RegistryEntry<Block> = ModPlatform.registerBlock(
        "test_storage",
        { GenericBlockEntityBlock<TestStorageBlockEntity>({ BLOCK_ENTITY }, false, properties = BlockUtil.defaultProperties().setId(ResourceKey.create(Registries.BLOCK, modId("test_storage")))) },
    )
    val BLOCK_ENTITY: RegistryEntry<BlockEntityType<TestStorageBlockEntity>> = ModPlatform.registerBlockEntity(
        "test_storage",
    ) { PlatformToolkit.get().createBlockEntityType(::TestStorageBlockEntity, BLOCK.get()) }

    fun register() = Unit
}
