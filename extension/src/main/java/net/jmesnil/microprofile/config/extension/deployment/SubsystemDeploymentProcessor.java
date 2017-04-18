package net.jmesnil.microprofile.config.extension.deployment;

import java.util.List;

import net.jmesnil.microprofile.config.extension.ConfigSourceService;
import net.jmesnil.microprofile.config.WildFlyConfigBuilder;
import net.jmesnil.microprofile.config.WildFlyConfigProviderResolver;
import net.jmesnil.microprofile.config.inject.ConfigExtension;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.as.ee.weld.WeldDeploymentMarker;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.weld.deployment.WeldPortableExtensions;
import org.jboss.logging.Logger;
import org.jboss.modules.Module;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;

/**
 */
public class SubsystemDeploymentProcessor implements DeploymentUnitProcessor {

    Logger log = Logger.getLogger(SubsystemDeploymentProcessor.class);

    /**
     * See {@link Phase} for a description of the different phases
     */
    public static final Phase PHASE = Phase.POST_MODULE;

    /**
     * The relative order of this processor within the {@link #PHASE}.
     * The current number is large enough for it to happen after all
     * the standard deployment unit processors that come with JBoss AS.
     */
    public static final int PRIORITY = 0x4000;

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        Module module = deploymentUnit.getAttachment(Attachments.MODULE);

        WildFlyConfigBuilder builder = new WildFlyConfigBuilder();
        builder.forClassLoader(module.getClassLoader())
                .addDefaultSources()
                .addDiscoveredSources();
        addConfigSourcesFromServices(builder, phaseContext.getServiceRegistry());
        Config config = builder.build();

        WildFlyConfigProviderResolver.INSTANCE.registerConfig(config, module.getClassLoader());

        if (WeldDeploymentMarker.isPartOfWeldDeployment(deploymentUnit)) {
            WeldPortableExtensions extensions = WeldPortableExtensions.getPortableExtensions(deploymentUnit);
            extensions.registerExtensionInstance(new ConfigExtension(), deploymentUnit);
        }

    }

    private void addConfigSourcesFromServices(ConfigBuilder builder, ServiceRegistry serviceRegistry) {
        List<ServiceName> serviceNames = serviceRegistry.getServiceNames();
        for (ServiceName serviceName: serviceNames) {
            if (ConfigSourceService.SERVICE_NAME.isParentOf(serviceName)) {
                ServiceController<?> service = serviceRegistry.getService(serviceName);
                ConfigSource configSource = ConfigSource.class.cast(service.getValue());
                builder.withSources(configSource);
            }
        }
    }

    @Override
    public void undeploy(DeploymentUnit context) {
        Module module = context.getAttachment(Attachments.MODULE);
        WildFlyConfigProviderResolver.INSTANCE.releaseConfig(ConfigProvider.getConfig(module.getClassLoader()));
    }
}
