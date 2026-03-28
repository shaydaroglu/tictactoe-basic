package com.sercan.tictactoe_basic.common;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestLoggerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    @Override
    public void beforeTestExecution(@NonNull ExtensionContext context) {
        System.out.println("[TEST START] " + testName(context));
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        String status = context.getExecutionException().isPresent() ? "FAILED" : "PASSED";
        System.out.println("[TEST " + status + "] " + testName(context));
    }

    private String testName(ExtensionContext context) {
        return context.getRequiredTestClass().getSimpleName() + "." + context.getDisplayName();
    }
}
