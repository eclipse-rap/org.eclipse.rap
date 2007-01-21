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

package org.eclipse.rap.rwt.internal.widgets.labelkit;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.Label;


public class StandardLabelLCA extends AbstractLabelLCADelegate {

  private static final Pattern LINE_BREAK_PATTERN = Pattern.compile( "\n" );

  private static final String PROP_TEXT = "text";
  private static final String PROP_ALIGNMENT = "alignment";
  private static final String PROP_IMAGE = "image";

  private static final Integer DEFAULT_ALIGNMENT = new Integer( RWT.LEFT );

  void preserveValues( final Label label ) {
    ControlLCAUtil.preserveValues( label );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    adapter.preserve( PROP_TEXT, label.getText() );
    adapter.preserve( PROP_IMAGE, label.getImage() );
    adapter.preserve( PROP_ALIGNMENT, new Integer( label.getAlignment() ) );
  }

  void readData( final Label label ) {
  }

  void renderInitialization( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.newWidget( "qx.ui.basic.Label" );
    ControlLCAUtil.writeStyleFlags( label );
  }
  
  void renderChanges( final Label label ) throws IOException {
    ControlLCAUtil.writeChanges( label );
    writeAlignment( label );
    writeText( label );
  }

  void renderDispose( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.dispose();
  }

  //////////////////////////////////////
  // Helping methods to write JavaScript

  private static void writeText( final Label label ) throws IOException {
    if( WidgetUtil.hasChanged( label, PROP_TEXT, label.getText(), "" ) ) {
      // TODO [rh] rendering text that contains html special chars (<, >, etc)
      //      leads to strange results
      //      e.g. setText( "> <" ), setText( "<tralala>" );
      Matcher matcher = LINE_BREAK_PATTERN.matcher( label.getText() );
      String text = matcher.replaceAll( "<br/>" );
      JSWriter writer = JSWriter.getWriterFor( label );
      writer.set( "html", text );
    }
  }

  private static void writeAlignment( final Label label ) throws IOException {
    Integer alignment = new Integer( label.getAlignment() );
    Integer defValue = DEFAULT_ALIGNMENT;
    if( WidgetUtil.hasChanged( label, PROP_ALIGNMENT, alignment, defValue ) ) 
    {
      JSWriter writer = JSWriter.getWriterFor( label );
      Object[] args = new Object[] { getAlignment( label.getAlignment() ) };
      writer.set( "textAlign", args );
    }
  }

  private static String getAlignment( final int alignment ) {
    String result;
    if( ( alignment & RWT.LEFT ) != 0 ) {
      result = "left";
    } else if( ( alignment & RWT.CENTER ) != 0 ) {
      result = "center";
    } else if( ( alignment & RWT.RIGHT ) != 0 ) {
      result = "right";
    } else {
      result = "left";
    }
    return result;
  }
}
