:: This script is downloading the last "gen" folder backup from the production server.

SET EXE_LOCATION="C:\Program Files (x86)"\PuTTY
SET DEST_FOLDER=.
SET BACKUP_FOLDER=/opt/RYC_Maintenance/backup/gen
SET BACKUP_FILE=last.tar.xz
SET USER=e2admin
SET HOST=enseignementdeux.be
	
%EXE_LOCATION%\pscp %USER%@%HOST%:%BACKUP_FOLDER%/%BACKUP_FILE% %DEST_FOLDER%
	
PAUSE
