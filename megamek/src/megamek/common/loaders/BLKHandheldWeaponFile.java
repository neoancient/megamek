/*
 * MegaMek - Copyright (C) 2017 The MegaMek Team
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package megamek.common.loaders;

import megamek.common.AmmoType;
import megamek.common.Entity;
import megamek.common.EquipmentType;
import megamek.common.GunEmplacement;
import megamek.common.HandheldWeapon;
import megamek.common.LocationFullException;
import megamek.common.Mounted;
import megamek.common.util.BuildingBlock;

/**
*
* @author Neoancient
* 
*/

public class BLKHandheldWeaponFile extends BLKFile implements IMechLoader {

    public BLKHandheldWeaponFile(BuildingBlock bb) {
        dataFile = bb;
    }

    public Entity getEntity() throws EntityLoadingException {

        GunEmplacement e = new HandheldWeapon();

        if (!dataFile.exists("Name")) {
            throw new EntityLoadingException("Could not find name block.");
        }
        e.setChassis(dataFile.getDataAsString("Name")[0]);

        if (dataFile.exists("Model") && (dataFile.getDataAsString("Model")[0] != null)) {
            e.setModel(dataFile.getDataAsString("Model")[0]);
        } else {
            e.setModel("");
        }

        setTechLevel(e);
        setFluff(e);
        checkManualBV(e);

        if (dataFile.exists("source")) {
            e.setSource(dataFile.getDataAsString("source")[0]);
        }

        if (dataFile.exists("Turret")) {
            if (dataFile.getDataAsInt("Turret")[0] != 1) {
                e.setHasNoTurret(true);
            }
        }

        if (!dataFile.exists("armor")) {
            throw new EntityLoadingException("Could not find armor block.");
        }

        int[] armor = dataFile.getDataAsInt("armor");

        if (armor.length != 1) {
            throw new EntityLoadingException("Incorrect armor array length");
        }

        // add the body to the armor array
        e.refreshLocations();
        e.initializeArmor(armor[0], HandheldWeapon.LOC_GUNS);

        e.autoSetInternal();
        
        if (dataFile.exists("armor_type")){
            e.setArmorType(dataFile.getDataAsInt("armor_type")[0]);
        }
        
        if (dataFile.exists("armor_tech")) {
            e.setArmorTechLevel(dataFile.getDataAsInt("armor_tech")[0]);
        }

        loadEquipment(e, "Guns", GunEmplacement.LOC_GUNS);
        e.setArmorTonnage(e.getArmorWeight());
        return e;
    }

    @Override
    protected void loadEquipment(Entity t, String sName, int nLoc) throws EntityLoadingException {
        String[] saEquip = dataFile.getDataAsString(sName + " Equipment");
        if (saEquip == null) {
            return;
        }

        if (saEquip[0] != null) {
            for (int x = 0; x < saEquip.length; x++) {
                int numShots = 0;
                if (saEquip[x].contains(":Shots")){
                    String shotString = saEquip[x].substring(
                            saEquip[x].indexOf(":Shots"),
                            saEquip[x].indexOf("#")+1);
                    numShots = Integer.parseInt(
                            shotString.replace(":Shots", "").replace("#", ""));
                    saEquip[x] = saEquip[x].replace(shotString, "");
                }
                
                String equipName = saEquip[x].trim();
                EquipmentType etype = EquipmentType.get(equipName);

                if (etype != null) {
                    try {
                        Mounted m = t.addEquipment(etype, HandheldWeapon.LOC_GUNS, false);
                        if (numShots != 0 && m != null 
                                && (m.getType() instanceof AmmoType)){
                            m.setShotsLeft(numShots);
                            m.setOriginalShots(numShots);
                        }
                    } catch (LocationFullException ex) {
                        throw new EntityLoadingException(ex.getMessage());
                    }
                } else if (!equipName.equals("")) {
                    t.addFailedEquipment(equipName);
                }
            }
        }
    }
}
