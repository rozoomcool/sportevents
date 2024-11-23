package config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import component.WebDriverComponent
import org.springframework.context.annotation.ComponentScan

@Configuration
//@Import(WebDriverComponent::class)
@ComponentScan("component", "service")
class AppConfig {
    @Bean
    fun a(webDriverComponent: WebDriverComponent): String {
        webDriverComponent.run { driver ->  println(driver.toString()) }
        return "a"
    }
}