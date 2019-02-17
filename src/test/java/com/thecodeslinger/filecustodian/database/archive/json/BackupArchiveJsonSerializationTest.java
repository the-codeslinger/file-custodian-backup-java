package com.thecodeslinger.filecustodian.database.archive.json;

import static com.thecodeslinger.filecustodian.database.archive.BackupArchive.Type.FULL;
import static com.thecodeslinger.filecustodian.database.archive.BackupArchive.Type.INCREMENTAL;
import static com.thecodeslinger.filecustodian.database.archive.json.JsonConstants.BACKUP_ARCHIVE_CREATED;
import static com.thecodeslinger.filecustodian.database.archive.json.JsonConstants.BACKUP_ARCHIVE_PREVIOUS;
import static com.thecodeslinger.filecustodian.database.archive.json.JsonConstants.BACKUP_ARCHIVE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thecodeslinger.filecustodian.database.archive.BackupArchive;

/**
 * Validates the serialization and deserialization functionality of {@link BackupArchive}.
 *
 * @author Robert Lohr
 * @since 2019-02-17
 */
public class BackupArchiveJsonSerializationTest {
    private ObjectMapper mapper = new ObjectMapper();
    
    private static final String INSTANT_FULL = "2019-02-17T11:14:42Z";
    private static final String INSTANT_INCR = "2019-02-18T00:00:00Z";
    private static final String FULL_ARCHIVE_NAME = FULL.text + "_" + INSTANT_FULL.replace(':', '_');
    private static final String INCR_ARCHIVE_NAME = INCREMENTAL.text + "_" + INSTANT_INCR.replace(':', '_');
    
    @Test
    public void serialize_full_to_json() {
        // Given
        BackupArchive archive = BackupArchive.create(FULL, Instant.parse(INSTANT_FULL));
        
        // When
        JsonNode json = mapper.valueToTree(archive);
        
        // Then
        // Make sure any change in the JSON breaks the test.
        assertThat(json.size()).isEqualTo(3);
        assertThat(extractedText(json, BACKUP_ARCHIVE_TYPE)).isEqualTo(FULL.text);
        assertThat(extractedText(json, BACKUP_ARCHIVE_CREATED)).isEqualTo(INSTANT_FULL);
        assertThat(json.get(BACKUP_ARCHIVE_PREVIOUS).isNull()).isTrue();
    }
    
    @Test
    public void serialize_incremental_to_json() {
        // Given
        BackupArchive archive = BackupArchive.create(
                INCREMENTAL, Instant.parse(INSTANT_INCR), FULL_ARCHIVE_NAME);
        
        // When
        JsonNode json = mapper.valueToTree(archive);
        
        // Then
        assertThat(json.size()).isEqualTo(3);
        assertThat(extractedText(json, BACKUP_ARCHIVE_TYPE)).isEqualTo(INCREMENTAL.text);
        assertThat(extractedText(json, BACKUP_ARCHIVE_CREATED)).isEqualTo(INSTANT_INCR);
        assertThat(extractedText(json, BACKUP_ARCHIVE_PREVIOUS)).isEqualTo(FULL_ARCHIVE_NAME);
    }
    
    @Test
    public void deserialize_full_from_json() throws IOException {
        // Given
        final String json = String.format(
                "{\"type\":\"%s\", \"created\":\"%s\", \"previous\":null}",
                FULL.text, INSTANT_FULL);

        // When
        BackupArchive archive = mapper.readValue(json,  BackupArchive.class);
        
        // Then
        assertThat(archive.getType()).isEqualTo(FULL);
        assertThat(archive.getCreated()).isEqualTo(Instant.parse(INSTANT_FULL));
        assertThat(archive.getName()).isEqualTo(FULL_ARCHIVE_NAME);
        assertThat(archive.getPrevious()).isNull();
    }
    
    @Test
    public void deserialize_incremental_from_json() throws IOException {
        // Given
        final String json = String.format(
                "{\"type\":\"%s\", \"created\":\"%s\", \"previous\":\"%s\"}",
                INCREMENTAL.text, INSTANT_INCR, FULL_ARCHIVE_NAME);

        // When
        BackupArchive archive = mapper.readValue(json,  BackupArchive.class);
        
        // Then
        assertThat(archive.getType()).isEqualTo(INCREMENTAL);
        assertThat(archive.getCreated()).isEqualTo(Instant.parse(INSTANT_INCR));
        assertThat(archive.getName()).isEqualTo(INCR_ARCHIVE_NAME);
        assertThat(archive.getPrevious()).isEqualTo(FULL_ARCHIVE_NAME);
        
    }
    
    private String extractedText(JsonNode node, String property) {
        return node.get(property).asText();
    }
}
