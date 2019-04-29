package org.springframework.ide.vscode.boot.app;

import java.lang.Override;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.ide.vscode.boot.java.annotations.AnnotationHierarchyAwareLookup;
import org.springframework.ide.vscode.boot.java.utils.SymbolCache;

public class SpringSymbolIndexerConfigInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
  @Override
  public void initialize(GenericApplicationContext context) {
    if (context.getBeanFactory().getBeanNamesForType(SpringSymbolIndexerConfig.class).length==0) {
      context.registerBean(SpringSymbolIndexerConfig.class, () -> new SpringSymbolIndexerConfig());
      context.registerBean("symbolProviders", AnnotationHierarchyAwareLookup.class, () -> context.getBean(SpringSymbolIndexerConfig.class).symbolProviders(context.getBean(SymbolCache.class)));
    }
  }
}
