/**
 * MegaMek - Copyright (C) 2005 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */
package megamek.common.weapons;

import megamek.common.AmmoType;
import megamek.common.TechConstants;

/**
 * @author Sebastian Brocks
 */
public class CLImprovedSRM2 extends SRMWeapon {

    /**
     *
     */
    private static final long serialVersionUID = -8486208221700793591L;

    /**
     *
     */
    public CLImprovedSRM2() {
        super();
 
        name = "Improved SRM 2";
        setInternalName(name);
        addLookupName("CLImprovedSRM2");
        heat = 2;
        rackSize = 2;
        shortRange = 4;
        mediumRange = 8;
        longRange = 12;
        extremeRange = 16;
        tonnage = 1.0f;
        criticals = 1;
        bv = 28;
        flags = flags.or(F_NO_FIRES);
        ammoType = AmmoType.T_SRM_IMP;
        cost = 10000;
        shortAV = 2;
        maxRange = RANGE_SHORT;
        introDate = 2815;
        extinctDate = 2828;
    	reintroDate =  3080;
    	techLevel.put(2815, TechConstants.T_IS_EXPERIMENTAL);   ///EXP
        techLevel.put(2817, TechConstants.T_IS_ADVANCED);   ///ADV
        techLevel.put(2819, TechConstants.T_ALLOWED_ALL);   ///COMMON
        availRating = new int[] { RATING_X, RATING_D, RATING_X, RATING_X };
        techRating = RATING_D;
        rulesRefs = "96, IO";
        
    }
}