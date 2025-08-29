package com.example.bajajqualifier.model;

import lombok.Data;

@Data
public class GenerateWebhookResponse {
    private String webhookURL;
    private String accessToken;
}