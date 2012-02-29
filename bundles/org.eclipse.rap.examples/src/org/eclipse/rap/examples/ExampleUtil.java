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

import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public final class ExampleUtil {

  public static GridLayout createMainLayout( int numColumns ) {
    GridLayout result = createGridLayout();
    result.makeColumnsEqualWidth = true;
    result.numColumns = numColumns;
    result.marginWidth = 0;
    result.marginHeight = 0;
    result.marginTop = 20;
    result.verticalSpacing = 20;
    result.horizontalSpacing = 20;
    return result;
  }

  public static GridLayout createMainLayout( int numColumns, int marginLeft ) {
    GridLayout result = createMainLayout( numColumns );
    result.marginLeft = marginLeft;
    return result;
  }

  public static GridLayout createColumnLayout() {
    GridLayout result = createGridLayout();
    result.verticalSpacing = 20;
    return result;
  }

  public static GridLayout createGridLayout( int numColumns, boolean makeColumnsEqual ) {
    return createGridLayout( numColumns, makeColumnsEqual, 0, 0 );
  }

  public static GridLayout createGridLayout( int numColumns,
                                             boolean makeColumnsEqual,
                                             int spacing,
                                             int margin )
  {
    GridLayout result = new GridLayout( numColumns, makeColumnsEqual );
    result.horizontalSpacing = spacing;
    result.verticalSpacing = spacing;
    result.marginWidth = margin;
    result.marginHeight = margin;
    return result;
  }

  public static GridLayout createGridLayoutWithOffset( int numColumns,
                                                       boolean makeColumnsEqual,
                                                       int spacing,
                                                       int marginRight,
                                                       int marginTop )
  {
    GridLayout result = new GridLayout( numColumns, makeColumnsEqual );
    result.horizontalSpacing = spacing;
    result.verticalSpacing = spacing;
    result.marginWidth = 0;
    result.marginLeft = 0;
    result.marginRight = marginRight;
    result.marginTop =  marginTop;
    return result;
  }

  public static GridLayout createGridLayout( int numColumns,
                                             boolean makeColumnsEqual,
                                             int spacing,
                                             int marginWidth,
                                             int marginHeight )
  {
    GridLayout result = new GridLayout( numColumns, makeColumnsEqual );
    result.horizontalSpacing = spacing;
    result.verticalSpacing = spacing;
    result.marginWidth = marginWidth;
    result.marginHeight = marginHeight;
    return result;
  }

  public static GridLayout createGridLayout() {
    GridLayout result = new GridLayout();
    result.horizontalSpacing = 0;
    result.verticalSpacing = 0;
    result.marginWidth = 0;
    result.marginHeight = 0;
    result.marginHeight = 0;
    return result;
  }

  public static GridData createHorzFillData() {
    return new GridData( SWT.FILL, SWT.TOP, true, false );
  }

  public static GridData createFillData() {
    return new GridData( SWT.FILL, SWT.FILL, true, true );
  }

  public static void createHeadingLabel( Composite parent, String text, int horizontalSpan ) {
    Label label = new Label( parent, SWT.NONE );
    label.setText( text );
    label.setData(  WidgetUtil.CUSTOM_VARIANT, "heading" );
    GridData labelLayoutData = new GridData();
    labelLayoutData.horizontalSpan = horizontalSpan;
    label.setLayoutData( labelLayoutData );
  }

  private ExampleUtil() {
    // prevent instantiation
  }
}
