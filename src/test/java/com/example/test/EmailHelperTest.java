package com.example.test;

import com.example.common.EmailHelper;
import com.example.config.Environment;
import com.example.models.TestResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmailHelperTest {

    @Test
    void emailHelperSendsEmail() {
        if (!Environment.sendEmail)
            return;
        List<TestResult> results = new ArrayList<>();
        results.add(TestResult.builder()
                .testResult(true)
                .applicationName("Google")
                .url("https://www.google.com")
                .build());
        results.add(TestResult.builder()
                .testResult(false)
                .applicationName("Facebook")
                .url("https://www.facebook.com")
                .build());
        EmailHelper helper = new EmailHelper(results, LocalDateTime.now().toString(), LocalDateTime.now().toString());
        String result = helper.sendEmail();
        Assert.assertEquals(result.substring(0, 23), "250 2.0.0 Ok: queued as");
    }
}
