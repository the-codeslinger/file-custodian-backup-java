package com.thecodeslinger.filecustodian.database.archive.json;

import static com.thecodeslinger.filecustodian.database.archive.json.JsonConstants.*;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.thecodeslinger.filecustodian.database.archive.BackupArchive;

/**
 * Serializes {@link BackupArchive} to the following JSON.
 * 
 * <pre><code>
 * {
 *   "type": [String],
 *   "created": [String: ISO_INSTANT],
 *   "previous": [String: nullable]
 * }
 * </code></pre>
 *
 * @author Robert Lohr
 * @since 2019-02-17
 */
public class BackupArchiveJsonSerializer extends StdSerializer<BackupArchive> {
    private static final long serialVersionUID = -8316290052171289255L;

    protected BackupArchiveJsonSerializer() {
        this(null);
    }
    
    protected BackupArchiveJsonSerializer(Class<BackupArchive> type) {
        super(type);
    }

    @Override
    public void serialize(BackupArchive value, JsonGenerator gen, SerializerProvider ignored) 
            throws IOException {
        gen.writeStartObject();
        
        gen.writeStringField(BACKUP_ARCHIVE_TYPE, value.getType().text);
        gen.writeStringField(BACKUP_ARCHIVE_CREATED, value.getCreated().toString());
        
        if (BackupArchive.Type.FULL == value.getType()) {
            gen.writeNullField(BACKUP_ARCHIVE_PREVIOUS);
        }
        else {
            gen.writeStringField(BACKUP_ARCHIVE_PREVIOUS, value.getPrevious());
        }
        
        gen.writeEndObject();
    }
    
}
