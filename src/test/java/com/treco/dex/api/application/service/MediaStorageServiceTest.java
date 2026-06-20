package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.CreateMediaAssetRequest;
import com.treco.dex.api.api.dto.MediaAssetResponse;
import com.treco.dex.api.domain.model.MediaAsset;
import com.treco.dex.api.domain.model.ObjectSpecies;
import com.treco.dex.api.domain.model.UserAccount;
import com.treco.dex.api.domain.repository.MediaAssetRepository;
import com.treco.dex.api.domain.repository.ObjectSpeciesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaStorageServiceTest {

    @Mock
    private MediaAssetRepository mediaAssetRepository;

    @Mock
    private ObjectSpeciesRepository objectSpeciesRepository;

    @Mock
    private CloudStorageProvider cloudStorageProvider;

    @InjectMocks
    private MediaStorageService mediaStorageService;

    private UUID userId;
    private UUID objectSpeciesId;
    private ObjectSpecies objectSpecies;
    private UserAccount owner;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        objectSpeciesId = UUID.randomUUID();

        owner = UserAccount.builder().id(userId).build();

        objectSpecies = ObjectSpecies.builder()
                .id(objectSpeciesId)
                .owner(owner)
                .build();
    }

    @Test
    void testUploadMediaAsset() {
        CreateMediaAssetRequest request = new CreateMediaAssetRequest();
        request.setUrl("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==");
        request.setMediaType("image/png");

        String expectedUrl = "http://localhost:9000/trecodex-media/test-image.png";

        when(objectSpeciesRepository.findById(objectSpeciesId)).thenReturn(Optional.of(objectSpecies));
        when(cloudStorageProvider.uploadBase64Image(anyString(), anyString(), anyString())).thenReturn(expectedUrl);

        MediaAsset savedAsset = MediaAsset.builder()
                .id(UUID.randomUUID())
                .objectSpecies(objectSpecies)
                .uploadedBy(owner)
                .url(expectedUrl)
                .mediaType("image/png")
                .createdAt(LocalDateTime.now())
                .build();

        when(mediaAssetRepository.save(any(MediaAsset.class))).thenReturn(savedAsset);

        MediaAssetResponse response = mediaStorageService.uploadMediaAsset(objectSpeciesId, request, userId);

        assertNotNull(response);
        assertEquals(expectedUrl, response.getUrl());

        ArgumentCaptor<MediaAsset> assetCaptor = ArgumentCaptor.forClass(MediaAsset.class);
        verify(mediaAssetRepository).save(assetCaptor.capture());

        MediaAsset capturedAsset = assetCaptor.getValue();
        assertEquals(expectedUrl, capturedAsset.getUrl());
        assertEquals("image/png", capturedAsset.getMediaType());
        assertEquals(objectSpecies, capturedAsset.getObjectSpecies());
        assertEquals(owner.getId(), capturedAsset.getUploadedBy().getId());
    }

    @Test
    void testDeleteMediaAsset() {
        UUID mediaAssetId = UUID.randomUUID();
        String fileUrl = "http://localhost:9000/trecodex-media/test-image.png";

        MediaAsset mediaAsset = MediaAsset.builder()
                .id(mediaAssetId)
                .objectSpecies(objectSpecies)
                .uploadedBy(owner)
                .url(fileUrl)
                .mediaType("image/png")
                .build();

        when(mediaAssetRepository.findById(mediaAssetId)).thenReturn(Optional.of(mediaAsset));

        mediaStorageService.deleteMediaAsset(mediaAssetId, userId);

        verify(cloudStorageProvider).deleteImage(fileUrl);
        verify(mediaAssetRepository).delete(mediaAsset);
    }
}
