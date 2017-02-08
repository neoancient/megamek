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
	
	@Override
	public boolean isTurret() {
		return false;
	}
	
    @Override
    public int[] getNoOfSlots() {
        return CRITICAL_SLOTS;
    }

    /* We only want a critical slot for weapons, so we add to LOC_NONE first then move it
     * and add a critical slot if necessary.
     */
    public void addEquipment(Mounted mounted, int loc, boolean rearMounted)
            throws LocationFullException {
    	super.addEquipment(mounted, LOC_NONE, rearMounted);
    	mounted.setLocation(LOC_GUNS);
    	if (mounted.getType() instanceof WeaponType) {
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
    public long getEntityType(){
        return Entity.ETYPE_TANK | Entity.ETYPE_GUN_EMPLACEMENT | Entity.ETYPE_HANDHELD_WEAPON;
    }
    
}
