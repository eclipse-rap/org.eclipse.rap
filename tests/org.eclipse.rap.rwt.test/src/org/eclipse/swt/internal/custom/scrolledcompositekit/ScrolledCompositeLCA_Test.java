/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.custom.scrolledcompositekit;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.lifecycle.IWidgetAdapter;
import org.eclipse.swt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.*;

public class ScrolledCompositeLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    int style = SWT.H_SCROLL | SWT.V_SCROLL;
    ScrolledComposite sc = new ScrolledComposite( shell, style );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( sc );
    assertEquals( null, adapter.getPreserved( "hBarSelection" ) );
    assertEquals( null, adapter.getPreserved( "vBarSelection" ) );
    sc.getHorizontalBar().setSelection( 23 );
    sc.getVerticalBar().setSelection( 42 );
    assertEquals( 23, sc.getHorizontalBar().getSelection() );
    assertEquals( 42, sc.getVerticalBar().getSelection() );
    sc.getHorizontalBar().setVisible( true );
    sc.getVerticalBar().setVisible( true );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    assertEquals( new Integer( 23 ), adapter.getPreserved( "hBarSelection" ) );
    assertEquals( new Integer( 42 ), adapter.getPreserved( "vBarSelection" ) );
    display.dispose();
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
