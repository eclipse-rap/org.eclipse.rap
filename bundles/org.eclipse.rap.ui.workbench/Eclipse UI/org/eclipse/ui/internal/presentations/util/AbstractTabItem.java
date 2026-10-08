/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
package org.eclipse.ui.internal.presentations.util;

import org.eclipse.swt.graphics.Rectangle;

/**
 */
public abstract class AbstractTabItem {
    public abstract Rectangle getBounds();
    public abstract void setInfo(PartInfo info);
    public abstract void dispose();
    public void setBusy(boolean busy) {}
    public void setBold(boolean bold) {}
    
    public abstract Object getData();
    public abstract void setData(Object data);
    
    public boolean isShowing() {
        return true;
    }
    
}
