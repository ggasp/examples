package com.emarsys.e3.api.example;

/**
 * Provides the necessary information for establishment of a connection to the emarsys BMAPI.
 *
 * @author Alex Kraml <kraml@emarsys.com>
 */
public interface ClientConfiguration {

    /**
     * The base URL for all request to the emarsys Batch Mailing API for the configured account.
     * @return
     */
    String getBMAPIBaseURL();

    /**
     * The user name which is used to authenticate against the emarsys BMAPI.
     * @return
     */
    String getBMAPIUsername();

/**
     * The password hash which is used to authenticate against the emarsys BMAPI.
     * @return
     */
    String getBMAPIPasswordHash();

    /**
     * The remote host where the recipient data files are copied to via SFTP.
     * @return
     */
    String getScpRemoteHost();

    /**
     * The port the SCP service listens to.
     * @return
     */
    int getScpPort();

    /**
     * The SCP user.
     * @return
     */
    String getScpUsername();

    /**
     * The SCP password.
     * @return
     */
    String getScpPassword();

    /**
     * The remote directory for all files to be copied via SCP.
     * @return
     */
    String getScpRemoteRootDirectory();

    /**
     * The path to the local recipients file which needs to be transferred via SCP.
     * @return
     */
    String getLocalRecipientsFilePath();
    
    /**
    * the Domain that should be used for sending the batch ** should be pre-installed on your account
    * @return 
    */
    String getDomain();
}
