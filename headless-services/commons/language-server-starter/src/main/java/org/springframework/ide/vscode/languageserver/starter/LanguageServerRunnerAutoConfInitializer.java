package org.springframework.ide.vscode.languageserver.starter;

import java.lang.Override;
import java.lang.String;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.ide.vscode.commons.languageserver.LanguageServerRunner;
import org.springframework.ide.vscode.commons.languageserver.config.LanguageServerProperties;
import org.springframework.ide.vscode.commons.languageserver.util.SimpleLanguageServer;

public class LanguageServerRunnerAutoConfInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
  @Override
  public void initialize(GenericApplicationContext context) {
    if (context.getBeanFactory().getBeanNamesForType(LanguageServerRunnerAutoConf.class).length==0) {
      context.registerBean(LanguageServerRunnerAutoConf.class, () -> new LanguageServerRunnerAutoConf());
      context.registerBean("serverApp", LanguageServerRunner.class, () -> context.getBean(LanguageServerRunnerAutoConf.class).serverApp(BeanFactoryAnnotationUtils.qualifiedBeanOfType(context, String.class, "serverName"),context.getBean(LanguageServerProperties.class),context.getBean(SimpleLanguageServer.class)));
    }
  }
}
