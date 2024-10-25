package com.example.WebService.controller;

import org.apache.catalina.connector.InputBuffer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
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
        else if(browser.equalsIgnoreCase("firefox")){
            processBuilder=new ProcessBuilder("cmd","/c","start firefox",url);
        }
        else {
            processBuilder=new ProcessBuilder("cmd", "/c", "start chrome", url);
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
        else if(browser.equalsIgnoreCase("firefox")){
            processBuilder=new ProcessBuilder("taskkill","/F","/IM","firefox.exe");
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

        ProcessBuilder processBuilder;
        switch (browser.toLowerCase()) {
            case "microsoft edge":
                processBuilder = new ProcessBuilder("powershell.exe", "-Command",
                        "$edge = Get-Process msedge | Where-Object { $_.MainWindowTitle -ne '' } | Select-Object -First 1;" +
                                " if ($edge) { $edge.MainWindowTitle } else { 'No active tabs found.' }");
                break;

            case "google chrome":
                System.out.println("Entering google chrome to get active tab url");
                processBuilder = new ProcessBuilder("powershell.exe", "-Command",
                        "$chrome = Get-Process chrome | Where-Object { $_.MainWindowTitle -match ' - Google Chrome$' } | Select-Object -First 1;" +
                                " if ($chrome) { $chrome.MainWindowTitle } else { 'No active tabs found.' }");
                break;

            case "firefox":
                processBuilder = new ProcessBuilder("powershell.exe", "-Command",
                        "$firefox = Get-Process firefox | Where-Object { $_.MainWindowTitle -ne '' -and $_.MainWindowTitle -notmatch 'Mozilla Firefox' } | Select-Object -First 1;" +
                                " if ($firefox) { $firefox.MainWindowTitle } else { 'No active tabs found.' }");
                break;

            default:
                return "Browser not supported for fetching active tab URL.";
        }

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return Optional.ofNullable(reader.readLine()).orElse("No active tabs found.");
        }
    }
}
