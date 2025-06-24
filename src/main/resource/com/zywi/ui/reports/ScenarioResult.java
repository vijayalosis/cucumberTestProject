package com.zywi.ui.reports;

import java.util.List;

public class ScenarioResult {
    private String featureName;
    private String scenarioName;
    private List<StepResult> steps;
    private long totalDurationMillis;
    private boolean isFailed;
}
