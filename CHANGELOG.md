# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]
- Planned changes.


## v1.0.1 - 2025-10-11
- Add item translation keys for Solar Panel (en,tr)\n- Remove redundant item_group_en_us.json


## v1.1.0 - 2025-10-11
- Convert Solar Panel to SlabBlock\n- Add slab models and blockstate (bottom/top/double)\n- Adjust loot to drop 2 when double slab\n- Update item model and recipe


## v1.1.1 - 2025-10-11
- New top texture for Solar Panel slabs\n- Update slab and double slab models to use it


## v1.2.0 - 2025-10-12
- Cables: full 6-direction connect + visual arms\n- Cables: internal buffer for chaining, auto-recompute connections\n- KE Lamp: max brightness and 5x persistence\n- Battery: side texture and model update\n- Remove all GUIs


## v1.2.1 - 2025-10-12
- Persist KE for Battery and Solar Panel
- Cable buffer & 6-direction connections
- KE Lamp visuals and brightness


## v1.2.2 - 2025-10-12
- Added BlockApiLookup for KE (abekons-sun-panels:ke)\n- Cables now find neighbors via API (no instanceof)\n- Registered for battery, panel, cable, lamp


## v1.2.3 - 2025-10-12
- Add block tag: babekons-sun-panels:ke_connectable
- Cable connects if KE API or tag matches
- Data pack friendly configuration


## v1.2.4 - 2025-10-12
- Add WATERLOGGED state and fluid visuals\n- Thinner center/arms (4px) for hitbox/outline\n- Connect via KE API or ke_connectable tag

