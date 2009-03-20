/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.forms.internal.widgets.hyperlinkkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.internal.widgets.IHyperlinkAdapter;
import org.eclipse.ui.forms.widgets.Hyperlink;

/* (intentionally non-JavaDoc'ed)
 * This class serves as the LCA for org.eclipse.ui.forms.widgets.TreeNode and
 * org.eclipse.ui.forms.widgets.Twistie.
 */
public class HyperlinkLCA extends AbstractWidgetLCA {

  private static final String PROP_TEXT
    = "text"; //$NON-NLS-1$
  private static final String PROP_UNDERLINED
    = "underlined"; //$NON-NLS-1$
  private static final String PROP_SELECTION_LISTENERS
    = "selectionListeners"; //$NON-NLS-1$
  private static final String PROP_ACTIVE_FOREGROUND
    = "activeForeground"; //$NON-NLS-1$
  private static final String PROP_ACTIVE_BACKGROUND
    = "activeBackground"; //$NON-NLS-1$
  private static final String PROP_INACTIVE_FOREGROUND
    = "inactiveForeground"; //$NON-NLS-1$
  private static final String PROP_INACTIVE_BACKGROUND
    = "inactiveBackground"; //$NON-NLS-1$
  private static final String PROP_UNDERLINE_MODE
    = "underlineMode"; //$NON-NLS-1$

  private static final JSListenerInfo SELECTION_LISTENER
    = new JSListenerInfo( "click", //$NON-NLS-1$
                          "org.eclipse.ui.forms.widgets.Hyperlink.onClick", //$NON-NLS-1$
                          JSListenerType.ACTION );

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
  }

  public String getTypePoolId( final Widget widget ) {
    return null;
  }

  public void readData( final Widget widget ) {
    ControlLCAUtil.processSelection( widget, null, false );
  }

  public void preserveValues( final Widget widget ) {
    Hyperlink hyperlink = ( Hyperlink )widget;
    ControlLCAUtil.preserveValues( hyperlink );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( hyperlink );
    adapter.preserve( PROP_TEXT, hyperlink.getText() );
    adapter.preserve( PROP_UNDERLINED,
                      Boolean.valueOf( hyperlink.isUnderlined() ) );
    boolean hasListener = SelectionEvent.hasListener( hyperlink );
    adapter.preserve( PROP_SELECTION_LISTENERS,
                      Boolean.valueOf( hasListener ) );
    adapter.preserve( PROP_ACTIVE_BACKGROUND,
    		          getActiveBackground( hyperlink ) );
    adapter.preserve( PROP_ACTIVE_FOREGROUND,
                      getActiveForeground( hyperlink ) );
    adapter.preserve( PROP_UNDERLINE_MODE,
                      getUnderlineMode( hyperlink ) );
//    adapter.preserve( PROP_INACTIVE_BACKGROUND, hyperlink.getBackground() );
//    adapter.preserve( PROP_INACTIVE_FOREGROUND, hyperlink.getForeground() );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Hyperlink hyperlink = ( Hyperlink )widget;
    JSWriter writer = JSWriter.getWriterFor( hyperlink );
    writer.newWidget( "org.eclipse.ui.forms.widgets.Hyperlink" ); //$NON-NLS-1$
    WidgetLCAUtil.writeCustomVariant( widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Hyperlink hyperlink = ( Hyperlink )widget;
    ControlLCAUtil.writeChanges( hyperlink );
    writeText( hyperlink );
    writeSelectionListener( hyperlink );
    writeActiveForeground( hyperlink );
    writeActiveBackground( hyperlink );
    writeUnderlineMode( hyperlink );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  ////////////////
  // Write changes

  private static void writeText( final Hyperlink hyperlink ) throws IOException
  {
    String text = hyperlink.getText();
    Boolean underlined = Boolean.valueOf( hyperlink.isUnderlined() );
    Boolean def = Boolean.FALSE;
    boolean textChanged
      = WidgetLCAUtil.hasChanged( hyperlink, PROP_TEXT, text, "" ); //$NON-NLS-1$
    boolean underlinedChanged
      = WidgetLCAUtil.hasChanged( hyperlink, PROP_UNDERLINED, underlined, def );
    if( textChanged || underlinedChanged ) {
      text = WidgetLCAUtil.escapeText( text, false );
      if( underlined.booleanValue() ) {
        text = underlineText( text );
      }
      JSWriter writer = JSWriter.getWriterFor( hyperlink );
      writer.set( "label", text ); //$NON-NLS-1$
    }
  }

  private static void writeSelectionListener( final Hyperlink hyperlink )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( hyperlink );
    writer.updateListener( SELECTION_LISTENER,
                           PROP_SELECTION_LISTENERS,
                           SelectionEvent.hasListener( hyperlink ) );
  }

  private static void writeActiveForeground( final Hyperlink hyperlink )
    throws IOException
  {
	  Color newValue = getActiveForeground( hyperlink );
	  JSWriter writer = JSWriter.getWriterFor( hyperlink );
	  writer.set( PROP_ACTIVE_FOREGROUND,
	              "activeTextColor", //$NON-NLS-1$
	              newValue,
	              null );
  }

  private static void writeActiveBackground( final Hyperlink hyperlink )
    throws IOException
  {
    Color newValue = getActiveBackground( hyperlink );
    JSWriter writer = JSWriter.getWriterFor( hyperlink );
    writer.set( PROP_ACTIVE_BACKGROUND,
                "activeBackgroundColor", //$NON-NLS-1$
                newValue,
                null );
  }

  private static void writeUnderlineMode( final Hyperlink hyperlink )
    throws IOException
  {
    Integer newValue = getUnderlineMode( hyperlink );
    JSWriter writer = JSWriter.getWriterFor( hyperlink );
    writer.set( PROP_UNDERLINE_MODE,
                "underlineMode", //$NON-NLS-1$
                newValue,
                null );
  }

  //////////////////
  // Helping methods

  private static String underlineText( final String text ) {
    StringBuffer result = new StringBuffer();
    result.append( "<u>" ); //$NON-NLS-1$
    result.append( text );
    result.append( "</u>" ); //$NON-NLS-1$
    return result.toString();
  }

  private static Color getActiveForeground( final Hyperlink hyperlink ) {
    Object adapter = hyperlink.getAdapter( IHyperlinkAdapter.class );
    IHyperlinkAdapter hyperlinkAdapter = ( IHyperlinkAdapter )adapter;
    Color newValue = hyperlinkAdapter.getActiveForeground();
    return newValue;
  }

  private static Color getActiveBackground( final Hyperlink hyperlink ) {
    Object adapter = hyperlink.getAdapter( IHyperlinkAdapter.class );
    IHyperlinkAdapter hyperlinkAdapter = ( IHyperlinkAdapter )adapter;
    Color newValue = hyperlinkAdapter.getActiveBackground();
    return newValue;
  }

  private static Integer getUnderlineMode( final Hyperlink hyperlink ) {
    Object adapter = hyperlink.getAdapter( IHyperlinkAdapter.class );
    IHyperlinkAdapter hyperlinkAdapter = ( IHyperlinkAdapter )adapter;
    Integer newValue = new Integer( hyperlinkAdapter.getUnderlineMode() );
    return newValue;
  }

}
