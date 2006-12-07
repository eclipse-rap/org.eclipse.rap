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
package org.eclipse.rap.rwt;

/**
 * TODO: [fappel] comment
 */
public class RWT {
  
  public static final int NULL = 0;
  public static final int NONE = 0;

  public static final int DEFAULT = -1;
  public static final int HORIZONTAL = 1 << 8;
  public static final int VERTICAL = 1 << 9;
  public static final int CENTER = 1 << 24;
  public static final int UP = 1 << 7;
  public static final int TOP = UP;
  public static final int DOWN = 1 << 10;
  public static final int BOTTOM = DOWN;
  public static final int LEAD = 1 << 14;
  public static final int LEFT = LEAD;
  public static final int TRAIL = 1 << 17;
  public static final int RIGHT = TRAIL;
  public static final int BEGINNING = 1;
  public static final int FILL = 4;
  public static final int KEYCODE_BIT = ( 1 << 24 );
  public static final int END = KEYCODE_BIT + 8;
  public static final int SEPARATOR = 1 << 1;
  public static final int PUSH = 1 << 3;
  public static final int RADIO = 1 << 4;
  public static final int CHECK = 1 << 5;
  public static final int ARROW = 1 << 2;
  public static final int BORDER = 1 << 11;
  public static final int FLAT = 1 << 23;
  public static final int CLOSE = 1 << 6;
  
  // menu flags
  public static final int BAR = 1 << 1;
  public static final int POP_UP = 1 << 3;
  public static final int DROP_DOWN = 1 << 2;
  public static final int CASCADE = 1 << 6;
  
  // text flags
  public static final int SINGLE = 1 << 2;
  public static final int MULTI = 1 << 1;
  public static final int WRAP = 1 << 6;
  public static final int PASSWORD = 1 << 22;
  
  public static final int ERROR_NULL_ARGUMENT = 4;
  public static final int ERROR_INVALID_ARGUMENT = 5;
  public static final int ERROR_INVALID_RANGE = 6;
  public static final int ERROR_CANNOT_BE_ZERO = 7;
  public static final int ERROR_INVALID_PARENT = 32;
  
  public static void error( final int code ) {
  }
}
