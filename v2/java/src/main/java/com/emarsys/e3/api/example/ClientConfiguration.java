package com.emarsys.e3.api.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the necessary information for establishment of a connection to the emarsys API.
 *
 * @author Alex Kraml <kraml@emarsys.com>
 */
public interface ClientConfiguration {

    /**
     * The user name which is used to authenticate against the emarsys BMAPI.
     * @return
     */
    String getApiUsername();

/**
     * The password hash which is used to authenticate against the emarsys BMAPI.
     * @return
     */
    String getApiPasswordHash();

    /**
     * The base URL for all request to the emarsys Batch Mailing API for the configured account.
     * @return
     */
    String getApiBaseURL();

    /**
    * the Domain that should be used for sending the batch ** should be pre-installed on your account
    * @return
    */
    String getLinkDomain();

    /**
     * The path to the local recipients file which needs to be transferred via SCP.
     * @return
     */
    String getSenderId();

    /**
     * The path to the local recipients file which needs to be transferred via SCP.
     * @return
     */
    String getSenderName();

    /**
     * The path to the local recipients file which needs to be transferred via SCP.
     * @return
     */
    String getSenderAddress();

    /**
     * The path to the local recipients file which needs to be transferred via SCP.
     * @return
     */
    String getLocalRecipientFile(int num);

    /**
    * the Fields we want to use for the recipients
    * @return
    */
    List<RecipientField> getFields();

    /**
     * The remote host where the recipient data files are copied to via SFTP.
     * @return
     */
    String getScpHost();

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
    String getScpDirectory();
}
