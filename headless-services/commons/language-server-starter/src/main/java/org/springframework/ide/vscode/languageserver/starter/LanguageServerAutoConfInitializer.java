package org.springframework.ide.vscode.languageserver.starter;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessorRegistrar;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.ide.vscode.commons.languageserver.completion.VscodeCompletionEngineAdapter;
import org.springframework.ide.vscode.commons.languageserver.config.LanguageServerInitializer;
import org.springframework.ide.vscode.commons.languageserver.config.LanguageServerProperties;
import org.springframework.ide.vscode.commons.languageserver.reconcile.DiagnosticSeverityProvider;
import org.springframework.ide.vscode.commons.languageserver.util.DefinitionHandler;
import org.springframework.ide.vscode.commons.languageserver.util.DocumentSymbolHandler;
import org.springframework.ide.vscode.commons.languageserver.util.SimpleLanguageServer;
import org.springframework.ide.vscode.commons.languageserver.util.SimpleTextDocumentService;

public class LanguageServerAutoConfInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME,
				SlimConfigurationClassPostProcessor.class, () -> new SlimConfigurationClassPostProcessor());
		AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
		context.registerBean(ConditionService.class,
				() -> new SimpleConditionService(context, context.getBeanFactory(), context.getEnvironment(), context));
		if (context.getBeanFactory().getBeanNamesForType(ConfigurationPropertiesAutoConfiguration.class).length == 0) {
			new ConfigurationPropertiesBindingPostProcessorRegistrar().registerBeanDefinitions(null, context);
		}
		if (context.getBeanFactory().getBeanNamesForType(PropertyPlaceholderAutoConfiguration.class).length == 0) {
			context.registerBean(PropertyPlaceholderAutoConfiguration.class,
					() -> new PropertyPlaceholderAutoConfiguration());
			ConditionService conditions = context.getBeanFactory().getBean(ConditionService.class);
			if (conditions.matches(PropertyPlaceholderAutoConfiguration.class,
					PropertySourcesPlaceholderConfigurer.class)) {
				context.registerBean("propertySourcesPlaceholderConfigurer", PropertySourcesPlaceholderConfigurer.class,
						() -> context.getBean(PropertyPlaceholderAutoConfiguration.class)
								.propertySourcesPlaceholderConfigurer());
			}
		}
		if (context.getBeanFactory().getBeanNamesForType(LanguageServerAutoConf.class).length == 0) {
			context.registerBean(LanguageServerAutoConf.class, () -> new LanguageServerAutoConf());
			ConditionService conditions = context.getBeanFactory().getBean(ConditionService.class);
			if (conditions.matches(LanguageServerAutoConf.class, SimpleLanguageServer.class)) {
				context.registerBean("languageServer", SimpleLanguageServer.class, () -> {
					try {
						return context.getBean(LanguageServerAutoConf.class).languageServer(
								context.getBean(LanguageServerProperties.class),
								Optional.ofNullable(
										context.getBeanProvider(DiagnosticSeverityProvider.class).getIfAvailable()),
								Optional.ofNullable(
										context.getBeanProvider(VscodeCompletionEngineAdapter.CompletionFilter.class)
												.getIfAvailable()));
					} catch (Exception e) {
						throw new IllegalStateException(e);
					}
				});
			}
			if (conditions.matches(LanguageServerAutoConf.class, InitializingBean.class)) {
				context.registerBean("initializer", InitializingBean.class,
						() -> context.getBean(LanguageServerAutoConf.class).initializer(
								context.getBean(SimpleLanguageServer.class),
								context.getBean(LanguageServerInitializer.class)));
			}
			context.registerBean("documents", SimpleTextDocumentService.class, () -> context
					.getBean(LanguageServerAutoConf.class).documents(context.getBean(SimpleLanguageServer.class)));
			if (conditions.matches(LanguageServerAutoConf.class, InitializingBean.class)) {
				context.registerBean("registerDefintionHandler", InitializingBean.class,
						() -> context.getBean(LanguageServerAutoConf.class).registerDefintionHandler(
								context.getBean(SimpleTextDocumentService.class),
								context.getBeanProvider(DefinitionHandler.class).stream()
										.collect(Collectors.toList())));
			}
			if (conditions.matches(LanguageServerAutoConf.class, InitializingBean.class)) {
				context.registerBean("registerDocumentSymbolHandler", InitializingBean.class,
						() -> context.getBean(LanguageServerAutoConf.class).registerDocumentSymbolHandler(
								context.getBean(SimpleTextDocumentService.class),
								context.getBean(DocumentSymbolHandler.class)));
			}
			context.registerBean(LanguageServerProperties.class, () -> new LanguageServerProperties());
		}
	}
}

class SlimConfigurationClassPostProcessor
		implements BeanDefinitionRegistryPostProcessor, BeanClassLoaderAware, PriorityOrdered {

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 1;
	}

	public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
	}

}
