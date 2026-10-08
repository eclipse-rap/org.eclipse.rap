/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages.internal;

import org.eclipse.rap.rwt.RWT;

public final class ExamplesMessages {

  private static final String BUNDLE_NAME
    = "org.eclipse.rap.examples.pages.internal.ExamplesMessages";

  public String WhatIsUnicode_Title;
  public String WhatIsUnicode_Descritption;

  private ExamplesMessages() {
    // prevent instantiation
  }

  public static ExamplesMessages get() {
    return RWT.NLS.getUTF8Encoded( BUNDLE_NAME, ExamplesMessages.class );
  }

}
