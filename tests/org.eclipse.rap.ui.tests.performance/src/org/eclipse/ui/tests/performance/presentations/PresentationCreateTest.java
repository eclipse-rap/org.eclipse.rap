/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.tests.performance.presentations;

import org.eclipse.ui.presentations.AbstractPresentationFactory;
import org.eclipse.ui.tests.performance.TestRunnable;
import org.eclipse.ui.tests.performance.layout.PresentationWidgetFactory;

public class PresentationCreateTest extends PresentationPerformanceTest {
    
    private int type;
    private int number;
    private AbstractPresentationFactory factory;

    public PresentationCreateTest(AbstractPresentationFactory factory, int type, int number) {
        this(factory, type, number, "creation");
    }
    
    public PresentationCreateTest(AbstractPresentationFactory factory, int type, int number, String message) {
        super(PresentationWidgetFactory.describePresentation(factory, type) + " " + message);
        this.type = type;
        this.number = number;
        this.factory = factory;
    }
    
    protected void runTest() throws Throwable {
        exercise(new TestRunnable() {
            public void run() throws Exception {
                
                startMeasuring();
                
                PresentationTestbed testbed = createPresentation(factory, type, number);
                processEvents();
                testbed.getControl().dispose();
                processEvents();
                
                stopMeasuring();
            } 
        });
        
        commitMeasurements();
        assertPerformance();    
    }
}
