#/bin/bash
#tips : exit error code are free between 2 and 64 ( so can use 3 to 63)

# Make sure only root can run our script
if [ "$(id -u)" != "0" ]; then
        echo "$0 This script must be run as root" 1>&2
        exit 1
fi

BASE_FOLDER=/opt/RYC_Maintenance
#in case of executing the script in a différent folder, we are we access ressource as relative path
cd $BASE_FOLDER

. bin/config
. bin/common_function

case $1 in
        "backupDB")
                echo " backup DB..."

                ensureFolderExists $BACKUP_FOLDER
                ensureFolderExists $BACKUP_FOLDER/DB
                #because postgres must write in
                chmod 0777 $BACKUP_FOLDER/DB
                
                cd $BACKUP_FOLDER/DB 
                su -c 'pg_dump '$DB_NAME' | xz -e9c > last-db.xz' postgres 
                cp -a last-db.xz $NOW-db.xz

                rotateBackup DB
                exit 0
                ;;
        "backupGen")
                echo " backup gen dir..."

                ensureFolderExists $BACKUP_FOLDER
                ensureFolderExists $BACKUP_FOLDER/gen

                assertFolderExists $GEN_DIR

                # Compresses the gen folder into an XZ file.
                if [ "$(ls -A "$GEN_DIR")" ]; then

                        #must be in the gen and execute tar in it otherwise tar archive all the dir structure
                        cd $GEN_DIR/..
                        tar -Jcvf $BACKUP_FOLDER/gen/last-gen.tar.xz gen 
                        #must now copy the generated archive  to the right location
                        cd $BACKUP_FOLDER/gen 
                        #use "last" copy name instead of directly named with the name to easely get the last backup from dev computer with a script                      
                        cp -a last-gen.tar.xz $NOW-gen.tar.xz
                        rotateBackup gen

                else
                        echo "nothing to do, gen directory is empty "
                       
                fi
            
                exit $?
                ;;
        "rotateBackup")
                case $2 in
                        "DB")
                                rotateBackup DB $BACKUPS_TO_KEEP
                        ;;
                        "gen")
                                rotateBackup gen $BACKUPS_TO_KEEP
                        ;;
                        *)
                        echo "bad second argument : $2 . Use DB or gen ."
                         exit 3
                        ;;
                esac        
                ;;
        "restoreGen")
                if [ ! -n $2 ]
                then
                        echo "specify backup not implemented yet"
                        exit 5
                else
                        echo "restoring last gen backup..."
                        assertFolderExists $BACKUP_FOLDER/gen
                        assertFolderExists $WEBAPPS_FOLDER/ROOT
                        ensureFolderExists $WEBAPPS_FOLDER/ROOT/gen
                        chown tomcat:tomcat $WEBAPPS_FOLDER/ROOT/gen
                        
                        tar -Jxvf $BACKUP_FOLDER/gen/last.tar.xz -C $WEBAPPS_FOLDER/ROOT/gen
                fi
                ;;
        "restoreDB")
                if [ ! -n $2 ]
                then
                        echo "specify backup not implemented yet"
                        exit 5
                else
                        echo "restoring last DB backup..."
                        assertFolderExists $BACKUP_FOLDER/DB
                        #restart to force disconnection of user
                        service postgresql restart
                        #wait until server start inscrease if necessary
                        sleep 15
                        cd $BACKUP_FOLDER/DB
                        su -c 'dropdb '$DB_NAME'' postgres
                        su -c 'createdb -O '$DB_USER' '$DB_NAME' ' postgres
                        xz -d last.xz 
                        psql -d $DB_NAME -U $DB_USER -f last
                        xz -e9 last
                fi
                ;; 
        "deploy")
                echo "deploying application ..."
                ensureFolderExists $BACKUP_FOLDER
                #TODO make special command like force checkout , dev , verbose , debug , etc... 
                #echo "write a special command or leave blank for a normal deploy"
                #TEMP=
                #read TEMP
                case TEMP in
                        *)

                                . $BIN_FOLDER/switch_httpd 
                                switch_httpd dep

                                . $BIN_FOLDER/checkout-build
                                checkout_build
                                
                                #the precedent script change his working and we do not want to hardcode the emplacement of the config file
                                cd $BASE_FOLDER
                                #backup gen folder in case of...
                                ./maintenance.sh backupGen
                                
                                . $BIN_FOLDER/do_deployment
                                do_deployment

                                . $BIN_FOLDER/start_server
                                start_server
                                
                                . $BIN_FOLDER/switch_httpd
                                switch_httpd prod

                                ./maintenance.sh restoreGen
                        ;;
                esac
                ;;
                
        *)
                echo "bad argument : $1"
                exit 3
                ;;
esac
