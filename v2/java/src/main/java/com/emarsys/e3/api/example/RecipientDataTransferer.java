package com.emarsys.e3.api.example;

import static java.lang.System.err;
import static java.lang.System.out;

import com.sshtools.j2ssh.*;
import com.sshtools.j2ssh.authentication.*;
import com.sshtools.j2ssh.configuration.*;
import com.sshtools.j2ssh.io.*;
import com.sshtools.j2ssh.sftp.*;
import com.sshtools.j2ssh.transport.*;
import java.io.*;

/**
 * RecipientDataTransferer implements an SCP client which is used to
 * transfers the recipient information to the server
 *
 * @author Alex Kraml <kraml@emarsys.com>
 */
final class RecipientDataTransferer {

    private ClientConfiguration config;
    private String batchName;

    public RecipientDataTransferer( ClientConfiguration config, String batchName ) {
        this();
        this.config = config;
        this.batchName = batchName;
    }

    private RecipientDataTransferer() {}

    /**
     * Transfers passed file to the remote host.
     *
     * This method maintains the SSH connection.
     * @param inputFilePath
     * @throws java.io.IOException
     */
    void transferRecipientData( String inputFilePath ) throws IOException {

        SshClient ssh = authenticate();

        if ( null != ssh ) {
            try {
                SftpSubsystemClient sftp = ssh.openSftpChannel();

                scp( inputFilePath, getRemoteFileName(), sftp );
            } finally {
                ssh.disconnect();
            }
        }
    }

    /**
     * Returns the file name for the remote recipient file to be copied to.
     * @return
     */
    String getRemoteFileName() {
        return ( config.getScpDirectory() + "/" + batchName );
    }


    /**
     * Securely copies the File found at inputPath to the targetPath onto the remote node
     * via the passed SFTP client.
     *
     * @param inputPath
     * @param targetPath
     * @param sftp
     * @throws IOException
     */
    private void scp( String inputPath, String targetPath, SftpSubsystemClient sftp ) throws IOException {

        System.out.println( "Copying file " + inputPath + " to " + targetPath );

        // If we don't do this, the user will not have read access...
        FileAttributes attributes = new FileAttributes();

        attributes.setPermissions( new UnsignedInteger32( FileAttributes.S_IRUSR | FileAttributes.S_IWUSR ) );

        SftpFile remoteFile = sftp.openFile(
            targetPath,
            SshFxpOpen.FXF_CREAT | SshFxpOpen.FXF_WRITE,
            attributes
        );

        FileInputStream inputStream = new FileInputStream(inputPath);

        try {

            SftpFileOutputStream remoteOutput = new SftpFileOutputStream( remoteFile );

            try {
                copy( inputStream, remoteOutput );
            } finally {
                remoteOutput.close();
            }

        } finally {
            inputStream.close();
        }
    }

    /**
     * Does a buffered copy of the passed InputStream to the OutputStream.
     *
     * @param inputStream
     * @param outputStream
     * @throws  IOException - in case of any error regarding the copy
     */
    private void copy( InputStream inputStream, OutputStream outputStream ) throws IOException {

        byte[] buffer = new byte[65536];

        int bytesRead;

        do {
            bytesRead = inputStream.read( buffer );

            if ( bytesRead > 0 ) {
                outputStream.write( buffer, 0, bytesRead );
            }
        } while ( bytesRead > -1 );
    }

    /**
     * Authenticate using basic password authentication to our demonstration account.
     *
     * @throws com.sshtools.j2ssh.configuration.ConfigurationException
     * @throws java.io.IOException
     * @return*/
    private SshClient authenticate() throws IOException {

        String remoteHost = config.getScpHost();
        String scpUsername = config.getScpUsername();

        out.println( "Signing on to " + config.getScpHost() + " as " + scpUsername );

        PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();

        pwd.setUsername( scpUsername );
        pwd.setPassword( config.getScpPassword() );

        SshClient newSshClient = new SshClient();

        ConfigurationLoader.initialize( false );

        newSshClient.connect( remoteHost, config.getScpPort(), new IgnoreHostKeyVerification() );

        int authState = newSshClient.authenticate( pwd );

        Boolean authOK = ( AuthenticationProtocolState.COMPLETE == authState );

        if ( ! authOK ) err.println( "Authentication failed to " + remoteHost + " for " + scpUsername );

        return newSshClient;
    }
}
