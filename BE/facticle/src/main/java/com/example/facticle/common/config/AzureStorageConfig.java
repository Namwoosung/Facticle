package com.example.facticle.common.config;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AzureStorageConfig {


    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.sas-token}")
    private String sasToken;

    @Value("${azure.storage.container-name}")
    private String containerName;


    @Bean
    public BlobServiceClient blobServiceClient() {
        String endpoint = "https://" + accountName + ".blob.core.windows.net";

        log.info("endpoint {}", endpoint);
        log.info("SAS token {}", sasToken);

        return new BlobServiceClientBuilder()
                .endpoint(endpoint)
                .sasToken(sasToken)
                .buildClient();
    }

    @Bean
    public BlobContainerClient blobContainerClient(BlobServiceClient blobServiceClient) {
        return blobServiceClient.getBlobContainerClient(containerName);
    }
}
