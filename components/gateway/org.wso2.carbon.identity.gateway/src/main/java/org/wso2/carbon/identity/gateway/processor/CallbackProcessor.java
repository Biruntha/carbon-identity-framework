/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.gateway.processor;

import org.wso2.carbon.identity.framework.FrameworkException;
import org.wso2.carbon.identity.framework.IdentityProcessor;
import org.wso2.carbon.identity.framework.handler.AbstractHandler;
import org.wso2.carbon.identity.framework.message.Request;
import org.wso2.carbon.identity.framework.message.Response;
import org.wso2.carbon.identity.gateway.context.GatewayMessageContext;
import org.wso2.carbon.identity.gateway.element.callback.GatewayCallbackHandler;
import org.wso2.carbon.identity.gateway.internal.DataHolder;
import org.wso2.carbon.identity.gateway.message.GatewayRequest;
import org.wso2.carbon.identity.gateway.message.GatewayResponse;

/**
 * Handle callbacks coming into the Identity Gateway
 */
public class CallbackProcessor extends IdentityProcessor<GatewayRequest> {


    @Override
    public Response process(GatewayRequest identityRequest) throws FrameworkException {

        // get registered callback handlers.
        GatewayCallbackHandler handler = DataHolder.getInstance().getGatewayCallbackHandlers()
                .stream()
                .filter(x -> x.canExtractSessionIdentifier(identityRequest))
                .findFirst()
                .orElseThrow(() -> new FrameworkException("Unable to find a handler to process the callback"));

        GatewayMessageContext context = new GatewayMessageContext(identityRequest, new GatewayResponse());
        ((AbstractHandler) handler).execute(context);
        return context.getIdentityResponse();
    }

    @Override
    public String getName() {

        return "CallbackProcessor";
    }

    @Override
    public int getPriority() {

        return 50;
    }

    @Override
    public boolean canHandle(Request identityRequest) {
        // if the request url contains identity/callback
//        return identityRequest.getRequestURI().contains("callback");
        return true;
    }


}