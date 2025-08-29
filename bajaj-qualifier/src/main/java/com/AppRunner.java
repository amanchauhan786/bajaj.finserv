package com.example.bajajqualifier;
import com.example.bajajqualifier.model.GenerateWebhookRequest;
import com.example.bajajqualifier.model.GenerateWebhookResponse;
import com.example.bajajqualifier.model.SubmitSolutionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);
    private final RestTemplate restTemplate;

    public AppRunner(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Application starting the hiring task flow...");

        // === Step 1: Your Personal Details ===
        // ✏️ IMPORTANT: Replace these with your actual details
        String name = "Aman Chauhan"; 
        String regNo = "22BCE0476"; 
        String email = "aman.chauhan2022@vitstudent.ac.in";

        // === Step 2: Generate the webhook ===
        GenerateWebhookResponse webhookDetails = generateWebhook(name, regNo, email);
        if (webhookDetails == null || webhookDetails.getAccessToken() == null || webhookDetails.getWebhookURL() == null) {
            logger.error("Failed to get webhook details. Aborting.");
            return;
        }
        logger.info("Successfully received webhook URL and access token.");

        // === Step 3: Solve the assigned SQL problem ===
        String finalSqlQuery = solveSqlProblem(regNo);
        logger.info("SQL query for the problem has been prepared.");

        // === Step 4: Submit the solution ===
        submitSolution(webhookDetails.getWebhookURL(), webhookDetails.getAccessToken(), finalSqlQuery);
    }

    private GenerateWebhookResponse generateWebhook(String name, String regNo, String email) {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        GenerateWebhookRequest requestBody = new GenerateWebhookRequest(name, regNo, email);
        
        try {
            logger.info("Sending request to generate webhook...");
            return restTemplate.postForObject(url, requestBody, GenerateWebhookResponse.class);
        } catch (Exception e) {
            logger.error("Error while generating webhook: ", e);
            return null;
        }
    }

    private String solveSqlProblem(String regNo) {
        // Based on your regNo ending in '76' (Even), this will solve Question 2.
        return "WITH RankedEmployees AS ("
             + "SELECT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME AS \"LAST NAME\", d.DEPARTMENT_NAME, "
             + "ROW_NUMBER() OVER (PARTITION BY e.DEPARTMENT ORDER BY e.DOB DESC) as age_rank "
             + "FROM EMPLOYEE e JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID) "
             + "SELECT EMP_ID, FIRST_NAME, \"LAST NAME\", DEPARTMENT_NAME, age_rank - 1 AS YOUNGER_EMPLOYEES_COUNT "
             + "FROM RankedEmployees ORDER BY EMP_ID DESC;";
    }

    private void submitSolution(String webhookUrl, String accessToken, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);

        SubmitSolutionRequest requestBody = new SubmitSolutionRequest(finalQuery);
        HttpEntity<SubmitSolutionRequest> requestEntity = new HttpEntity<>(requestBody, headers);
        
        try {
            logger.info("Submitting final SQL query to: {}", webhookUrl);
            ResponseEntity<String> response = restTemplate.exchange(webhookUrl, HttpMethod.POST, requestEntity, String.class);
            logger.info("Submission successful! Status: {}, Body: {}", response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            logger.error("Error while submitting solution: ", e);
        }
    }
}