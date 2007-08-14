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
package org.eclipse.rwt.internal.browser;


/**
 * <p>
 * the implementation for Mozilla 1.7 and higher.
 * </p>
 */
public class Mozilla1_7up extends Mozilla1_6up {

  public Mozilla1_7up( final boolean scriptEnabled ) {
    super( scriptEnabled );
  }

  public Mozilla1_7up( final boolean scriptEnabled, final boolean ajaxEnabled ) {
    super( scriptEnabled, ajaxEnabled );
  }

  public Mozilla1_7up( final Browser browser ) {
    super( browser );
  }
}
