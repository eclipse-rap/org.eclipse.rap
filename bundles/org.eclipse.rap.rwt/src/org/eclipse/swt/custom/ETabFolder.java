/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.custom;

import org.eclipse.swt.widgets.Composite;


public class ETabFolder extends CTabFolder {

  public ETabFolder( Composite parent, int style ) {
    super( parent, style );
  }

  public void setWebbyStyle( boolean bool ) {
  }

  public boolean getWebbyStyle() {
    return true;
  }
}
