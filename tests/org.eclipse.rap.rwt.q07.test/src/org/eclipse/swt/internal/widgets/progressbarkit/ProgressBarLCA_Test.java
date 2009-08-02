/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.progressbarkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class ProgressBarLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( progressBar );
    Object preserved = adapter.getPreserved( ProgressBarLCA.PROP_STATE );
    assertNull( preserved );
  }

  public void testRenderChanges() throws Exception {
    Display display = new Display();
    RWTFixture.markInitialized( display );
    Shell shell = new Shell( display, SWT.NONE );
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    RWTFixture.markInitialized( progressBar );
    ProgressBarLCA lca = new ProgressBarLCA();
    Fixture.fakeResponseWriter();
    progressBar.setState( SWT.ERROR );
    lca.renderChanges( progressBar );
    String allMarkup = Fixture.getAllMarkup();
    String expected = "w.setState( \"error\" );";
    assertTrue( allMarkup.indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    lca.preserveValues( progressBar );
    progressBar.setState( SWT.NORMAL );
    RWTFixture.markInitialized( display );
    lca.renderChanges( progressBar );
    allMarkup = Fixture.getAllMarkup();
    expected = "w.setState( null );";
    assertTrue( allMarkup.indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
