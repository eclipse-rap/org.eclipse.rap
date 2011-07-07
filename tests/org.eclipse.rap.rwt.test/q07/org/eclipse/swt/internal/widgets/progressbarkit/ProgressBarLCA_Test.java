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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class ProgressBarLCA_Test extends TestCase {

  private Shell shell;

  public void testPreserveValues() {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( progressBar );
    Object preserved = adapter.getPreserved( ProgressBarLCA.PROP_STATE );
    assertNull( preserved );
  }

  public void testRenderChanges() throws Exception {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    Fixture.markInitialized( progressBar );
    ProgressBarLCA lca = new ProgressBarLCA();
    Fixture.fakeResponseWriter();
    progressBar.setState( SWT.ERROR );
    lca.renderChanges( progressBar );
    String allMarkup = Fixture.getAllMarkup();
    String expected = "w.setState( \"error\" );";
    assertTrue( allMarkup.indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    lca.preserveValues( progressBar );
    progressBar.setState( SWT.NORMAL );
    lca.renderChanges( progressBar );
    allMarkup = Fixture.getAllMarkup();
    expected = "w.setState( null );";
    assertTrue( allMarkup.indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    Fixture.markInitialized( display );
    shell = new Shell( display, SWT.NONE );
    Fixture.markInitialized( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
