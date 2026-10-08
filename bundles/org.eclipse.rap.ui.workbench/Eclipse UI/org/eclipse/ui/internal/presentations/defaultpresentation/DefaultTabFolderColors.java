/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.presentations.defaultpresentation;

import org.eclipse.swt.graphics.Color;

/**
 */
public final class DefaultTabFolderColors {
    Color foreground;
    int[] percentages;
    Color[] background;
    boolean vertical;
    
    public DefaultTabFolderColors() {
        
    }
    
    public DefaultTabFolderColors(Color fgColor, Color[] bgColors,
            int[] percentages, boolean vertical) {
        
        foreground = fgColor;
        background = bgColors;
        this.percentages = percentages;
        this.vertical = vertical;
    }
    
    public DefaultTabFolderColors setForeground(Color fg) {
        foreground = fg;
        return this;
    }
    
    public DefaultTabFolderColors setBackground(Color[] background, int[] percentages, boolean vertical) {
        this.background = background;
        this.percentages = percentages;
        this.vertical = vertical;
        return this;
    }
}
