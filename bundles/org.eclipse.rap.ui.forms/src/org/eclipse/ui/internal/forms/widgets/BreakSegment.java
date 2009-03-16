/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.forms.widgets;

import java.util.Hashtable;

//RAP [if] GC/FontMetrics not supported
//import org.eclipse.swt.graphics.FontMetrics;
//import org.eclipse.swt.graphics.GC;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;

/**
 * This segment serves as break within a paragraph. It has no data -
 * just starts a new line and resets the locator.
 */

public class BreakSegment extends ParagraphSegment {
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.internal.widgets.ParagraphSegment#advanceLocator(org.eclipse.swt.graphics.GC, int, org.eclipse.ui.forms.internal.widgets.Locator, java.util.Hashtable)
	 */
// RAP [if] changed method signature and implementation to cope with missing GC
//	public boolean advanceLocator(GC gc, int wHint, Locator locator,
  public boolean advanceLocator(Font font, int wHint, Locator locator,
			Hashtable objectTable, boolean computeHeightOnly) {
		if (locator.rowHeight==0) {
//			FontMetrics fm = gc.getFontMetrics();
//			locator.rowHeight = fm.getHeight();
		    locator.rowHeight = Graphics.getCharHeight( font );
		}
		if (computeHeightOnly) locator.collectHeights();
		locator.x = locator.indent;
		locator.y += locator.rowHeight;
		locator.rowHeight = 0;
		locator.leading = 0;
		return true;
	}

//RAP [if] paint unnecessary
//	public void paint(GC gc, boolean hover, Hashtable resourceTable, boolean selected, SelectionData selData, Rectangle repaintRegion) {
//		//nothing to paint
//	}
	public boolean contains(int x, int y) {
		return false;
	}
	public boolean intersects(Rectangle rect) {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.forms.widgets.ParagraphSegment#layout(org.eclipse.swt.graphics.GC, int, org.eclipse.ui.internal.forms.widgets.Locator, java.util.Hashtable, boolean, org.eclipse.ui.internal.forms.widgets.SelectionData)
	 */
// RAP [if] changed method signature and implementation to cope with missing GC
//	public void layout(GC gc, int width, Locator locator, Hashtable ResourceTable,
	public void layout(Font font, int width, Locator locator, Hashtable ResourceTable,
			boolean selected) {
		locator.resetCaret();
		if (locator.rowHeight==0) {
//			FontMetrics fm = gc.getFontMetrics();
//			locator.rowHeight = fm.getHeight();
		    locator.rowHeight = Graphics.getCharHeight( font );
		}
		locator.y += locator.rowHeight;
		locator.rowHeight = 0;
		locator.rowCounter++;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.forms.widgets.ParagraphSegment#computeSelection(org.eclipse.swt.graphics.GC, java.util.Hashtable, boolean, org.eclipse.ui.internal.forms.widgets.SelectionData)
	 */
// RAP [if] changed method signature and implementation to cope with missing GC
//	public void computeSelection(GC gc, Hashtable resourceTable, SelectionData selData) {
	public void computeSelection(Font font, Hashtable resourceTable, SelectionData selData) {
		selData.markNewLine();
	}
}
