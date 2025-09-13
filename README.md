# Hide Body Parts

Utility mod for Fabric Minecraft.

## Invisible Body Parts

Use the `cardinal_components.hide-body-parts:hidden_parts` NBT compound list on a player entity to hide their body parts.

Currently available Body Parts:

| Body Part | name |
|---|---|
| HEAD | "head" |
| BODY | "body" |
| RIGHT ARM | "r_arm" |
| LEFT ARM | "l_arm" |
| RIGHT LEG | "r_leg" |
| LEFT LEG | "l_leg" |

### Commands

Toggle body parts: `/hide bodyPart changeEntry bodyPartName target`

Reset all body parts to visible: `/hide bodyPart removeAllEntries target`

Still need an example of how the NBT is structured in-game? Use the previously mentioned commands to disable a body part and use minecraft's
`/data get entity @p cardinal_components.hide-body-parts:hidden_parts` command to inspect the listed nbt values of the disabled body parts.

## Invisible Armor

Prevent armor rendering with the new

- Invisible Armor Enchantment
- `invisible_armor` Item Tag
- custom Boolean NBT value on the ItemStack, called `InvisibleArmor`

Note, that this is only a very basic implementation. External Mods, which don't make use of Minecraft's Armor Rendering,
might not work in this case.

<div style="text-align: center;">
<br>
<a href="https://fabricmc.net/"><img
    src="https://raw.githubusercontent.com/fabricated-atelier/.github/a021bde84febcb68adc69fc7ae60114e8c0902db/assets/badges/bc25/supported_on_fabric_loader.svg"
    alt="Supported on Fabric"
    width="200"
></a>
<a href="https://modfest.net/bc25">
<img src="https://raw.githubusercontent.com/fabricated-atelier/.github/f026478715176aeb6a334f1c21765031d9b6c3f9/assets/badges/bc25/featured_in_bc25.svg"
    alt="BlanketCon25"
    width="200"
/></a>
<a href="https://github.com/JR1811/NeMuelch-hide-body-parts/issues"><img
    src="https://raw.githubusercontent.com/fabricated-atelier/.github/f026478715176aeb6a334f1c21765031d9b6c3f9/assets/badges/bc25/work_in_progress.svg"
    alt="Work in Progress"
    width="200"
></a>
</div>
