/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;

/**
 * Utility class that helps to avoid redundant implementations
 * of asking for the lifecycle phase of a given event.
 * 
 * @see ILifeCycle
 * @see PhaseListener
 * @see PhaseEvent
 * @see PhaseId
 * @since 1.4
 */
public class PhaseListenerUtil {

  /**
   * Checks whether the given event was thrown in the read data lifecycle
   * phase.
   * @return true if the givent event was thrown in the read data lifecycle
   *         phase, false otherwise.
   */
  public static boolean isReadData( PhaseEvent event ) {
    return event.getPhaseId() == PhaseId.READ_DATA;
  }
  
  /**
   * Checks whether the given event was thrown in the process action lifecycle
   * phase.
   * @return true if the givent event was thrown in the process action lifecycle
   *         phase, false otherwise.
   */
  public static boolean isProcessAction( PhaseEvent event ) {
    return event.getPhaseId() == PhaseId.PROCESS_ACTION;
  }

  /**
   * Checks whether the given event was thrown in the render lifecycle
   * phase.
   * @return true if the givent event was thrown in the render lifecycle
   *         phase, false otherwise.
   */
  public static boolean isRender( PhaseEvent event ) {
    return event.getPhaseId() == PhaseId.RENDER;
  }

  /**
   * Checks whether the given event was thrown in the prepare UI root lifecycle
   * phase.
   * @return true if the givent event was thrown in the prepare UI root lifecycle
   *         phase, false otherwise.
   */
  public static boolean isPrepareUIRoot( PhaseEvent event ) {
    return event.getPhaseId() == PhaseId.PREPARE_UI_ROOT;
  }
  
  private PhaseListenerUtil() {
    // prevent instance creation
  }
}
