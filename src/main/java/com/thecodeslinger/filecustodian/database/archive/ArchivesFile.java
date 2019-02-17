package com.thecodeslinger.filecustodian.database.archive;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the "archives.json" file that describes all full and incremental backups archives of 
 * a backup archive.
 * 
 * <p>
 * For more clarity, here's are definitions for the wordings used in the sources.
 * <ul>
 *   <li>
 *     <i>Backup Archive:</i> Defines a full or an incremental backup. Contains the individual files
 *     and folders that have been archived. 
 *   </li>
 *   <li>
 *     <i>Backup:</i> Describes a complete history of all the files and folders that a user wants to
 *     have archived over a period of time. A period is represented by a single Backup Archive,
 *     which is a snapshot of the files and folders at a certain time.
 *   </li>
 *   <li>
 *     <i>Backup Operation:</i> The task of creating a single Backup Archive.
 *   </li>
 * </ul>
 * </p>
 *
 * @author Robert Lohr
 * @since 2019-02-17
 */
public class ArchivesFile {
    private static Logger log = LoggerFactory.getLogger(ArchivesFile.class);
    
    public List<BackupArchive> getList() {
        throw new UnsupportedOperationException();
    }
    
    public void add(BackupArchive archive) {
        throw new UnsupportedOperationException();
    }
    
    public void remove(BackupArchive archive) {
        throw new UnsupportedOperationException();
    }
}
