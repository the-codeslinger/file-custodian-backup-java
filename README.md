# File Custodian - A simple backup tool

The complete story of how this project came to be can be read on my [website](https://codeslingerhome.wordpress.com/2019/02/11/writing-a-custom-backup-solution/).

## What it is

_Disclaimer_: The following description is talking about the future, of which I already know the outcome today (!), where the application will already be finished.

File Custodian is a simple custom Java backup tool with support for full and incremental backups. For simple backup navigation and total operating system or tool independence, the original file and folder structure is retained at the backup's target location. The files are compressed individually and can be opened by any ZIP container capable application. That means, if you only need access to one file, e.g. to compare the current version with a previous one, you simply navigate to the file with whatever tools suits you most and unpack it. There is, of course, a disaster recovery mode in which you can restore the complete backup with only one command.

You point File Custodian at a folder and tell it to create a backup. That's it. This, on the other hand, also means you as the user are responsible for running the application on a regular basis. It is more work, but it also offers more control. It also means that it's a lean application with no system services that are required to execute a backup or even read the backup archives.

## What it isn't

File Custodian is not an imaging application. It won't create a full drive image that can be used on an empty drive to restore a complete computer with operating system and data.

It's not a background service that is continuously running only for the rare instance where it has to backup your data. You have to run the application yourself - or create cron jobs to automate the process.

## Usage

The following command will run a full backup of all files and folders inside of `Pictures` and create a backup archive under `/mnt/Backup`. Note that no other subfolders for the complete archive itself will be created. In this case `Backup` is the root.

    java -jar file-custodian.jar --full --source /Users/me/Pictures --dest /mnt/Backup

To create an incremental backup call the application with the `--inc` option.

    java -jar file-custodian.jar --inc --source /Users/me/Pictures --dest /mnt/Backup

It will find existing full or incremental backups that are already available at the destination.

For the sake of completeness, here's the command for disaster recovery. I hope no one needs it, but I have tested in anyway.

    java -jar file-custodian --restore --source /mnt/Backup --dest /Users/me/Pictures

As you can see, the `--source` and `--dest` parameters are reversed in this case. Makes sense, right? Just wanted to point it out.