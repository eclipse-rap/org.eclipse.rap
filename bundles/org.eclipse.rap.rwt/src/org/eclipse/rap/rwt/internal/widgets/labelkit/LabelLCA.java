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
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;


public class LabelLCA extends AbstractWidgetLCA {
  
  private static final Pattern LINE_BREAK_PATTERN = Pattern.compile( "\n" );
  
  public void preserveValues( final Widget widget ) {
    Label label = ( Label )widget;
    ControlLCAUtil.preserveValues( label );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.TEXT, label.getText() );
  }
  
  public void readData( final Widget widget ) {
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "qx.ui.basic.Label" );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    Label label = ( Label )widget;
    ControlLCAUtil.writeChanges( label );
    JSWriter writer = JSWriter.getWriterFor( widget );
    
    // TODO [rh] rendering text that contains html special chars (<, >, etc)
    //      leads to strange results
    //      e.g. setText( "> <" ), setText( "<tralala>" );
    Matcher matcher = LINE_BREAK_PATTERN.matcher( label.getText() );
    String text = matcher.replaceAll( "<br/>" );
    writer.set( Props.TEXT, "html", text, "" );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
