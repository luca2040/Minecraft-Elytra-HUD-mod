## Minecraft Mod Elytra HUD

### Adds the Elytra durability in the HUD, so you don't risk to fall while in the air.

![Modrinth Downloads](https://img.shields.io/modrinth/dt/elytra-hud-mod?style=for-the-badge&logo=Modrinth&link=https%3A%2F%2Fmodrinth.com%2Fmod%2Felytra-hud-mod)
<br/>
![CurseForge Downloads](https://img.shields.io/curseforge/dt/1093105?style=for-the-badge&logo=CurseForge&link=https%3A%2F%2Fwww.curseforge.com%2Fminecraft%2Fmc-mods%2Felytra-hud)

---

*This file only contains technical description about the mod, if you want to know what it does
check out [this mod on Modrinth](https://modrinth.com/mod/elytra-hud-mod).*

---

This mod is compatible with [Elytra Slot](https://modrinth.com/mod/elytra-slot), which actually uses
the [Trinkets API](https://github.com/emilyploszaj/trinkets), so to make the mod work both if `Trinkets API` is
installed or not its classes are loaded dynamically:

```java
static {
    boolean isPresent;
    try {
        // Load Trinkets API if present
        Class<?> trinketsApiClass = Class.forName("dev.emi.trinkets.api.TrinketsApi");
        Class<?> trinketComponentClass = Class.forName("dev.emi.trinkets.api.TrinketComponent");

        getTrinketComponentMethod = trinketsApiClass.getMethod("getTrinketComponent", LivingEntity.class);
        getEquippedMethod = trinketComponentClass.getMethod("getEquipped", Predicate.class);

        isPresent = true;
    } catch (ClassNotFoundException | NoSuchMethodException e) {
        isPresent = false;
    }
    IS_TRINKETS_API_PRESENT = isPresent;
}
```

And so also its functions need to be loaded dynamically:

```java
public ItemStack getTrinketsElytraItemStack(LivingEntity livingEntity) {
    try {
    Optional<?> trinketComponentOpt = (Optional<?>) getTrinketComponentMethod.invoke(null, livingEntity);

    if (trinketComponentOpt.isPresent()) {
        Object trinketComponent = trinketComponentOpt.get();
        List<?> equippedList = (List<?>) getEquippedMethod.invoke(trinketComponent, ELYTRA_CHECK);
    
        if (!equippedList.isEmpty()) {
            for (Object item : equippedList) {
                Pair<Object, ItemStack> pair = (Pair<Object, ItemStack>) item;
                ItemStack item_stack = pair.getRight();
            
                boolean isElytra = item_stack.getItem() instanceof net.minecraft.item.ElytraItem;
                if (isElytra)
                return item_stack;
            }
        }
    }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return ItemStack.EMPTY;
}
```

Because of this function the mod ***should*** work with every mod that uses the `Trinkets API`.

---

### You can download this mod on:

[Modrinth](https://modrinth.com/mod/elytra-hud-mod) <br/>
[Curseforge](https://www.curseforge.com/minecraft/mc-mods/elytra-hud)
