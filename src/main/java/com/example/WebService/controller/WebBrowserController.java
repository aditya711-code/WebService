package com.example.WebService.controller;

import org.apache.catalina.connector.InputBuffer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RestController
public class WebBrowserController {

    @GetMapping("/start")
    public void startBrowser(@RequestParam("browser")String browser, @RequestParam("url")String url)throws IOException{

        System.out.println("URL: "+url);
        ProcessBuilder processBuilder;
        if (browser.equalsIgnoreCase("Microsoft Edge")) {
            System.out.println("Browser edge");
            processBuilder=new ProcessBuilder("cmd","/c","start msedge",url);
        }
        else {
            processBuilder=new ProcessBuilder("cmd", "/c", "start", url);
        }
        System.out.println("Opening browser "+browser+" with"+url);
        processBuilder.start();
    }

    @GetMapping("/stop")
    public void stopBrowser(@RequestParam("browser")String browser) throws IOException{

        System.out.println("browser"+browser);
        ProcessBuilder processBuilder ;

        if(browser.equalsIgnoreCase("Google Chrome")){
            processBuilder=new ProcessBuilder("taskkill", "/F", "/IM", "chrome.exe");

        }
        else {
            processBuilder=new ProcessBuilder("taskkill","/F","/IM","msedge.exe");

        }
        Process process=processBuilder.start();





        try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {


            String line;
            while ((line = stdInput.readLine()) != null) {
                System.out.println("Output: " + line);
            }


            while ((line = stdError.readLine()) != null) {
                System.out.println("Error: " + line);
            }
        }

        System.out.println("Stopping browser: " + browser);

    }

    @GetMapping("/getUrl")
    public String getActiveTabUrl(@RequestParam("browser")String browser)throws IOException{
        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command",
                "$edge = Get-Process msedge | Where-Object { $_.MainWindowTitle -ne '' } | Select-Object -First 1; if ($edge) { $edge.MainWindowTitle } else { 'No active tabs found.' }");

        Process process=processBuilder.start();

        InputStream inputStream=process.getInputStream();


        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
        String activeTabUrl= bufferedReader.readLine();

        bufferedReader.close();

        System.out.println("Active browser: "+activeTabUrl);

        return activeTabUrl;
    }
}
