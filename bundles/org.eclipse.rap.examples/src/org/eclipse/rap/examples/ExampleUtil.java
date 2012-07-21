/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples;

import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public final class ExampleUtil {

  private static final int DEFAULT_SPACE = 10;

  public static Composite initPage( String title, Composite parent ) {
    Composite pageComp = new Composite( parent, SWT.NONE );
    pageComp.setLayout( ExampleUtil.createGridLayoutWithoutMargin( 1, false ) );
    Label label = createHeadlineLabel( pageComp, title );
    label.setLayoutData( createHeadlineLayoutData() );
    Composite contentComp = new Composite( pageComp, SWT.NONE );
    contentComp.setLayoutData( ExampleUtil.createFillData() );
    return contentComp;
  }

  public static void createHeading( Composite parent, String text, int horizontalSpan ) {
    Label label = new Label( parent, SWT.NONE );
    label.setText( text );
    label.setData(  WidgetUtil.CUSTOM_VARIANT, "heading" );
    GridData labelLayoutData = new GridData();
    labelLayoutData.horizontalSpan = horizontalSpan;
    label.setLayoutData( labelLayoutData );
  }

  public static GridLayout createMainLayout( int numColumns ) {
    GridLayout result = new GridLayout( numColumns, true );
    result.marginWidth = 0;
    result.marginHeight = 0;
    result.marginTop = 0;
    result.verticalSpacing = 0;
    result.horizontalSpacing = 60;
    return result;
  }

  public static GridLayout createMainLayout( int numColumns, int horzSpacing ) {
    GridLayout result = new GridLayout( numColumns, true );
    result.marginWidth = 0;
    result.marginHeight = 0;
    result.marginTop = 0;
    result.verticalSpacing = 0;
    result.horizontalSpacing = horzSpacing;
    return result;
  }

  public static GridLayout createGridLayout( int numColumns,
                                             boolean makeColsEqualWidth,
                                             boolean setTopMargin,
                                             boolean setVertSpacing )
  {
    GridLayout result = new GridLayout( numColumns, makeColsEqualWidth );
    result.marginWidth = DEFAULT_SPACE;
    result.marginHeight = 0;
    result.marginBottom = DEFAULT_SPACE;
    result.horizontalSpacing = DEFAULT_SPACE;
    if( setTopMargin ) {
      result.marginTop = DEFAULT_SPACE;
    }
    if( setVertSpacing ) {
      result.verticalSpacing = DEFAULT_SPACE;
    }
    return result;
  }

  public static GridLayout createGridLayoutWithoutMargin( int numColumns,
                                                          boolean makeColsEqualWidth )
  {
    GridLayout result = new GridLayout( numColumns, makeColsEqualWidth );
    result.marginHeight = 0;
    result.marginWidth = 0;
    return result;
  }

  public static RowLayout createRowLayout( int type, boolean setMargin ) {
    RowLayout result = new RowLayout( type );
    result.marginTop = 0;
    result.marginLeft = 0;
    result.marginHeight = 0;
    if( setMargin ) {
      result.marginBottom = DEFAULT_SPACE;
      result.marginWidth = DEFAULT_SPACE;
    } else {
      result.marginBottom = 0;
      result.marginWidth = 0;
    }
    return result;
  }

  public static FillLayout createFillLayout( boolean setMargin ) {
    FillLayout result = new FillLayout();
    if( setMargin ) {
      result.marginWidth = DEFAULT_SPACE;
      result.marginHeight = DEFAULT_SPACE;
    }
    return result;
  }

  public static GridData createHorzFillData() {
    return new GridData( SWT.FILL, SWT.TOP, true, false );
  }

  public static GridData createFillData() {
    return new GridData( SWT.FILL, SWT.FILL, true, true );
  }

  private static Label createHeadlineLabel( Composite parent, String text ) {
    Label label = new Label( parent, SWT.NONE );
    label.setText( text.replace( "&", "&&" ) );
    label.setData(  WidgetUtil.CUSTOM_VARIANT, "pageHeadline" );
    return label;
  }

  private static GridData createHeadlineLayoutData() {
    GridData layoutData = new GridData();
    layoutData.verticalIndent = 30;
    layoutData.horizontalIndent = DEFAULT_SPACE;
    return layoutData;
  }

  private ExampleUtil() {
    // prevent instantiation
  }

}
