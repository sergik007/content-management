package com.productiveedge.content_mgmt_automation;

import com.productiveedge.content_mgmt_automation.entity.CommandFlowStrategy;
import com.productiveedge.content_mgmt_automation.flow.Flow;
import com.productiveedge.content_mgmt_automation.flow.exception.InvalidJarRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;


public class MainTest {
    private static final Logger logger = LoggerFactory.getLogger(MainTest.class);

    public static void main(String[] args) {
        try {
            String[] a = {
                    "root_folder_path=C://folder",
                    "process_url=https://www.productiveedge.com/technology/cloud",
                    "process_strategy=true",
                    "max_process_urls_value=2",
                    "process_strange_urls=false",
                    "process_strategy=hello",
                    "generate_report=true",
                    "xlsx_report_name=today_report.xlsx",
                    "report_sheet_name=report",
                    "save_html=true",
                    "save_txt=true",
                    "take_screenshot=false",
                    "page_screen_space_value=550",
                    "browser_name=chrome",
                    "driver_path=C://folder//chromedriver.exe",
                    //"operation_system=MAC",
                    //"browser_path=path",
            };

            Map<String, String> request = makeRequest(a);
            Queue<Flow> flows = CommandFlowStrategy.getFlow(request);
            while (!flows.isEmpty()) {
                Flow flow = flows.poll();
                flow.run();
            }
        } catch (InvalidJarRequestException ex) {
            logger.error(ex.getMessage());
        }
    }

    //works only if the key doesn't have any '='
    private static Map<String, String> makeRequest(String[] args) {
        Map<String, String> request = new HashMap<>();
        for (String arg : args) {
            if (arg.contains("=")) {
                String key = arg.substring(0, arg.indexOf('=')).toUpperCase();
                String value = arg.substring(arg.indexOf('=') + 1);
                request.put(key, value);
            }
        }
        return request;
    }
}
