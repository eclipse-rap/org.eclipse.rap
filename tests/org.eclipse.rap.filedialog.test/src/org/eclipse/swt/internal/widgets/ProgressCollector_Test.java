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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "restriction" )
public class ProgressCollector_Test {

  private Display display;
  private Shell shell;
  private ProgressCollector progressCollector;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    progressCollector = new ProgressCollector( shell );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreate_createsProgressBar() {
    assertNotNull( getProgressBar() );
  }

  @Test
  public void testGetCompletedFileNames_initially() {
    assertEquals( 0, progressCollector.getCompletedFileNames().length );
  }

  @Test
  public void testUpdateCompletedFileNames() {
    List<String> fileNames = new ArrayList<String>();
    fileNames.add( "foo" );

    progressCollector.updateCompletedFiles( fileNames );

    assertEquals( "foo", progressCollector.getCompletedFileNames()[ 0 ] );
  }

  @Test
  public void testUpdateCompletedFileNames_onDisposedProgressCollector() {
    List<String> fileNames = new ArrayList<String>();
    fileNames.add( "foo" );
    progressCollector.dispose();

    progressCollector.updateCompletedFiles( fileNames );

    assertEquals( 0, progressCollector.getCompletedFileNames().length );
  }

  @Test
  public void testUpdateProgress() {
    progressCollector.updateProgress( 20 );

    assertEquals( 20, getProgressBar().getSelection() );
  }

  @Test
  public void testUpdateProgress_setsToolTip() {
    progressCollector.updateProgress( 20 );

    assertEquals( "20%", getProgressBar().getToolTipText() );
  }

  @Test
  public void testUpdateProgress_onDisposedProgressCollector_doesNotThrowException() {
    progressCollector.dispose();

    progressCollector.updateProgress( 20 );
  }

  @Test
  public void testResetToolTip() {
    progressCollector.updateProgress( 20 );

    progressCollector.resetToolTip();

    assertNull( getProgressBar().getToolTipText() );
  }

  private ProgressBar getProgressBar() {
    return ( ProgressBar )progressCollector.getChildren()[ 0 ];
  }

}
