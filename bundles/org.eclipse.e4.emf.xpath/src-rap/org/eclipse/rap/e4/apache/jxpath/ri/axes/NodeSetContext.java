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

import org.eclipse.rap.e4.apache.jxpath.NodeSet;
import org.eclipse.rap.e4.apache.jxpath.ri.EvalContext;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointer;

/**
 * A simple context that is based on a {@link NodeSet}.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class NodeSetContext extends EvalContext {
    private boolean startedSet = false;
    private NodeSet nodeSet;

    /**
     * Create a new NodeSetContext.
     * @param parentContext parent context
     * @param nodeSet associated NodeSet
     */
    public NodeSetContext(EvalContext parentContext, NodeSet nodeSet) {
        super(parentContext);
        this.nodeSet = nodeSet;
    }

    public NodeSet getNodeSet() {
        return nodeSet;
    }

    public NodePointer getCurrentNodePointer() {
        if (position == 0 && !setPosition(1)) {
            return null;
        }
        return (NodePointer) nodeSet.getPointers().get(position - 1);
    }

    public boolean setPosition(int position) {
        super.setPosition(position);
        return position >= 1 && position <= nodeSet.getPointers().size();
    }

    public boolean nextSet() {
        if (startedSet) {
            return false;
        }
        startedSet = true;
        return true;
    }

    public boolean nextNode() {
        return setPosition(position + 1);
    }
}
