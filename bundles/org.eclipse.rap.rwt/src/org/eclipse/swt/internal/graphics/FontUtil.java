/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;


public class FontUtil {
  
  public static FontData getData( Font font ) {
    return font.getFontData()[ 0 ];
  }
  
  private FontUtil() {
    // prevent instance creation
  }
}
