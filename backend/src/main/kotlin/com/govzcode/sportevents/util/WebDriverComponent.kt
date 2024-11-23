package com.govzcode.sportevents.util

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class WebDriverComponent {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private lateinit var driver: WebDriver

    fun run(func: (WebDriver) -> Unit) {
        initDriver()
        func(driver)
        quitDriver()
    }

    private fun initDriver() {
        WebDriverManager.chromedriver().setup()
        val options = ChromeOptions()
        options.addArguments("--headless=new")
        options.addArguments("--disable-gpu")
        options.addArguments("--no-sandbox")

        logger.info("Инициализация WebDriver...")
        this.driver = ChromeDriver(options)
    }

    private fun quitDriver() {
        this.driver.quit()
        logger.info("Закрываем WebDriver.")
    }
}