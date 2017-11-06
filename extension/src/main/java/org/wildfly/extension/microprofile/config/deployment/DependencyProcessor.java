package org.wildfly.extension.microprofile.config.deployment;

import org.jboss.as.ee.weld.WeldDeploymentMarker;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.annotation.CompositeIndex;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.logging.Logger;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;

/**
 * Add dependencies required by deployment unit to access the Config API (programmatically or using CDI).
 */
public class DependencyProcessor implements DeploymentUnitProcessor {

    Logger log = Logger.getLogger(DependencyProcessor.class);

    /**
     * See {@link Phase} for a description of the different phases
     */
    public static final Phase PHASE = Phase.DEPENDENCIES;

    /**
     * The relative order of this processor within the {@link #PHASE}.
     * The current number is large enough for it to happen after all
     * the standard deployment unit processors that come with JBoss AS.
     */
    public static final int PRIORITY = 0x4000;

    public static final ModuleIdentifier MICROPROFILE_CONFIG_API = ModuleIdentifier.create("org.eclipse.microprofile.config.api");
    public static final ModuleIdentifier JAVAX_CONFIG_API = ModuleIdentifier.create("javax.config.api");
    public static final ModuleIdentifier MICROPROFILE_CONFIG_EXTENSION = ModuleIdentifier.create("org.wildfly.extension.microprofile.config");

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

        addDependencies(deploymentUnit);
    }

    @Override
    public void undeploy(DeploymentUnit context) {
    }

    private void addDependencies(DeploymentUnit deploymentUnit) {
        final CompositeIndex index = deploymentUnit.getAttachment(Attachments.COMPOSITE_ANNOTATION_INDEX);
        final ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);
        final ModuleLoader moduleLoader = Module.getBootModuleLoader();

        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, MICROPROFILE_CONFIG_API, false, false, true, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, JAVAX_CONFIG_API, false, false, true, false));

        if (WeldDeploymentMarker.isPartOfWeldDeployment(deploymentUnit)) {
            boolean addConfigExtension = false;
            for (final MicroProfileConfigAnnotations annotation : MicroProfileConfigAnnotations.values()) {
                if (!index.getAnnotations(annotation.getDotName()).isEmpty()) {
                    ConfigMarkers.markMicroProfileConfig(deploymentUnit);
                    addConfigExtension = true;
                    break;
                }
            }

            for (final JavaxConfigAnnotations annotation : JavaxConfigAnnotations.values()) {
                if (!index.getAnnotations(annotation.getDotName()).isEmpty()) {
                    ConfigMarkers.markJavaxConfig(deploymentUnit);
                    addConfigExtension = true;
                    break;
                }
            }
            if (addConfigExtension) {
                moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, MICROPROFILE_CONFIG_EXTENSION, false, false, true, false));
            }
        }
    }
}
