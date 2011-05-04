/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.internal.graphics.FontUtil;


public class MeasurementUtil {
  
  public static int getProbeCount() {
    return RWTFactory.getProbeStore().getSize();
  }
  
  public static String getStartupProbeCode() {
    return TextSizeUtilFacade.getStartupProbeCode();
  }
  
  static void addItemToMeasure( String toMeasure, Font font, int wrapWidth ) {
    FontData fontData = FontUtil.getData( font );
    MeasurementItem newItem = new MeasurementItem( toMeasure, fontData, wrapWidth );
    MeasurementOperator.getInstance().addItemToMeasure( newItem );
  }
  
  private MeasurementUtil() {
    // prevent instance creation
  }
}