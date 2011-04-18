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

import org.eclipse.rwt.internal.textsize.TextSizeDeterminationFacadeImpl;
import org.eclipse.rwt.internal.textsize.TextSizeProbeStore.Probe;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


public class TextSizeDeterminationFacadeImpl_Test extends TestCase {


  public void testCreateProbeParamFragment() {
    FontData fontData = new FontData( "font-name", 1, SWT.NORMAL );
    Probe probe = new Probe( "text", fontData );
    String expected = "[ -1586239415, \"text\", [ \"font-name\" ], 1, false, false ]";
    String actual = TextSizeDeterminationFacadeImpl.createProbeParamFragment( probe );
    assertEquals( expected, actual );
  }
}
