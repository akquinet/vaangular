/* 
   Licensed under the Apache License, Version 2.0 (the "License"); you may not
   use this file except in compliance with the License. You may obtain a copy of
   the License at
  
     http://www.apache.org/licenses/LICENSE-2.0
  
   Unless required by applicable law or agreed to in writing, software 
   distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
   WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
   License for the specific language governing permissions and limitations under
   the License. 
 */
package de.akquinet.engineering.vaadin.vaangular.demo;

import javax.servlet.Servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.RegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class VaangularApplication extends SpringBootServletInitializer
{

    @Bean
    public RegistrationBean vaangularServlet(final ApplicationContext context)
    {
        final Servlet servlet = new VaangularServlet();
        return new ServletRegistrationBean(servlet, "/*");
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application)
    {
        return application.sources(VaangularApplication.class);
    }

    public static void main(final String... args)
    {
        SpringApplication.run(VaangularApplication.class, args);
    }

}
