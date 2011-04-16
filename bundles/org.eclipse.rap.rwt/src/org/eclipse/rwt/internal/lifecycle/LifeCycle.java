/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.lifecycle.PhaseListener;


public abstract class LifeCycle implements ILifeCycle {

  public abstract Scope getScope();

  public abstract void execute() throws IOException;

  public abstract void addPhaseListener( PhaseListener phaseListener );
  public abstract void removePhaseListener( PhaseListener phaseListener );
}
