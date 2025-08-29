package com.example.bajajqualifier.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateWebhookRequest {
    private String name;
    private String regNo;
    private String email;
}