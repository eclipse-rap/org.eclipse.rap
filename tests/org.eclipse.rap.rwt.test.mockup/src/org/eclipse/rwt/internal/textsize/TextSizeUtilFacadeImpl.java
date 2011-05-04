/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;


public final class TextSizeUtilFacadeImpl extends TextSizeUtilFacade {

  public String createMeasurementStringInternal( String string, boolean expandNewLines ) {
    return string;
  }

  public void writeStringMeasurementsInternal() {
  }

  public void writeFontProbingInternal() {
  }

  public String getStartupProbeCodeInternal() {
    return null;
  }
}
