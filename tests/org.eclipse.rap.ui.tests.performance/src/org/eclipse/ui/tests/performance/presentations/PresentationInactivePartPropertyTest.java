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

public class PresentationInactivePartPropertyTest extends PresentationPerformanceTest {
    
    private int type;
    private int number;
    private AbstractPresentationFactory factory;
    
    public PresentationInactivePartPropertyTest(AbstractPresentationFactory factory, int type, int number) {
        super(PresentationWidgetFactory.describePresentation(factory, type) + " inactive part properties");
        this.type = type;
        this.number = number;
        this.factory = factory;
    }
    
    protected void runTest() throws Throwable {
        final PresentationTestbed testbed = createPresentation(factory, type, number);
        
        final TestPresentablePart part = new TestPresentablePart(theShell, img);
        testbed.add(part);
        
        exercise(new TestRunnable() {
            public void run() throws Exception {
                
                startMeasuring();
                
                for (int counter = 0; counter < 5; counter++) {
                    twiddleProperty(DESCRIPTION, part);
                    twiddleProperty(DIRTY, part);
                    twiddleProperty(IMAGE, part);
                    twiddleProperty(NAME, part);
                    twiddleProperty(TITLE, part);
                    twiddleProperty(TOOLBAR, part);
                    twiddleProperty(TOOLTIP, part);
                }
                
                stopMeasuring();
            } 
        });
        
        commitMeasurements();
        assertPerformance();       
    }
}
