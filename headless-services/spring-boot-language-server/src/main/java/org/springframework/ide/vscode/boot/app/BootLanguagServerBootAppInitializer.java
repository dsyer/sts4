package org.springframework.ide.vscode.boot.app;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.ide.vscode.boot.java.handlers.RunningAppProvider;
import org.springframework.ide.vscode.boot.java.links.JavaDocumentUriProvider;
import org.springframework.ide.vscode.boot.java.links.JavaElementLocationProvider;
import org.springframework.ide.vscode.boot.java.links.SourceLinks;
import org.springframework.ide.vscode.boot.java.utils.CompilationUnitCache;
import org.springframework.ide.vscode.boot.java.utils.SymbolCache;
import org.springframework.ide.vscode.boot.metadata.AdHocSpringPropertyIndexProvider;
import org.springframework.ide.vscode.boot.metadata.ProjectBasedPropertyIndexProvider;
import org.springframework.ide.vscode.boot.metadata.ValueProviderRegistry;
import org.springframework.ide.vscode.commons.languageserver.util.DocumentEventListenerManager;
import org.springframework.ide.vscode.commons.languageserver.util.SimpleLanguageServer;
import org.springframework.ide.vscode.commons.languageserver.util.SimpleTextDocumentService;
import org.springframework.ide.vscode.commons.util.FileObserver;
import org.springframework.ide.vscode.commons.yaml.ast.YamlASTProvider;
import org.springframework.ide.vscode.commons.yaml.completion.YamlAssistContextProvider;
import org.springframework.ide.vscode.commons.yaml.structure.YamlStructureProvider;
import org.springframework.ide.vscode.languageserver.starter.ConditionService;
import org.yaml.snakeyaml.Yaml;

public class BootLanguagServerBootAppInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
  @Override
  public void initialize(GenericApplicationContext context) {
    if (context.getBeanFactory().getBeanNamesForType(BootLanguagServerBootApp.class).length==0) {
      ConditionService conditions = context.getBeanFactory().getBean(ConditionService.class);
      if (conditions.includes(BootLanguageServerInitializer.class)) {
        context.registerBean(BootLanguageServerInitializer.class, () -> new BootLanguageServerInitializer());
      }
      if (conditions.includes(SpringSymbolIndex.class)) {
        context.registerBean(SpringSymbolIndex.class, () -> new SpringSymbolIndex());
      }
      if (conditions.includes(BootJavaConfig.class)) {
        context.registerBean(BootJavaConfig.class, () -> new BootJavaConfig(context.getBean(SimpleLanguageServer.class)));
      }
      if (conditions.includes(PropertiesJavaDefinitionHandler.class)) {
        context.registerBean(PropertiesJavaDefinitionHandler.class, () -> new PropertiesJavaDefinitionHandler());
      }
      if (conditions.includes(YamlPropertiesJavaDefinitionHandler.class)) {
        context.registerBean(YamlPropertiesJavaDefinitionHandler.class, () -> new YamlPropertiesJavaDefinitionHandler());
      }
      if (conditions.includes(SpringSymbolIndexerConfig.class)) {
        new SpringSymbolIndexerConfigInitializer().initialize(context);
      }
      context.registerBean(BootLsConfigProperties.class, () -> new BootLsConfigProperties());
      context.registerBean(BootLanguagServerBootApp.class, () -> new BootLanguagServerBootApp());
      context.registerBean("serverName", String.class, () -> context.getBean(BootLanguagServerBootApp.class).serverName());
      if (conditions.matches(BootLanguagServerBootApp.class, SymbolCache.class)) {
        context.registerBean("symbolCache", SymbolCache.class, () -> context.getBean(BootLanguagServerBootApp.class).symbolCache(context.getBean(BootLsConfigProperties.class)));
      }
      if (conditions.matches(BootLanguagServerBootApp.class, RunningAppProvider.class)) {
        context.registerBean("runningAppProvider", RunningAppProvider.class, () -> context.getBean(BootLanguagServerBootApp.class).runningAppProvider(context.getBean(SimpleLanguageServer.class)));
      }
      if (conditions.matches(BootLanguagServerBootApp.class, AdHocSpringPropertyIndexProvider.class)) {
        context.registerBean("adHocProperties", AdHocSpringPropertyIndexProvider.class, () -> context.getBean(BootLanguagServerBootApp.class).adHocProperties(context.getBean(BootLanguageServerParams.class),context.getBean(FileObserver.class),context.getBean(DocumentEventListenerManager.class)));
      }
      context.registerBean("fileObserver", FileObserver.class, () -> context.getBean(BootLanguagServerBootApp.class).fileObserver(context.getBean(SimpleLanguageServer.class)));
      context.registerBean("valueProviders", ValueProviderRegistry.class, () -> context.getBean(BootLanguagServerBootApp.class).valueProviders());
      context.registerBean("initializeValueProviders", InitializingBean.class, () -> context.getBean(BootLanguagServerBootApp.class).initializeValueProviders(context.getBean(ValueProviderRegistry.class),BeanFactoryAnnotationUtils.qualifiedBeanOfType(context, ProjectBasedPropertyIndexProvider.class, "adHocProperties"),context.getBean(SourceLinks.class)));
      if (conditions.matches(BootLanguagServerBootApp.class, BootLanguageServerParams.class)) {
        context.registerBean("serverParams", BootLanguageServerParams.class, () -> context.getBean(BootLanguagServerBootApp.class).serverParams(context.getBean(SimpleLanguageServer.class),context.getBean(ValueProviderRegistry.class),context.getBean(BootLsConfigProperties.class)));
      }
      if (conditions.matches(BootLanguagServerBootApp.class, SourceLinks.class)) {
        context.registerBean("sourceLinks", SourceLinks.class, () -> context.getBean(BootLanguagServerBootApp.class).sourceLinks(context.getBean(SimpleLanguageServer.class),context.getBean(CompilationUnitCache.class),context.getBean(BootLanguageServerParams.class)));
      }
      context.registerBean("cuCache", CompilationUnitCache.class, () -> context.getBean(BootLanguagServerBootApp.class).cuCache(context.getBean(BootLanguageServerParams.class),context.getBean(SimpleTextDocumentService.class)));
      context.registerBean("javaDocumentUriProvider", JavaDocumentUriProvider.class, () -> context.getBean(BootLanguagServerBootApp.class).javaDocumentUriProvider());
      context.registerBean("javaElementLocationProvider", JavaElementLocationProvider.class, () -> context.getBean(BootLanguagServerBootApp.class).javaElementLocationProvider(context.getBean(SimpleLanguageServer.class),context.getBean(CompilationUnitCache.class),context.getBean(JavaDocumentUriProvider.class)));
      context.registerBean("yaml", Yaml.class, () -> context.getBean(BootLanguagServerBootApp.class).yaml());
      context.registerBean("yamlAstProvider", YamlASTProvider.class, () -> context.getBean(BootLanguagServerBootApp.class).yamlAstProvider());
      context.registerBean("yamlStructureProvider", YamlStructureProvider.class, () -> context.getBean(BootLanguagServerBootApp.class).yamlStructureProvider());
      context.registerBean("yamlAssistContextProvider", YamlAssistContextProvider.class, () -> context.getBean(BootLanguagServerBootApp.class).yamlAssistContextProvider(context.getBean(BootLanguageServerParams.class),context.getBean(JavaElementLocationProvider.class),context.getBean(SourceLinks.class)));
    }
  }
}
