package com.treco.dex.api.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DescriptionService {

    public String generateDescription(String objectName, String habitatName, String environmentName) {
        log.info("Generating description for object: {} in {}@{}", objectName, habitatName, environmentName);
        return "Object '" + objectName + "' is cataloged in the " + habitatName + " habitat within the " + environmentName + " environment.";
    }
}
