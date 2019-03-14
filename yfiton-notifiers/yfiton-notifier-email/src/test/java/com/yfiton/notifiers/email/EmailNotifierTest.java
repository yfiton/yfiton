/*
 * Copyright 2016 Laurent Pellegrino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yfiton.notifiers.email;

import com.google.common.collect.ImmutableMap;
import com.yfiton.api.exceptions.ParameterException;
import com.yfiton.core.Yfiton;
import com.yfiton.core.YfitonBuilder;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Ignore;
import org.junit.Test;

import javax.mail.*;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

/**
 * System test that aims to check that email notifier is able to send notifications
 * using real services.
 *
 * @author Laurent Pellegrino
 */
@Ignore
public class EmailNotifierTest {

    @Test
    public void testFree() throws MessagingException, ConfigurationException, ParameterException {
        testSendEmail(
                "smtp.free.fr", "imap.free.fr",
                System.getenv("FREE_EMAIL"),
                System.getenv("FREE_USERNAME"),
                System.getenv("FREE_PASSWORD"));
    }

    @Test
    public void testGmail() throws MessagingException, ConfigurationException, ParameterException {
        // https://www.google.com/settings/security/lesssecureapps
        testSendEmail(
                "gmail", "imap.gmail.com",
                System.getenv("GMAIL_EMAIL"),
                System.getenv("GMAIL_USERNAME"),
                System.getenv("GMAIL_PASSWORD"));
    }

    @Test
    public void testOutlook() throws MessagingException, ConfigurationException, ParameterException {
        // http://pchelp.ricmedia.com/how-to-fix-550-5-3-4-requested-action-not-taken-error/
        testSendEmail(
                "outlook", "imap-mail.outlook.com",
                System.getenv("OUTLOOK_EMAIL"),
                System.getenv("OUTLOOK_USERNAME"),
                System.getenv("OUTLOOK_PASSWORD"));
    }


    private void testSendEmail(String smtpFqdn, String imapFqdn, String email, String username, String password) throws ConfigurationException, ParameterException, MessagingException {
        String subject = createSubject();

        sendEmail(smtpFqdn, email, username, password, subject);

        assertThat(checkEmailReception(subject, imapFqdn, username, password)).isTrue();
    }

    private String createSubject() {
        String uuid = UUID.randomUUID().toString();
        return "[Yfiton-system-test] " + uuid;
    }

    private void sendEmail(String smtpFqdn, String email, String username, String password, String subject) throws ConfigurationException, ParameterException {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
        builder.put("host", smtpFqdn);
        builder.put("username", username);
        builder.put("password", password);
        builder.put("from", "noreply@yfiton.com");
        builder.put("to", email);
        builder.put("subject", subject);
        builder.put("body", "Yfiton auto generated email for testing purposes.");

        sendEmail(builder.build());
    }

    private void sendEmail(Map<String, String> properties) throws ConfigurationException, ParameterException {
        Yfiton yfiton = new YfitonBuilder(new EmailNotifier()).displayStackTraces().build();
        yfiton.notify(properties);
    }

    private boolean checkEmailReception(String subject, String host, String username, String password) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session session = Session.getInstance(properties);

        Store store = null;

        try {
            store = session.getStore("imaps");
            store.connect(host, username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.getMessages();

            for (Message message : messages) {
                if (message.getSubject().equals(subject)) {
                    message.setFlag(Flags.Flag.DELETED, true);
                    return true;
                }
            }

            inbox.close(true);
        } finally {
            try {
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

}
