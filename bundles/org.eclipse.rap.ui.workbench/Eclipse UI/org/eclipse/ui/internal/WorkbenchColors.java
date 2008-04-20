/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * This class manages the common workbench colors.  
 */
public class WorkbenchColors {
    static private boolean init = false;

    static private Color[] workbenchColors;

    /**
     * Dispose all color pre-allocated by the workbench.
     */
    private static void disposeWorkbenchColors() {
        for (int i = 0; i < workbenchColors.length; i++) {
// RAP [rh] Color#dispose() missing          
//            workbenchColors[i].dispose();
        }
        workbenchColors = null;
    }

    /**
     * Initialize all colors used in the workbench in case the OS is using
     * a 256 color palette making sure the workbench colors are allocated.
     *
     * This list comes from the designers.
     */
    private static void initWorkbenchColors(Display d) {
        if (workbenchColors != null) {
			return;
		}

// RAP [rh] Color constructor missing        
        workbenchColors = new Color[] {
        //Product pallet
                Graphics.getColor(255, 255, 255), Graphics.getColor(255, 251, 240),
                Graphics.getColor(223, 223, 191), Graphics.getColor(223, 191, 191),
                Graphics.getColor(192, 220, 192), Graphics.getColor(192, 192, 192),
                Graphics.getColor(191, 191, 191), Graphics.getColor(191, 191, 159),
                Graphics.getColor(191, 159, 191), Graphics.getColor(160, 160, 164),
                Graphics.getColor(159, 159, 191), Graphics.getColor(159, 159, 159),
                Graphics.getColor(159, 159, 127), Graphics.getColor(159, 127, 159),
                Graphics.getColor(159, 127, 127), Graphics.getColor(128, 128, 128),
                Graphics.getColor(127, 159, 159), Graphics.getColor(127, 159, 127),
                Graphics.getColor(127, 127, 159), Graphics.getColor(127, 127, 127),
                Graphics.getColor(127, 127, 95), Graphics.getColor(127, 95, 127),
                Graphics.getColor(127, 95, 95), Graphics.getColor(95, 127, 127),
                Graphics.getColor(95, 127, 95), Graphics.getColor(95, 95, 127),
                Graphics.getColor(95, 95, 95), Graphics.getColor(95, 95, 63),
                Graphics.getColor(95, 63, 95), Graphics.getColor(95, 63, 63),
                Graphics.getColor(63, 95, 95), Graphics.getColor(63, 95, 63),
                Graphics.getColor(63, 63, 95), Graphics.getColor(0, 0, 0),
                //wizban pallet
                Graphics.getColor(195, 204, 224), Graphics.getColor(214, 221, 235),
                Graphics.getColor(149, 168, 199), Graphics.getColor(128, 148, 178),
                Graphics.getColor(106, 128, 158), Graphics.getColor(255, 255, 255),
                Graphics.getColor(0, 0, 0), Graphics.getColor(0, 0, 0),
                //Perspective 
                Graphics.getColor(132, 130, 132), Graphics.getColor(143, 141, 138),
                Graphics.getColor(171, 168, 165),
                //PreferenceDialog and TitleAreaDialog
                Graphics.getColor(230, 226, 221) };
    }

    /**
     * Disposes of the colors. Ignore all
     * system colors as they do not need
     * to be disposed.
     */
    static public void shutdown() {
        if (!init) {
			return;
		}
        disposeWorkbenchColors();
        init = false;
    }

    /**
     * Initializes the colors.
     */
    static public void startup() {
        if (init) {
			return;
		}

        // Initialize the caches first.
        init = true;

        Display display = Display.getDefault();
        initWorkbenchColors(display);
    }

}
