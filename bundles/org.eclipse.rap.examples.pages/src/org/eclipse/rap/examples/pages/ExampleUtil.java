/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.swt.layout.GridLayout;


public final class ExampleUtil {

  public static GridLayout createGridLayout( final int numColumns,
                                             final boolean makeColumnsEqualWidth )
  {
    return createGridLayout( numColumns, makeColumnsEqualWidth, 0, 0 );
  }

  public static GridLayout createGridLayout( final int numColumns,
                                             final boolean makeColumnsEqualWidth,
                                             final int margin,
                                             final int spacing )
  {
    GridLayout result = new GridLayout( numColumns, makeColumnsEqualWidth );
    result.horizontalSpacing = spacing;
    result.verticalSpacing = spacing;
    result.marginWidth = margin;
    result.marginHeight = margin;
    return result;
  }
}
