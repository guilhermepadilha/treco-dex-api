package com.treco.dex.api.application.service;

public interface CloudStorageProvider {
    /**
     * Uploads a base64 encoded image and returns the public URL.
     * @param base64Content The base64 encoded image content
     * @param fileName The desired file name
     * @param mediaType The media type (e.g., image/jpeg)
     * @return The URL of the uploaded image
     */
    String uploadBase64Image(String base64Content, String fileName, String mediaType);

    /**
     * Deletes an image from the storage.
     * @param fileUrl The URL of the file to delete
     */
    void deleteImage(String fileUrl);
}
