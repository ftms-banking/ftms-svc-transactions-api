package ftms.svc.transactions.api.config;

import ftms.svc.transactions.api.filter.RequestValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RequestFilterConfig {

    @Bean
    @Profile("!test")
    public FilterRegistrationBean<RequestValidationFilter> requestValidationFilter() {
        FilterRegistrationBean<RequestValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestValidationFilter());
        registrationBean.addUrlPatterns("/api/v1/transactions/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}