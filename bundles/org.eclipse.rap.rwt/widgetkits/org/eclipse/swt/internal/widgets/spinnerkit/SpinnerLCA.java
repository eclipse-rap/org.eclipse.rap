/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;

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
  private static final String[] ALLOWED_STYLES = new String[] {
    "READ_ONLY", "WRAP", "BORDER"
  };

  // Property names for preserveValues
  static final String PROP_MINIMUM = "minimum";
  static final String PROP_MAXIMUM = "maximum";
  static final String PROP_SELECTION = "selection";
  static final String PROP_DIGITS = "digits";
  static final String PROP_INCREMENT = "increment";
  static final String PROP_PAGE_INCREMENT = "pageIncrement";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_DECIMAL_SEPARATOR = "decimalSeparator";
  static final String PROP_MODIFY_LISTENER = "modify";
  static final String PROP_SELECTION_LISTENER = "selection";

  // Default values
  private static final int DEFAULT_MINIMUM = 0;
  private static final int DEFAULT_MAXIMUM = 100;
  private static final int DEFAULT_SELECTION = 0;
  private static final int DEFAULT_DIGITS = 0;
  private static final int DEFAULT_INCREMENT = 1;
  private static final int DEFAULT_PAGE_INCREMENT = 10;
  private static final String DEFAULT_DECIMAL_SEPARATOR = ".";

  public void preserveValues( Widget widget ) {
    Spinner spinner = ( Spinner )widget;
    ControlLCAUtil.preserveValues( spinner );
    WidgetLCAUtil.preserveCustomVariant( spinner );
    preserveProperty( spinner, PROP_MINIMUM, spinner.getMinimum() );
    preserveProperty( spinner, PROP_MAXIMUM, spinner.getMaximum() );
    preserveProperty( spinner, PROP_SELECTION, spinner.getSelection() );
    preserveProperty( spinner, PROP_DIGITS, spinner.getDigits() );
    preserveProperty( spinner, PROP_INCREMENT, spinner.getIncrement() );
    preserveProperty( spinner, PROP_PAGE_INCREMENT, spinner.getPageIncrement() );
    preserveProperty( spinner, PROP_TEXT_LIMIT, getTextLimit( spinner ) );
    preserveProperty( spinner, PROP_DECIMAL_SEPARATOR, getDecimalSeparator() );
    preserveListener( spinner, PROP_MODIFY_LISTENER, ModifyEvent.hasListener( spinner ) );
    preserveListener( spinner, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( spinner ) );
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
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( spinner, ALLOWED_STYLES ) );
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

  ///////////////////////////////////////////////////
  // Helping methods to render the changed properties

  private static void renderMinimum( Spinner spinner ) {
    renderProperty( spinner, PROP_MINIMUM, spinner.getMinimum(), DEFAULT_MINIMUM );
  }

  private static void renderMaximum( Spinner spinner ) {
    renderProperty( spinner, PROP_MAXIMUM, spinner.getMaximum(), DEFAULT_MAXIMUM );
  }

  private static void renderSelection( Spinner spinner ) {
    renderProperty( spinner, PROP_SELECTION, spinner.getSelection(), DEFAULT_SELECTION );
  }

  private static void renderDigits( Spinner spinner ) {
    renderProperty( spinner, PROP_DIGITS, spinner.getDigits(), DEFAULT_DIGITS );
  }

  private static void renderIncrement( Spinner spinner ) {
    renderProperty( spinner, PROP_INCREMENT, spinner.getIncrement(), DEFAULT_INCREMENT );
  }

  private static void renderPageIncrement( Spinner spinner ) {
    int defValue = DEFAULT_PAGE_INCREMENT;
    renderProperty( spinner, PROP_PAGE_INCREMENT, spinner.getPageIncrement(), defValue );
  }

  private static void renderTextLimit( Spinner spinner ) {
    renderProperty( spinner, PROP_TEXT_LIMIT, getTextLimit( spinner ), null );
  }

  private static void renderDecimalSeparator( Spinner spinner ) {
    String defValue = DEFAULT_DECIMAL_SEPARATOR;
    renderProperty( spinner, PROP_DECIMAL_SEPARATOR, getDecimalSeparator(), defValue );
  }

  private static void renderListenModify( Spinner spinner ) {
    renderListener( spinner, PROP_MODIFY_LISTENER, ModifyEvent.hasListener( spinner ), false );
  }

  private static void renderListenSelection( Spinner spinner ) {
    String prop = PROP_SELECTION_LISTENER;
    renderListener( spinner, prop, SelectionEvent.hasListener( spinner ), false );
  }

  //////////////////
  // Helping methods

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
