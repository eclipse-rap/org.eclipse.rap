/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


public class MeasurementItem_Test extends TestCase {
  
  public void testMeasurementItemCreation() {
    String textToMeasure = "textToMeasure";
    FontData fontData = new FontData( "arial", 12, SWT.BOLD );
    int wrapWidth = 13;
    MeasurementItem item = new MeasurementItem( textToMeasure, fontData, wrapWidth );
    
    assertSame( fontData, item.getFontData() );
    assertSame( textToMeasure, item.getTextToMeasure() );
    assertEquals( wrapWidth, item.getWrapWidth() );
  }
}
