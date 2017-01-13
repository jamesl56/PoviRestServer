package com.antwish.povi.server.utils.aws;

import java.io.IOException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.antwish.povi.server.db.mysql.DBUtilities;
import com.antwish.povi.server.db.mysql.SymmetricEncryptionUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsEmailClient {
    private static Logger _log = LoggerFactory.getLogger(AwsEmailClient.class);
    static final String FROM = "noreply@povi.me";

    static final String AWS_ACCESS_KEY="wfStXr9H7OM/7tX838mibBxZbWqeqWgKkIGMx/I+iRs=";
    static final String AWS_SECRET_KEY="r4ciIhmn1jk+HsF2hxurE2NWg0AfdMoKAA3tn1+s9werYmEZc+N86PRg977XJWIj";
  

    public static boolean sendEmail(String toAddress, String subject, String body) throws IOException {    	
                
        // Construct an object to contain the recipient address.
        Destination destination = new Destination().withToAddresses(toAddress);
        
        // Create the subject and body of the message.
        Content contentSubject = new Content().withData(subject);
        Content textBody = new Content().withData(body); 
        Body contentBody = new Body().withText(textBody);
        
        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(contentSubject).withBody(contentBody);
        
        // Assemble the email.
        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);

        try
        {
            _log.debug("Attempting to send an email through Amazon SES by using the AWS SDK for Java to: " + toAddress);
        
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(SymmetricEncryptionUtility.decrypt(AWS_ACCESS_KEY), SymmetricEncryptionUtility.decrypt(AWS_SECRET_KEY));
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(awsCreds);

            Region REGION = Region.getRegion(Regions.US_EAST_1);
            client.setRegion(REGION);
       
            // Send the email.
            client.sendEmail(request);  
            _log.debug("Email sent successfully to " + toAddress);
            
            return true;
        }
        catch (Exception ex) 
        {
            _log.error("The email was not sent to " + toAddress + " with error: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
            
            return false;
        }
    }
}
