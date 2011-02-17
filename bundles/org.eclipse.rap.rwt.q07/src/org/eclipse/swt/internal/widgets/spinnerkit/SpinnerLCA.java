/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.spinnerkit;

import java.io.IOException;
import java.text.DecimalFormatSymbols;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;


public final class SpinnerLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE = "org.eclipse.swt.widgets.Spinner";

  // Property names for preserveValues
  static final String PROP_SELECTION = "selection";
  static final String PROP_MAXIMUM = "maximum";
  static final String PROP_MINIMUM = "minimum";
  static final String PROP_DIGITS = "digits";
  static final String PROP_INCREMENT = "increment";
  static final String PROP_PAGE_INCREMENT = "pageIncrement";
  static final String PROP_MODIFY_LISTENER = "modifyListener";
  static final String PROP_SELECTION_LISTENER = "selectionListener";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_DECIMAL_SEPARATOR = "decimalSeparator";

  // Default values
  private static final Integer DEFAULT_SELECTION = new Integer( 0 );
  private static final Integer DEFAULT_MAXIMUM = new Integer( 100 );
  private static final Integer DEFAULT_MINIMUM = new Integer( 0 );
  private static final Integer DEFAULT_DIGITS = new Integer( 0 );
  private static final Integer DEFAULT_PAGE_INCREMENT = new Integer( 10 );
  private static final Integer DEFAULT_INCREMENT = new Integer( 1 );
  private static final Integer DEFAULT_TEXT_LIMIT
    = new Integer( Spinner.LIMIT );
  private static final String DEFAULT_DECIMAL_SEPARATOR = ".";

  public void preserveValues( final Widget widget ) {
    Spinner spinner = ( Spinner )widget;
    ControlLCAUtil.preserveValues( spinner );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_SELECTION, new Integer( spinner.getSelection() ) );
    adapter.preserve( PROP_MINIMUM, new Integer( spinner.getMinimum() ) );
    adapter.preserve( PROP_MAXIMUM, new Integer( spinner.getMaximum() ) );
    adapter.preserve( PROP_DIGITS, new Integer( spinner.getDigits() ) );
    adapter.preserve( PROP_INCREMENT, new Integer( spinner.getIncrement() ) );
    adapter.preserve( PROP_PAGE_INCREMENT,
                      new Integer( spinner.getPageIncrement() ) );
    adapter.preserve( PROP_MODIFY_LISTENER,
                      Boolean.valueOf( ModifyEvent.hasListener( spinner ) ) );
    adapter.preserve( PROP_SELECTION_LISTENER,
                      Boolean.valueOf( SelectionEvent.hasListener( spinner ) ) );
    adapter.preserve( PROP_TEXT_LIMIT, new Integer( spinner.getTextLimit() ) );
    adapter.preserve( PROP_DECIMAL_SEPARATOR, getDecimalSeparator() );
    WidgetLCAUtil.preserveCustomVariant( spinner );
  }

  /* (intentionally non-JavaDoc'ed)
   * readData does not explicitly handle modifyEvents. They are fired implicitly
   * by updating the selection property.
   */
  public void readData( final Widget widget ) {
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

  public void renderInitialization( final Widget widget ) throws IOException {
    Spinner spinner = ( Spinner )widget;
    JSWriter writer = JSWriter.getWriterFor( spinner );
    writer.newWidget( QX_TYPE );
    ControlLCAUtil.writeStyleFlags( spinner );
    writeReadOnly( spinner );
    writeWrap( spinner );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Spinner spinner = ( Spinner )widget;
    ControlLCAUtil.writeChanges( spinner );
    writeMinMaxSelection( spinner );
    writeValues( spinner );
    writeTextLimit( spinner );
    writeModifyListener( spinner );
    writeSelectionListener( spinner );
    writeDecimalSeparator( spinner );
    WidgetLCAUtil.writeCustomVariant( spinner );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  //////////////////////////////////////
  // Helping methods to write JavaScript

  private static void writeValues( final Spinner spinner ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( spinner );
    writer.set( PROP_DIGITS,
                "digits",
                new Integer( spinner.getDigits() ),
                DEFAULT_DIGITS );
    writer.set( PROP_INCREMENT,
                "incrementAmount",
                new Integer( spinner.getIncrement() ),
                DEFAULT_INCREMENT );
    writer.set( PROP_INCREMENT,
                "wheelIncrementAmount",
                new Integer( spinner.getIncrement() ),
                DEFAULT_INCREMENT );
    writer.set( PROP_PAGE_INCREMENT,
                "pageIncrementAmount",
                new Integer( spinner.getPageIncrement() ),
                DEFAULT_PAGE_INCREMENT );
  }

  // [if] Spinner#setValues allows minimum, maximum and selection to be set in
  // one hop. In case of not crossed ranges ( for example new min > old max ), a
  // javascript error appears if we set them one by one.
  private static void writeMinMaxSelection( final Spinner spinner )
    throws IOException
  {
    Integer newMin = new Integer( spinner.getMinimum() );
    Integer newMax = new Integer( spinner.getMaximum() );
    Integer newSel = new Integer( spinner.getSelection() );
    boolean minChanged = WidgetLCAUtil.hasChanged( spinner,
                                                   PROP_MINIMUM,
                                                   newMin,
                                                   DEFAULT_MINIMUM );
    boolean maxChanged = WidgetLCAUtil.hasChanged( spinner,
                                                   PROP_MAXIMUM,
                                                   newMax,
                                                   DEFAULT_MAXIMUM );
    boolean selChanged = WidgetLCAUtil.hasChanged( spinner,
                                                   PROP_SELECTION,
                                                   newSel,
                                                   DEFAULT_SELECTION );
    if( minChanged || maxChanged || selChanged ) {
      JSWriter writer = JSWriter.getWriterFor( spinner );
      Integer[] args = new Integer[] {
        newMin, newMax, newSel
      };
      writer.call( "setMinMaxSelection", args );
    }
  }

  private static void writeTextLimit( final Spinner spinner ) throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( spinner );
    Integer newValue = new Integer( spinner.getTextLimit() );
    Integer defValue = DEFAULT_TEXT_LIMIT;
    String prop = PROP_TEXT_LIMIT;
    if( WidgetLCAUtil.hasChanged( spinner, prop, newValue, defValue ) ) {
      // Negative values are treated as 'no limit' which is achieved by passing
      // null to the client-side maxLength property
      if( newValue.intValue() < 0 ) {
        newValue = null;
      }
      writer.set( "maxLength", newValue );
    }
  }

  private static void writeReadOnly( final Spinner spinner ) throws IOException
  {
    boolean readOnly = ( spinner.getStyle() & SWT.READ_ONLY ) != 0;
    JSWriter writer = JSWriter.getWriterFor( spinner );
    writer.set( JSConst.QX_FIELD_EDITABLE, !readOnly );
  }

  private static void writeWrap( final Spinner spinner ) throws IOException {
    if( ( spinner.getStyle() & SWT.WRAP ) != 0 ) {
      JSWriter writer = JSWriter.getWriterFor( spinner );
      writer.set( "wrap", true );
    }
  }

  private static void writeModifyListener( final Spinner spinner )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( spinner );
    String prop = PROP_MODIFY_LISTENER;
    Boolean newValue = Boolean.valueOf( ModifyEvent.hasListener( spinner ) );
    Boolean defValue = Boolean.FALSE;
    writer.set( prop, "hasModifyListener", newValue, defValue );
  }

  private static void writeSelectionListener( final Spinner spinner )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( spinner );
    String prop = PROP_SELECTION_LISTENER;
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( spinner ) );
    Boolean defValue = Boolean.FALSE;
    writer.set( prop, "hasSelectionListener", newValue, defValue );
  }

  private static void writeDecimalSeparator( final Spinner spinner )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( spinner );
    String prop = PROP_DECIMAL_SEPARATOR;
    String newValue = getDecimalSeparator();
    String defValue = DEFAULT_DECIMAL_SEPARATOR;
    writer.set( prop, "decimalSeparator", newValue, defValue );
  }

  private static String getDecimalSeparator() {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols( RWT.getLocale() );
    return String.valueOf( symbols.getDecimalSeparator() );
  }
}
