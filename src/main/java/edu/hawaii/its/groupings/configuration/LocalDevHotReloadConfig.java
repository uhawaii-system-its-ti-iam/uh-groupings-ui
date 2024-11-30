package edu.hawaii.its.groupings.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

@Configuration
@Profile({"localhost", "dockerhost"})
public class LocalDevHotReloadConfig {
    public LocalDevHotReloadConfig(final TemplateEngine templateEngine) throws IOException {
        final ClassPathResource applicationProp = new ClassPathResource("application.properties");
        if (applicationProp.isFile()) {
            File sourceRoot = applicationProp.getFile().getParentFile();
            while (Objects.requireNonNull(sourceRoot.listFiles((dir, name) -> name.equals("mvnw"))).length != 1) {
                sourceRoot = sourceRoot.getParentFile();
            }
            final FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
            fileTemplateResolver.setPrefix(sourceRoot.getPath() + "/src/main/resources/templates/");
            fileTemplateResolver.setSuffix(".html");
            fileTemplateResolver.setCacheable(false);
            fileTemplateResolver.setCharacterEncoding("UTF-8");
            fileTemplateResolver.setCheckExistence(true);
            templateEngine.setTemplateResolver(fileTemplateResolver);
        }
    }
}