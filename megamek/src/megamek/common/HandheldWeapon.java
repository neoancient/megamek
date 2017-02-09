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
package megamek.common;

import megamek.common.verifier.TestEntity;

/**
 * Weapons that can be picked up and used by Meks with working hand actuators. Rules are found
 * in TacOps p. 314 but the most recent rules are found in TacOps errata 3.5, pp. 23-24.
 * 
 * @author Neoancient
 *
 */

public class HandheldWeapon extends GunEmplacement {
	
	private static final long serialVersionUID = -8857292801028543020L;

    private static int[] CRITICAL_SLOTS = new int[] { 6 };
    private int heatSinks = 0;
	
	@Override
	public boolean isTurret() {
		return false;
	}
	
    @Override
    public int[] getNoOfSlots() {
        return CRITICAL_SLOTS;
    }
    
    /*
     * Heat sinks are required by energy weapons and count toward weight and cost.
     */
    public int getNumHeatSinks() {
    	return heatSinks;
    }
    
    public void setNumHeatSinks(int hs) {
    	heatSinks = hs;
    }

    /* We only want a critical slot for weapons, so we add to LOC_NONE first then move it
     * and add a critical slot if necessary.
     */
    public void addEquipment(Mounted mounted, int loc, boolean rearMounted)
            throws LocationFullException {
    	boolean addCrit = mounted.getType() instanceof WeaponType
    			|| (mounted.getType() instanceof MiscType
    					&& mounted.getType().hasFlag(MiscType.F_VEHICLE_MINE_DISPENSER));
    	if (addCrit && getEmptyCriticals(LOC_GUNS) <= 0) {
			throw new LocationFullException("Handheld weapon has a maximum of "
					+ getNoOfSlots()[LOC_GUNS] + " slots.");
    	}
    	super.addEquipment(mounted, LOC_NONE, rearMounted);
    	if (!(mounted.getType() instanceof MiscType
    			&& mounted.getType().hasFlag(MiscType.F_HEAT_SINK))) {
    		mounted.setLocation(LOC_GUNS);
    	}
    	if (addCrit) {
    		addCritical(loc, new CriticalSlot(mounted));
    	}
    }

    @Override
    public boolean isLocationProhibited(Coords c, int currElevation) {
        IHex hex = game.getBoard().getHex(c);

        if(hex.containsTerrain(Terrains.SPACE) && doomedInSpace()) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public double getWeight() {
    	double tons = 0;
    	for (Mounted m : getEquipment()) {
    		if (m.getType() instanceof AmmoType) {
    			tons += ((double)m.getBaseShotsLeft()) / ((AmmoType)m.getType()).getShots();
    		} else {
    			tons += m.getType().getTonnage(this);
    		}
    	}
    	tons += getTotalOArmor() / 16.0;
    	tons += heatSinks;
    	return TestEntity.ceil(tons, TestEntity.Ceil.HALFTON);
    }
    
    @Override
    public double getCost(boolean ignoreAmmo) {
    	double cost = 0;
    	for (Mounted m : getEquipment()) {
    		if (m.getType() instanceof AmmoType) {
    			if (!ignoreAmmo) {
    				cost += m.getType().getCost(this, false, m.getLocation())
    						* m.getBaseShotsLeft() / ((AmmoType)m.getType()).getShots();
    			}
    		} else {
    			cost += m.getType().getCost(this, false, m.getLocation());
    		}
    	}
    	cost += EquipmentType.get("Heat Sink").getCost(this, false, LOC_NONE) * heatSinks;
    	return cost * 2;
    }
    
    @Override
    public int calculateBattleValue() {
    	double bv = 0;
    	for (Mounted m : getEquipment()) {
    		if (!m.isDestroyed()) {
    			if (m.getType() instanceof WeaponType) {
    				double wBV = m.getType().getBV(this);
    				Mounted linker = m.getLinkedBy();
    				if (linker != null && linker.getType() instanceof MiscType) {
    					if (linker.getType().hasFlag(MiscType.F_ARTEMIS)) {
    						wBV *= 1.2;
    					}
    					if (linker.getType().hasFlag(MiscType.F_ARTEMIS_V)) {
    						wBV *= 1.3;
    					}
    					if (linker.getType().hasFlag(MiscType.F_APOLLO)) {
    						wBV *= 1.15;
    					}
    					if (linker.getType().hasFlag(MiscType.F_RISC_LASER_PULSE_MODULE)) {
    						wBV *= 1.25;
    					}
    				}
    				bv += wBV;
    			} else if (m.getType() instanceof AmmoType) {
    				bv += m.getType().getBV(this) * m.getBaseShotsLeft()
    						/ ((AmmoType)m.getType()).getShots();
    			} else if (m.getType() instanceof MiscType
    					&& m.getType().hasFlag(MiscType.F_VEHICLE_MINE_DISPENSER)) {
    				bv += m.getType().getBV(this);
    			}
    		}
    	}
    	bv += getTotalOArmor() * 2;
    	return (int)Math.round(bv);
    }
	
    @Override
    public long getEntityType(){
        return Entity.ETYPE_TANK | Entity.ETYPE_GUN_EMPLACEMENT | Entity.ETYPE_HANDHELD_WEAPON;
    }
    
}
