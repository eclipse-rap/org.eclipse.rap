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

import org.eclipse.rap.e4.apache.jxpath.Pointer;
import org.eclipse.rap.e4.apache.jxpath.ri.EvalContext;
import org.eclipse.rap.e4.apache.jxpath.ri.compiler.NodeTest;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointer;

/**
 * EvalContext that returns the current node from the parent context if the
 * test succeeds.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 670727 $ $Date: 2008-06-23 15:10:38 -0500 (Mon, 23 Jun 2008) $
 */
public class SelfContext extends EvalContext {
    private NodeTest nodeTest;
    private boolean startedSet = false;
    private NodePointer nodePointer;

    /**
     * Create a new SelfContext.
     * @param parentContext EvalContext
     * @param nodeTest guard
     */
    public SelfContext(EvalContext parentContext, NodeTest nodeTest) {
        super(parentContext);
        this.nodeTest = nodeTest;
    }

    public Pointer getSingleNodePointer() {
        return parentContext.getSingleNodePointer();
    }

    public NodePointer getCurrentNodePointer() {
        if (position == 0 && !setPosition(1)) {
            return null;
        }
        return nodePointer;
    }

    public boolean nextNode() {
        return setPosition(getCurrentPosition() + 1);
    }

    public void reset() {
        super.reset();
        startedSet = false;
    }

    public boolean setPosition(int position) {
        if (position != 1) {
            return false;
        }
        super.setPosition(position);
        if (!startedSet) {
            startedSet = true;
			nodePointer = parentContext.getCurrentNodePointer();
        }

        if (nodePointer == null) {
            return false;
        }

        return nodeTest == null || nodePointer.testNode(nodeTest);
    }
}
