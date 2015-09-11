/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.dnd;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.dnd.droptargetkit.DropTargetLCA;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class DropTarget_Test {

  @Rule
  public TestContext context = new TestContext();

  private DropTarget dropTarget;

  @Before
  public void setUp() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    dropTarget = new DropTarget( shell, SWT.NONE );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( dropTarget.getAdapter( WidgetLCA.class ) instanceof DropTargetLCA );
    assertSame( dropTarget.getAdapter( WidgetLCA.class ), dropTarget.getAdapter( WidgetLCA.class ) );
  }

}
