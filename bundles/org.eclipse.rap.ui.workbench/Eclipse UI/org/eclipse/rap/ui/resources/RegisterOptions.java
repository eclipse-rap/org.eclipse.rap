/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.resources;

/**
 * <p>An enumeration used to specify additional behavior when registering
 * a resource.</p>
 * <ul>
 *    <li><code>NONE</code> - no further action is taken.</li>
 *    <li><code>VERSION</code> - the resource to be registered will be versioned.</li>
 *    <li><code>COMPRESS</code> - the resource to be registered will be
 *      compressed, assuming it contains JavaScript code.</li>
 *    <li><code>VERSION_AND_COMPRESS</code> - the resource to be registered will
 *      be versioned and compressed.</li>
 * </ul>
 *
 * @since 1.0
 */
public final class RegisterOptions {

  /**
   * <code>NONE</code> - no further action is taken.
   */
  public static final RegisterOptions NONE
    = new RegisterOptions( "none" );

  /**
   * <code>VERSION</code> - the resource to be registered will be versioned.
   */
  public static final RegisterOptions VERSION
    = new RegisterOptions( "version" );

  /**
   * <code>COMPRESS</code> - the resource to be registered will be
   * compressed, assuming it contains JavaScript code.
   */
  public static final RegisterOptions COMPRESS
    = new RegisterOptions( "compress" );

  /**
   * <code>VERSION_AND_COMPRESS</code> - the resource to be registered will
   * be versioned and compressed.
   */
  public static final RegisterOptions VERSION_AND_COMPRESS
    = new RegisterOptions( "version_and_compress" );

  private static RegisterOptions[] INTERNAL_VALUES = new RegisterOptions[] {
    NONE, VERSION, COMPRESS, VERSION_AND_COMPRESS };

  /**
   * Returns all available <code>RegisterOptions</code>.
   *
   * @return array of available <code>RegisterOptions</code>
   */
  public static RegisterOptions[] values() {
    RegisterOptions[] result = new RegisterOptions[ INTERNAL_VALUES.length ];
    System.arraycopy( INTERNAL_VALUES, 0, result, 0, INTERNAL_VALUES.length );
    return result;
  }

  private final String name;

  private RegisterOptions( String name ) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}