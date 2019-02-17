package com.thecodeslinger.filecustodian.database.content;

/**
 * Represents the "content.json" of an individual backup. In it, all backed-up files are referenced
 * at all times, independent of the type of backup. That means that even an incremental backup, 
 * where only a few files have been archived, all files of previous backups are included as well. 
 *
 * @author Robert Lohr
 * @since 2019-02-17
 */
public class ContentFile {
    
}
