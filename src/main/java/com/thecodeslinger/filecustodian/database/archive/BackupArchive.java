package com.thecodeslinger.filecustodian.database.archive;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thecodeslinger.filecustodian.database.archive.json.BackupArchiveJsonDeserializer;
import com.thecodeslinger.filecustodian.database.archive.json.BackupArchiveJsonSerializer;

/**
 * Contains all the properties of a single backup archive. An archive is either a full or an
 * incremental backup that has been created in a backup operation.
 * 
 * @see ArchivesFile
 *
 * @author Robert Lohr
 * @since 2019-02-17
 */
@JsonSerialize(using=BackupArchiveJsonSerializer.class)
@JsonDeserialize(using=BackupArchiveJsonDeserializer.class)
public class BackupArchive {
    public static enum Type {
        /**
         * Contains a complete copy of a source folder's contents.
         */
        FULL("full"),
        /**
         * Contains only the files and folders that have been changed since the last backup
         * operation, be it a full or incremental backup.
         */
        INCREMENTAL("inc");
        
        /**
         * Returns a Type from its String representation.
         * 
         * @throws IllegalArgumentException
         *      If {@code text} is unknown.
         */
        public static Type of(String text) {
            var found = Arrays.stream(values())
                    .filter(v -> v.text.equalsIgnoreCase(text))
                    .findFirst();
            return found.orElseThrow(() -> new IllegalArgumentException(
                    text + " is not a valid BackupArchive.Type"));
        }
        
        public final String text;
        Type(String text) {
            this.text = text;
        }
    }
    
    private static final String NAME_FORMAT = "%s_%s";
    private Type type;
    private Instant created;
    private String previous;
    
    /**
     * Convenient alternative to the constructor variant that creates the {@link BackupArchive#name}
     * according to the expected format and validates the input.
     * 
     * <b>Note:</b> This method is only to be used for {@code type=FULL}.
     * 
     * @param type
     *      The type of the backup archive.
     *      
     * @param created
     *      The timestamp in UTC when the archive was created.
     *      
     * @return
     *      Returns a new BackupArchive instance.
     * 
     * @throws IllegalArgumentException
     *      One of the required parameters is null.
     */
    public static BackupArchive create(Type type, Instant created) {
        Assert.isTrue(type == Type.FULL, "BackupArchive.create(Type, Date) only for Type.FULL");
        return new BackupArchive(type, created, null);
    }
    
    /**
     * Convenient alternative to the constructor variant that creates the {@link BackupArchive#name}
     * according to the expected format and validates the input.
     * 
     * @param type
     *      The type of the backup archive.
     *      
     * @param created
     *      The timestamp in UTC when the archive was created.
     *      
     * @param previous
     *      If it's an incremental backup, the name of the archive folder that this backup archive
     *      is based on, be it full or incremental. This must not be null or empty if 
     *      {@code type=INCREMENTAL}.
     *      
     * @return
     *      Returns a new BackupArchive instance.
     * 
     * @throws IllegalArgumentException
     *      One of the required parameters is null.
     */
    public static BackupArchive create(Type type, Instant created, String previous) {
        return new BackupArchive(type, created, previous);
    }
    
    /**
     * Creates an uninitialized instance. This constructor mainly exists for the Jackson parser.
     */
    public BackupArchive() {
    }
    
    /**
     * Create a new BackupArchive with all required properties.
     * 
     * @param type
     *      The type of the backup archive.
     *      
     * @param created
     *      The timestamp in UTC when the archive was created.
     *      
     * @param previous
     *      If it's an incremental backup, the name of the archive folder that this backup archive
     *      is based on, be it full or incremental. This must only be null if {@code type=FULL}.
     */
    protected BackupArchive(Type type, Instant created, String previous) {
        Assert.notNull(type, "type of BackupArchive cannot be null");
        Assert.notNull(created, "created of BackupArchive cannot be null");
        if (type == Type.INCREMENTAL) {
            Assert.hasText(previous, "previous of BackupArchive cannot be null for Type.INCREMENTAL");
        }
        
        this.type = type;
        this.created = created;
        this.previous = previous;
    }

    public Type getType() {
        return type;
    }
    
    public Instant getCreated() {
        return created;
    }
    
    public String getName() {
        var isoInstantString = DateTimeFormatter.ISO_INSTANT.format(created);
        return String.format(NAME_FORMAT, type.text, isoInstantString.replace(':', '_'));
    }
    
    public String getPrevious() {
        return previous;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(created, previous, type);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BackupArchive)) {
            return false;
        }
        var other = (BackupArchive) obj;
        return Objects.equals(created, other.created) && Objects.equals(previous, other.previous) 
                && type == other.type;
    }
}
