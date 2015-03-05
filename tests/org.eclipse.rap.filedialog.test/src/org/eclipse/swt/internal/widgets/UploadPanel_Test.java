/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.internal.widgets.FileUploadRunnable.State;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "restriction" )
public class UploadPanel_Test {

  private Display display;
  private Shell shell;
  private UploadPanel uploadPanel;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    uploadPanel = new UploadPanel( shell, new String[] { "foo", "bar" } );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreate_createsUploadsRows() {
    Control[] children = uploadPanel.getChildren();

    assertEquals( 2, children.length );
    assertTrue( children[ 0 ] instanceof Composite );
    assertTrue( children[ 1 ] instanceof Composite );
  }

  @Test
  public void testCreate_createsUploadsRows_withNullFileName() {
    uploadPanel = new UploadPanel( shell, new String[] { null, "bar" } );

    assertEquals( 1, uploadPanel.getChildren().length );
  }

  @Test
  public void testCreate_initsFileNames() {
    assertEquals( "foo", ( ( Label )getRow( 0 ).getChildren()[ 1 ] ).getText() );
    assertEquals( "bar", ( ( Label )getRow( 1 ).getChildren()[ 1 ] ).getText() );
  }

  @Test
  public void testUpdateIcons_onDisposedUploadPanel() {
    uploadPanel.dispose();

    uploadPanel.updateIcons( State.UPLOADING );
  }

  private Composite getRow( int index ) {
    return ( Composite )uploadPanel.getChildren()[ index ];
  }

}
