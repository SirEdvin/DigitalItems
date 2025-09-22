package site.siredvin.digitalitems.client

import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.MenuType

abstract class BasicMenu<T : AbstractContainerMenu>(id: Int, @JvmField val data: ContainerData, menuType: MenuType<T>) : AbstractContainerMenu(menuType, id)
