package com.emarsys.e3.api.example;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Provides the necessary information for establishment of a connection to the
 * emarsys API.
 *
 * @author Alexander Kraml <kraml@emarsys.com>
 */
public interface ClientConfiguration {

    /**
     * The user name which is used to authenticate against the emarsys BMAPI.
     */
    String getApiUsername();

    /**
     * The password hash which is used to authenticate against the emarsys BMAPI.
     */
    String getApiPasswordHash();

    /**
     * The base URL for all request to the emarsys Batch Mailing API for the
     * configured account.
     */
    URL getApiBaseURL();

    /**
     * the Domain that should be used for sending the batch ** should be
     * pre-installed on your account
     */
    String getLinkDomain();

    /**
     * The senders ID
     */
    String getSenderId();

    /**
     * The senders name
     */
    String getSenderName();

    /**
     * The senders address
     */
    String getSenderAddress();

    /**
     * The path to the local recipients file which needs to be transferred.
     */
    File getLocalRecipientFile(int num);

    /**
    * the Fields we want to use for the recipients
    */
    List<RecipientField> getFields();
}
