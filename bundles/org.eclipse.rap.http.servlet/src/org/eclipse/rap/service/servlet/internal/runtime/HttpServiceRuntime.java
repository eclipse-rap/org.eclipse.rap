/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.eclipse.rap.service.servlet.internal.runtime;

import org.eclipse.rap.service.servlet.internal.runtime.dto.RequestInfoDTO;
import org.eclipse.rap.service.servlet.internal.runtime.dto.RuntimeDTO;
import org.osgi.annotation.versioning.ProviderType;

/**
 * The HttpServiceRuntime service represents the runtime information of a
 * Servlet Whiteboard implementation.
 * <p>
 * It provides access to DTOs representing the current state of the service.
 * <p>
 * The HttpServiceRuntime service must be registered with the
 * {@link HttpServiceRuntimeConstants#HTTP_SERVICE_ENDPOINT} service property.
 *
 * @ThreadSafe
 * @author $Id: c36db83dfe9f7841a18cd9e98c1bdb30a04aaeb1 $
 */
@ProviderType
public interface HttpServiceRuntime {

	/**
	 * Return the runtime DTO representing the current state.
	 * 
	 * @return The runtime DTO.
	 */
	public RuntimeDTO getRuntimeDTO();

	/**
	 * Return a request info DTO containing the services involved with
	 * processing a request for the specified path.
	 * 
	 * @param path The request path, relative to the root of the Servlet Whiteboard
	 *        implementation.
	 * @return The request info DTO for the specified path.
	 */
	public RequestInfoDTO calculateRequestInfoDTO(String path);
}
