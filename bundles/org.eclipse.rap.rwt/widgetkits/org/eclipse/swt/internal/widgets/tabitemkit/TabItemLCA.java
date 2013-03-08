/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tabitemkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.util.MnemonicUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;


public class TabItemLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.TabItem";

  private static final String PROP_TEXT = "text";
  private static final String PROP_MNEMONIC_INDEX = "mnemonicIndex";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_CONTROL = "control";

  @Override
  public void preserveValues( Widget widget ) {
    TabItem item = ( TabItem )widget;
    WidgetLCAUtil.preserveCustomVariant( item );
    WidgetLCAUtil.preserveData( item );
    WidgetLCAUtil.preserveToolTipText( item, item.getToolTipText() );
    preserveProperty( item, PROP_TEXT, item.getText() );
    preserveProperty( item, PROP_IMAGE, item.getImage() );
    preserveProperty( item, PROP_CONTROL, item.getControl() );
  }

  public void readData( Widget widget ) {
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    TabItem item = ( TabItem )widget;
    TabFolder parent = item.getParent();
    IClientObject clientObject = ClientObjectFactory.getClientObject( item );
    clientObject.create( TYPE );
    // TODO [tb] : Do not render id!
    clientObject.set( "id", WidgetUtil.getId( item ) );
    clientObject.set( "parent", WidgetUtil.getId( parent ) );
    clientObject.set( "index", parent.indexOf( item ) ) ;
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    TabItem item = ( TabItem )widget;
    WidgetLCAUtil.renderCustomVariant( item );
    WidgetLCAUtil.renderData( item );
    WidgetLCAUtil.renderToolTip( item, item.getToolTipText() );
    renderText( item );
    renderMnemonicIndex( item );
    renderProperty( item, PROP_IMAGE, item.getImage(), null );
    renderProperty( item, PROP_CONTROL, item.getControl(), null );
  }

  //////////////////
  // Helping methods

  private static void renderText( TabItem item ) {
    String newValue = item.getText();
    if( WidgetLCAUtil.hasChanged( item, PROP_TEXT, newValue, "" ) ) {
      String text = MnemonicUtil.removeAmpersandControlCharacters( newValue );
      IClientObject clientObject = ClientObjectFactory.getClientObject( item );
      clientObject.set( PROP_TEXT, text );
    }
  }

  private static void renderMnemonicIndex( TabItem item ) {
    String text = item.getText();
    if( WidgetLCAUtil.hasChanged( item, PROP_TEXT, text, "" ) ) {
      int mnemonicIndex = MnemonicUtil.findMnemonicCharacterIndex( text );
      if( mnemonicIndex != -1 ) {
        IClientObject clientObject = ClientObjectFactory.getClientObject( item );
        clientObject.set( PROP_MNEMONIC_INDEX, mnemonicIndex );
      }
    }
  }

}
