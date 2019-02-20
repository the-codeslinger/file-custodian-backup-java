package com.thecodeslinger.filecustodian.database.archive.json;

import static com.thecodeslinger.filecustodian.database.archive.json.JsonConstants.BACKUP_ARCHIVE_CREATED;
import static com.thecodeslinger.filecustodian.database.archive.json.JsonConstants.BACKUP_ARCHIVE_PREVIOUS;
import static com.thecodeslinger.filecustodian.database.archive.json.JsonConstants.BACKUP_ARCHIVE_TYPE;

import java.io.IOException;
import java.time.Instant;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.thecodeslinger.filecustodian.database.archive.BackupArchive;
import com.thecodeslinger.filecustodian.database.archive.BackupArchive.Type;

/**
 * Deserializes {@link BackupArchive} from the following JSON.
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
public class BackupArchiveJsonDeserializer extends StdDeserializer<BackupArchive> {
    private static final long serialVersionUID = -2383094245297091071L;
    
    protected BackupArchiveJsonDeserializer() {
        this(null);
    }

    protected BackupArchiveJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public BackupArchive deserialize(JsonParser parser, DeserializationContext ignored)
            throws IOException, JsonProcessingException {
        ObjectCodec mapper = parser.getCodec();
        JsonNode json = mapper.readTree(parser);
        
        var typeNode = json.get(BACKUP_ARCHIVE_TYPE);
        var createdNode = json.get(BACKUP_ARCHIVE_CREATED);
        var previousNode = json.get(BACKUP_ARCHIVE_PREVIOUS);
        
        if (nodesContainValues(typeNode, createdNode)) {
            var type = Type.of(typeNode.asText());
            var created = Instant.parse(createdNode.asText().replace('_', ':'));
            
            String previous = null;
            if (nodesContainValues(previousNode)) {
                previous = previousNode.asText();
            }
            
            return BackupArchive.create(type, created, previous);
        }
        return null;
    }
    
    private static boolean nodesContainValues(JsonNode... nodes) {
        return Stream.of(nodes).allMatch(JsonNode::isTextual);
    }
}
