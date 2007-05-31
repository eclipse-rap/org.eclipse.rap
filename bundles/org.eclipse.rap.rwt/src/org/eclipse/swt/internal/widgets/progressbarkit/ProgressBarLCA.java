/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.progressbarkit;

import java.io.IOException;

import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;


public class ProgressBarLCA extends AbstractWidgetLCA {

  private static final String PROP_MINIMUM = "minimum";
  private static final String PROP_MAXIMUM = "maximum";
  private static final String PROP_SELECTION = "selection";

  public void preserveValues( final Widget widget ) {
    ProgressBar progressBar = ( ProgressBar )widget;
    ControlLCAUtil.preserveValues( progressBar );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( progressBar );
    adapter.preserve( PROP_MINIMUM, new Integer( progressBar.getMinimum() ) );
    adapter.preserve( PROP_MAXIMUM, new Integer( progressBar.getMaximum() ) );
    adapter.preserve( PROP_SELECTION, 
                      new Integer( progressBar.getSelection() ) );
  }
  
  public void readData( final Widget widget ) {
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    ProgressBar progressBar = ( ProgressBar )widget;
    JSWriter writer = JSWriter.getWriterFor( progressBar );
    writer.newWidget( "org.eclipse.swt.widgets.ProgressBar" );
    ControlLCAUtil.writeStyleFlags( progressBar );
    writer.set( "flag", progressBar.getStyle() );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ProgressBar pBar = ( ProgressBar )widget;
    ControlLCAUtil.writeStyleFlags( pBar );
    ControlLCAUtil.writeChanges( pBar );
    // do not change range and selection order
    writeSetInt( pBar, PROP_MINIMUM, "minimum", pBar.getMinimum(), 0 );
    writeSetInt( pBar, PROP_MAXIMUM, "maximum", pBar.getMaximum(), 100 );
    writeSetInt( pBar, PROP_SELECTION, "selection", pBar.getSelection(), 0 );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
  
  private static void writeSetInt( final ProgressBar progressBar,
                                   final String javaProperty,
                                   final String jsProperty,
                                   final int newValue,
                                   final int defValue ) 
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( progressBar );
    writer.set( javaProperty, 
                jsProperty, 
                new Integer( newValue ), 
                new Integer( defValue ) );
  }
}
