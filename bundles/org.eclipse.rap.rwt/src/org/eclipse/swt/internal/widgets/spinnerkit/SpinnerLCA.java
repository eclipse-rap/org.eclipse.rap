/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.spinnerkit;

import java.io.IOException;
import java.text.DecimalFormatSymbols;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;


public final class SpinnerLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Spinner";

  // Property names for preserveValues
  static final String PROP_MINIMUM = "minimum";
  static final String PROP_MAXIMUM = "maximum";
  static final String PROP_SELECTION = "selection";
  static final String PROP_DIGITS = "digits";
  static final String PROP_INCREMENT = "increment";
  static final String PROP_PAGE_INCREMENT = "pageIncrement";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_DECIMAL_SEPARATOR = "decimalSeparator";
  static final String PROP_MODIFY_LISTENER = "modifyListener";
  static final String PROP_SELECTION_LISTENER = "selectionListener";

  // Default values
  private static final Integer DEFAULT_MINIMUM = new Integer( 0 );
  private static final Integer DEFAULT_MAXIMUM = new Integer( 100 );
  private static final Integer DEFAULT_SELECTION = new Integer( 0 );
  private static final Integer DEFAULT_DIGITS = new Integer( 0 );
  private static final Integer DEFAULT_INCREMENT = new Integer( 1 );
  private static final Integer DEFAULT_PAGE_INCREMENT = new Integer( 10 );
  private static final String DEFAULT_DECIMAL_SEPARATOR = ".";

  public void preserveValues( Widget widget ) {
    Spinner spinner = ( Spinner )widget;
    ControlLCAUtil.preserveValues( spinner );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_MINIMUM, new Integer( spinner.getMinimum() ) );
    adapter.preserve( PROP_MAXIMUM, new Integer( spinner.getMaximum() ) );
    adapter.preserve( PROP_SELECTION, new Integer( spinner.getSelection() ) );
    adapter.preserve( PROP_DIGITS, new Integer( spinner.getDigits() ) );
    adapter.preserve( PROP_INCREMENT, new Integer( spinner.getIncrement() ) );
    adapter.preserve( PROP_PAGE_INCREMENT, new Integer( spinner.getPageIncrement() ) );
    adapter.preserve( PROP_TEXT_LIMIT, getTextLimit( spinner ) );
    adapter.preserve( PROP_DECIMAL_SEPARATOR, getDecimalSeparator() );
    adapter.preserve( PROP_MODIFY_LISTENER, Boolean.valueOf( ModifyEvent.hasListener( spinner ) ) );
    adapter.preserve( PROP_SELECTION_LISTENER,
                      Boolean.valueOf( SelectionEvent.hasListener( spinner ) ) );
    WidgetLCAUtil.preserveCustomVariant( spinner );
  }

  /* (intentionally non-JavaDoc'ed)
   * readData does not explicitly handle modifyEvents. They are fired implicitly
   * by updating the selection property.
   */
  public void readData( Widget widget ) {
    Spinner spinner = ( Spinner )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "selection" );
    if( value != null ) {
      spinner.setSelection( NumberFormatUtil.parseInt( value ) );
    }
    ControlLCAUtil.processSelection( widget, null, false );
    ControlLCAUtil.processMouseEvents( spinner );
    ControlLCAUtil.processKeyEvents( spinner );
    ControlLCAUtil.processMenuDetect( spinner );
    WidgetLCAUtil.processHelp( spinner );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    Spinner spinner = ( Spinner )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( spinner );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( spinner.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( spinner ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    Spinner spinner = ( Spinner )widget;
    ControlLCAUtil.renderChanges( spinner );
    WidgetLCAUtil.renderCustomVariant( spinner );
    renderMinimum( spinner );
    renderMaximum( spinner );
    renderSelection( spinner );
    renderDigits( spinner );
    renderIncrement( spinner );
    renderPageIncrement( spinner );
    renderTextLimit( spinner );
    renderDecimalSeparator( spinner );
    renderListenModify( spinner );
    renderListenSelection( spinner );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  //////////////////////////////////////
  // Helping methods to write JavaScript

  private static void renderMinimum( Spinner spinner ) {
    renderProperty( spinner, PROP_MINIMUM, new Integer( spinner.getMinimum() ), DEFAULT_MINIMUM );
  }

  private static void renderMaximum( Spinner spinner ) {
    renderProperty( spinner, PROP_MAXIMUM, new Integer( spinner.getMaximum() ), DEFAULT_MAXIMUM );
  }

  private static void renderSelection( Spinner spinner ) {
    Integer defValue = DEFAULT_SELECTION;
    renderProperty( spinner, PROP_SELECTION, new Integer( spinner.getSelection() ), defValue );
  }

  private static void renderDigits( Spinner spinner ) {
    renderProperty( spinner, PROP_DIGITS, new Integer( spinner.getDigits() ), DEFAULT_DIGITS );
  }

  private static void renderIncrement( Spinner spinner ) {
    Integer defValue = DEFAULT_INCREMENT;
    renderProperty( spinner, PROP_INCREMENT, new Integer( spinner.getIncrement() ), defValue );
  }

  private static void renderPageIncrement( Spinner spinner ) {
    String prop = PROP_PAGE_INCREMENT;
    Integer defValue = DEFAULT_PAGE_INCREMENT;
    renderProperty( spinner, prop, new Integer( spinner.getPageIncrement() ), defValue );
  }

  private static void renderTextLimit( Spinner spinner ) {
    renderProperty( spinner, PROP_TEXT_LIMIT, getTextLimit( spinner ), null );
  }

  private static void renderDecimalSeparator( Spinner spinner ) {
    String defValue = DEFAULT_DECIMAL_SEPARATOR;
    renderProperty( spinner, PROP_DECIMAL_SEPARATOR, getDecimalSeparator(), defValue );
  }

  private static void renderListenModify( Spinner spinner ) {
    Boolean newValue = Boolean.valueOf( ModifyEvent.hasListener( spinner ) );
    if( WidgetLCAUtil.hasChanged( spinner, PROP_MODIFY_LISTENER, newValue, Boolean.FALSE ) ) {
      renderListen( spinner, "modify", newValue.booleanValue() );
    }
  }

  private static void renderListenSelection( Spinner spinner ) {
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( spinner ) );
    if( WidgetLCAUtil.hasChanged( spinner, PROP_SELECTION_LISTENER, newValue, Boolean.FALSE ) ) {
      renderListen( spinner, "selection", newValue.booleanValue() );
    }
  }

  //////////////////
  // Helping methods

  private static void renderProperty( Spinner spinner,
                                      String property,
                                      Object newValue,
                                      Object defValue )
  {
    if( WidgetLCAUtil.hasChanged( spinner, property, newValue, defValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( spinner );
      clientObject.setProperty( property, newValue );
    }
  }

  private static void renderListen( Spinner spinner, String eventType, boolean hasListener ) {
    IClientObject clientObject = ClientObjectFactory.getForWidget( spinner );
    if( hasListener ) {
      clientObject.addListener( eventType );
    } else {
      clientObject.removeListener( eventType );
    }
  }

  private static Integer getTextLimit( Spinner spinner ) {
    Integer result = null;
    int textLimit = spinner.getTextLimit();
    if( textLimit > 0 && textLimit != Spinner.LIMIT ) {
      result = new Integer( textLimit );
    }
    return result;
  }

  private static String getDecimalSeparator() {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols( RWT.getLocale() );
    return String.valueOf( symbols.getDecimalSeparator() );
  }
}
