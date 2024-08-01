/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.rap.e4.apache.jxpath.ri.axes;

import org.eclipse.rap.e4.apache.jxpath.ri.EvalContext;
import org.eclipse.rap.e4.apache.jxpath.ri.compiler.NodeTest;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointer;

/**
 * EvalContext that walks the "ancestor::" and "ancestor-or-self::" axes.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class AncestorContext extends EvalContext {
    private NodeTest nodeTest;
    private boolean setStarted = false;
    private NodePointer currentNodePointer;
    private boolean includeSelf;

    /**
     * Create a new AncestorContext.
     * @param parentContext represents the previous step on the path
     * @param  includeSelf differentiates between "ancestor::" and
     *                     "ancestor-or-self::" axes
     * @param nodeTest is the name of the element(s) we are looking for
     */
    public AncestorContext(
        EvalContext parentContext,
        boolean includeSelf,
        NodeTest nodeTest) {
        super(parentContext);
        this.includeSelf = includeSelf;
        this.nodeTest = nodeTest;
    }

    public NodePointer getCurrentNodePointer() {
        return currentNodePointer;
    }

    public int getDocumentOrder() {
        return -1;
    }

    public void reset() {
        super.reset();
        setStarted = false;
    }

    public boolean setPosition(int position) {
        if (position < getCurrentPosition()) {
            reset();
        }

        while (getCurrentPosition() < position) {
            if (!nextNode()) {
                return false;
            }
        }
        return true;
    }

    public boolean nextNode() {
        if (!setStarted) {
            setStarted = true;
            currentNodePointer = parentContext.getCurrentNodePointer();
            if (includeSelf && currentNodePointer.testNode(nodeTest)) {
                position++;
                return true;
            }
        }

        while (true) {
            currentNodePointer = currentNodePointer.getImmediateParentPointer();

            if (currentNodePointer == null) {
                return false;
            }

            if (currentNodePointer.testNode(nodeTest)) {
                position++;
                return true;
            }
        }
    }
}
