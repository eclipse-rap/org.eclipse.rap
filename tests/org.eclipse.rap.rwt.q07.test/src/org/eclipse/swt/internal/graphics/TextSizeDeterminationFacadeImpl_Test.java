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
package org.eclipse.swt.internal.graphics;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.internal.graphics.TextSizeProbeStore.IProbe;


public class TextSizeDeterminationFacadeImpl_Test extends TestCase {

  private static class TestProbe implements IProbe {
    String text;
    FontData fontData;
    public FontData getFontData() {
      return fontData;
    }
    public String getText() {
      return text;
    }
  }

  private FontData fontData;

  public void testCreateProbeParamFragment() {
    TestProbe probe = new TestProbe();
    probe.text = "text";
    probe.fontData = fontData;
    String expected = "[ -1586239415, \"text\", [ \"font-name\" ], 1, false, false ]";
    String actual = TextSizeDeterminationFacadeImpl.createProbeParamFragment( probe );
    assertEquals( expected, actual );
  }

  protected void setUp() throws Exception {
    fontData = new FontData( "font-name", 1, SWT.NORMAL );
  }
}
