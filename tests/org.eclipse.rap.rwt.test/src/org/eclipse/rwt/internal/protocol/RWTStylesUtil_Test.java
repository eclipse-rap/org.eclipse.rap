/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import static org.eclipse.rwt.internal.resources.TestUtil.assertArrayEquals;
import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class RWTStylesUtil_Test extends TestCase {

  private Shell shell;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testGetStylesForShell() {
    String[] shellStyles = new String[]{
      "BORDER",
      "CLOSE",
      "MIN",
      "MAX",
      "NO_TRIM",
      "RESIZE",
      "TITLE",
      "ON_TOP",
      "TOOL",
      "SHEET",
      "APPLICATION_MODAL",
      "MODELESS",
      "PRIMARY_MODAL",
      "SYSTEM_MODAL"
    };
    String[] allowedStyles = RWTStylesUtil.getAllowedStylesForWidget( shell );
    assertArrayEquals( shellStyles, allowedStyles );
  }

  public void testGetStylesForButton() {
    String[] buttonStyles = new String[]{
      "CHECK",
      "PUSH",
      "RADIO",
      "TOGGLE",
      "FLAT",
      "WRAP",
      "LEFT",
      "RIGHT",
      "CENTER",
      "BORDER",
      "LEFT_TO_RIGHT"
    };
    Button button = new Button( shell, SWT.PUSH );
    String[] allowedStyles = RWTStylesUtil.getAllowedStylesForWidget( button );
    assertArrayEquals( buttonStyles, allowedStyles );
  }
}
