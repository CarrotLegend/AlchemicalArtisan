
package com.carrot123.alchemicalartisan.registry;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.menu.AlloyFurnaceMenu;
import com.carrot123.alchemicalartisan.menu.BlazingFurnaceMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(
                    ForgeRegistries.MENU_TYPES,
                    AlchemicalArtisan.MOD_ID
            );

    public static final RegistryObject<MenuType<AlloyFurnaceMenu>> ALLOY_FURNACE =
            MENUS.register("alloy_furnace",
                    () -> IForgeMenuType.create(AlloyFurnaceMenu::new));

    public static final RegistryObject<MenuType<BlazingFurnaceMenu>> BLAZING_FURNACE =
            MENUS.register("blazing_furnace",
                    () -> IForgeMenuType.create(BlazingFurnaceMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
