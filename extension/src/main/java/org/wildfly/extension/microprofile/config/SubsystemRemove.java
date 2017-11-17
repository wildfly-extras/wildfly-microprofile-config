package org.wildfly.extension.microprofile.config;

import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

/**
 * Handler responsible for removing the subsystem resource from the model
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
class SubsystemRemove extends AbstractRemoveStepHandler {

    static final SubsystemRemove INSTANCE = new SubsystemRemove();


    private SubsystemRemove() {
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model) throws OperationFailedException {
        context.removeService(ServiceNames.CONFIG_PROVIDER);
        context.removeService(ServiceNames.JSR_CONFIG_PROVIDER);
    }


}
