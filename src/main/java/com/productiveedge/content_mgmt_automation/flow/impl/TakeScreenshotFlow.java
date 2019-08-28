package com.productiveedge.content_mgmt_automation.flow.impl;

import com.productiveedge.content_mgmt_automation.entity.FolderName;
import com.productiveedge.content_mgmt_automation.entity.request.TakeScreenshotRequest;
import com.productiveedge.content_mgmt_automation.flow.Flow;
import com.productiveedge.content_mgmt_automation.flow.impl.helper.GrabAllLinksHelper;
import com.productiveedge.content_mgmt_automation.repository.PageContainer;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static com.productiveedge.content_mgmt_automation.flow.impl.helper.FlowHelper.generateDateFolderName;

public class TakeScreenshotFlow implements Flow {
    private static final Logger logger = LoggerFactory.getLogger(TakeScreenshotFlow.class);
    //Тоже нужно переделать.
    private static final String CHROME_PROPERTY = "webdriver.chrome.driver";
    private static final String JAVASCRIPT_COMMAND = "window.scrollBy(0,?)";
    private static final String GET_HTML_PAGE_HEIGHT_SCRIPT = "return document.body.scrollHeight";

    private WebDriver driver;
    private TakeScreenshotRequest request;

    public TakeScreenshotFlow(TakeScreenshotRequest request) {
        this.request = request;
        System.setProperty(CHROME_PROPERTY, request.getDriverPath());
        String browser = request.getBrowserName().toUpperCase();
        switch (browser) {
            case "CHROME":
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--incognito");
                options.addArguments("--start-maximized");
                options.addArguments("--kiosk");
                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                driver = new ChromeDriver(options);
                break;
            case "SAFARI":
                break;
            case "FIREFOX":
                break;

        }
    }

    @Override
    public synchronized void run() {
        PageContainer.getProcessedPageEntries().forEach(e -> {
            String url = e.getValue().getUrl();
            try {
                driver.get(url);
                TakesScreenshot ts = (TakesScreenshot) driver;
                if (driver instanceof JavascriptExecutor) {
                    JavascriptExecutor jsDriver = (JavascriptExecutor) driver;
                    int pageHeight = Integer.parseInt(jsDriver.executeScript(GET_HTML_PAGE_HEIGHT_SCRIPT).toString());
                    int pageScrollValue = Integer.valueOf(request.getPageScrollValue());
                    int amountScreens = pageHeight / pageScrollValue + 1;
                    String dateFolderName = generateDateFolderName();
                    String domainFolderName = GrabAllLinksHelper.generateNameByKey(e.getKey());
                    for (int i = 0; i < amountScreens; i++) {
                        File source = ts.getScreenshotAs(OutputType.FILE);
                        String fileName = (i + 1) + ".png";
                        File destination = new File(Paths.get(request.getRootFolderPath(), FolderName.SCREEN.name(), dateFolderName, domainFolderName, fileName).toString());
                        FileUtils.copyFile(source, destination);
                        jsDriver.executeScript(JAVASCRIPT_COMMAND.replaceFirst("[?]", request.getPageScrollValue()));
                    }
                    logger.info("Screenshots of site " + e.getValue().getUrl() + " are taken");
                } else {
                    logger.error("Driver can't takes screenshots. Please, change to another one driver type.");
                }
            } catch (IOException ex) {
                logger.error("Can't take screenshot by processUrl " + url + ".\n" + ex.getMessage());
            }
        });
        driver.quit();
    }
}
