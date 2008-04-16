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

package org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit;

import java.io.IOException;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.*;

/* (intentionally non-JavaDoc'ed)
 * This class serves as the LCA for org.eclipse.ui.forms.widgets.TreeNode and
 * org.eclipse.ui.forms.widgets.Twistie.
 */
public final class ToggleHyperlinkLCA extends AbstractWidgetLCA {

  private static final String PROP_EXPANDED
    = "expanded"; //$NON-NLS-1$
  private static final String PROP_SELECTION_LISTENERS
    = "selectionListeners"; //$NON-NLS-1$

  private static final String PREFIX
    = "resource/widget/rap/hyperlink/"; //$NON-NLS-1$
  private static final String MINUS_GIF = PREFIX + "minus.gif"; //$NON-NLS-1$
  private static final String PLUS_GIF = PREFIX + "plus.gif"; //$NON-NLS-1$

  private static final String TWISTIE_COLLAPSE_GIF
    = PREFIX + "twistie_collapse.gif"; //$NON-NLS-1$
  private static final String TWISTIE_COLLAPSE_HOVER_GIF
    = PREFIX + "twistie_collapse_hover.gif"; //$NON-NLS-1$
  private static final String TWISTIE_EXPAND_GIF
    = PREFIX + "twistie_expand.gif"; //$NON-NLS-1$
  private static final String TWISTIE_EXPAND_HOVER_GIF
    = PREFIX + "twistie_expand_hover.gif"; //$NON-NLS-1$

  private static final JSListenerInfo SELECTION_LISTENER
    = new JSListenerInfo( "click", //$NON-NLS-1$
                          "org.eclipse.ui.forms.widgets.ToggleHyperlink.onClick", //$NON-NLS-1$
                          JSListenerType.ACTION );

  public void preserveValues( final Widget widget ) {
    ToggleHyperlink hyperlink = ( ToggleHyperlink )widget;
    ControlLCAUtil.preserveValues( hyperlink );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( hyperlink );
    adapter.preserve( PROP_EXPANDED,
                      Boolean.valueOf( hyperlink.isExpanded() ) );
    boolean hasListener = SelectionEvent.hasListener( hyperlink );
    adapter.preserve( PROP_SELECTION_LISTENERS,
                      Boolean.valueOf( hasListener ) );
  }

  public void readData( final Widget widget ) {
    // It is not neccessary to read the expanded state as a HyperlinkListener
    // will always be registered (see ToggleHyperlink).
    ControlLCAUtil.processSelection( widget, null, false );
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    ToggleHyperlink hyperlink = ( ToggleHyperlink )widget;
    JSWriter writer = JSWriter.getWriterFor( hyperlink );
    writer.newWidget( "org.eclipse.ui.forms.widgets.ToggleHyperlink" ); //$NON-NLS-1$
    WidgetLCAUtil.writeCustomVariant( widget );
    writeImages( hyperlink );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ToggleHyperlink hyperlink = ( ToggleHyperlink )widget;
    ControlLCAUtil.writeChanges( hyperlink );
    writeExpanded( hyperlink );
    writeSelectionListener( hyperlink );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
  }

  public String getTypePoolId( final Widget widget ) {
    return null;
  }

  ////////////////
  // Write changes

  private void writeImages( final ToggleHyperlink hyperlink )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( hyperlink );
    Image[] images = getImages( hyperlink );
    String[] imageNames = new String[ images.length ];
    for( int i = 0; i < imageNames.length; i++ ) {
      imageNames[ i ] = ResourceFactory.getImagePath( images[ i ] );
    }
    writer.set( "images", imageNames ); //$NON-NLS-1$

  }

  private static void writeExpanded( final ToggleHyperlink hyperlink )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( hyperlink );
    Boolean newValue = Boolean.valueOf( hyperlink.isExpanded() );
    writer.set( PROP_EXPANDED, 
                "expanded", //$NON-NLS-1$ 
                newValue, 
                Boolean.FALSE );
  }

  private static void writeSelectionListener( final ToggleHyperlink hyperlink )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( hyperlink );
    writer.updateListener( SELECTION_LISTENER,
                           PROP_SELECTION_LISTENERS,
                           SelectionEvent.hasListener( hyperlink ) );
  }

  /* (intentiaonally non-JavaDoc'ed)
   * Returns four images for:
   *   collapsedNormal, collapsedHover, expandedNormal, expandedHover
   */
  private static Image[] getImages( final ToggleHyperlink hyperlink ) {
    ClassLoader classLoader = ToggleHyperlinkLCA.class.getClassLoader();
    Image[] result;
    if( hyperlink instanceof TreeNode ) {
      result = new Image[] {
        Graphics.getImage( MINUS_GIF, classLoader ),
        null,
        Graphics.getImage( PLUS_GIF, classLoader ),
        null
      };
    } else if( hyperlink instanceof Twistie ) {
      result = new Image[] {
        Graphics.getImage( TWISTIE_COLLAPSE_GIF, classLoader ),
        Graphics.getImage( TWISTIE_COLLAPSE_HOVER_GIF, classLoader ),
        Graphics.getImage( TWISTIE_EXPAND_GIF, classLoader ),
        Graphics.getImage( TWISTIE_EXPAND_HOVER_GIF, classLoader )
      };
    } else {
      result = new Image[] { null, null, null, null };
    }
    return result;
  }
}
