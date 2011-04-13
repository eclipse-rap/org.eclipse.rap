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
package org.eclipse.swt.internal.graphics;

import java.io.IOException;

import org.eclipse.rwt.internal.FacadesInitializer;
import org.eclipse.swt.internal.graphics.TextSizeDetermination.ICalculationItem;
import org.eclipse.swt.internal.graphics.TextSizeProbeStore.Probe;

abstract class TextSizeDeterminationFacade {

  private final static TextSizeDeterminationFacade FACADE_IMPL
    = ( TextSizeDeterminationFacade )FacadesInitializer.load( TextSizeDeterminationFacade.class );

  static String createMeasureString( String string, boolean expandLineDelimitors ) {
    return FACADE_IMPL.createMeasureStringInternal( string, expandLineDelimitors );
  }

  static ICalculationItem[] writeStringMeasurements() throws IOException {
    return FACADE_IMPL.writeStringMeasurementsInternal();
  }

  static Probe[] writeFontProbing() throws IOException {
    return FACADE_IMPL.writeFontProbingInternal();
  }

  static String getStartupProbeCode() {
    return FACADE_IMPL.getStartupProbeCodeInternal();
  }

  public abstract String createMeasureStringInternal( String string, boolean expandNewLines );

  public abstract ICalculationItem[] writeStringMeasurementsInternal() throws IOException;

  public abstract Probe[] writeFontProbingInternal() throws IOException;

  public abstract String getStartupProbeCodeInternal();
}
