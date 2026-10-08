/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import java.io.IOException;

import org.eclipse.swt.internal.widgets.displaykit.DisplayLCA;
import org.eclipse.swt.widgets.Display;


@SuppressWarnings( "deprecation" )
final class Render implements IPhase {

  @Override
  public PhaseId getPhaseId() {
    return PhaseId.RENDER;
  }

  @Override
  public PhaseId execute( Display display ) throws IOException {
    if( display != null ) {
      DisplayLCA displayLCA = DisplayUtil.getLCA( display );
      displayLCA.render( display );
      displayLCA.clearPreserved( display );
    }
    return null;
  }

}
