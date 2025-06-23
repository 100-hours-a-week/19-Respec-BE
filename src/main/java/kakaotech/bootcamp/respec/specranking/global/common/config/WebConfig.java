package kakaotech.bootcamp.respec.specranking.global.common.config;

import java.util.Arrays;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToJobFieldConverter());
    }

    private static class StringToJobFieldConverter implements Converter<String, JobField> {
        @Override
        public JobField convert(String source) {
            try {
                return JobField.valueOf(source);
            } catch (IllegalArgumentException e) {
                return Arrays.stream(JobField.values())
                        .filter(jobField -> jobField.getValue().equals(source))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Unknown JobField: " + source));
            }
        }
    }
}
