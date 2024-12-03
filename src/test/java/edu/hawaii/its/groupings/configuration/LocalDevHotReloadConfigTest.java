package edu.hawaii.its.groupings.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.io.IOException;

@ActiveProfiles({ "localhost", "dockerhost" })
public class LocalDevHotReloadConfigTest {

    @MockBean
    LocalDevHotReloadConfig localDevHotReloadConfig;

    @Test
    public void testConstructorWithValidTemplateEngine() throws IOException {
        TemplateEngine templateEngine = spy(new TemplateEngine());
        localDevHotReloadConfig = new LocalDevHotReloadConfig(templateEngine);
        verify(templateEngine, times(1)).setTemplateResolver(any(ITemplateResolver.class));
    }

    @Test
    public void testConstructorWithNullTemplateEngine() {
        assertThrows(NullPointerException.class, () -> new LocalDevHotReloadConfig(null));
    }

    @Test
    public void testConstructorConfiguresCorrectTemplateResolverProperties() throws IOException {
        TemplateEngine templateEngine = new TemplateEngine();
        localDevHotReloadConfig = new LocalDevHotReloadConfig(templateEngine);
        ITemplateResolver resolver = templateEngine.getTemplateResolvers().iterator().next();
        assertTrue(resolver instanceof FileTemplateResolver);
        FileTemplateResolver fileResolver = (FileTemplateResolver) resolver;
        assertFalse(fileResolver.isCacheable());
        assertEquals("UTF-8", fileResolver.getCharacterEncoding());
        assertTrue(fileResolver.getCheckExistence());
    }
}
