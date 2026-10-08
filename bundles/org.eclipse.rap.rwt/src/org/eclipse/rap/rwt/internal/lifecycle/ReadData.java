/*******************************************************************************
 * Copyright (c) 2002, 2018 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.swt.internal.widgets.displaykit.DisplayLCA;
import org.eclipse.swt.widgets.Display;


@SuppressWarnings( "deprecation" )
final class ReadData implements IPhase {

  @Override
  public PhaseId getPhaseId() {
    return PhaseId.READ_DATA;
  }

  @Override
  public PhaseId execute( Display display ) {
    DisplayLCA displayLCA = DisplayUtil.getLCA( display );
    displayLCA.readData( display );
    displayLCA.clearPreserved( display );
    displayLCA.preserveValues( display );
    return PhaseId.PROCESS_ACTION;
  }

}
