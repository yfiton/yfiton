/*
 * Copyright 2015 Laurent Pellegrino
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

import com.yfiton.api.Notifier;
import com.yfiton.api.annotation.Parameter;
import com.yfiton.api.exceptions.NotificationException;
import com.yfiton.api.exceptions.ValidationException;
import com.yfiton.api.parameter.Parameters;
import com.yfiton.api.parameter.validators.Validator;
import com.yfiton.api.utils.Console;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * This notifier allows to send emails using the desired server.
 *
 * @author lpellegr
 */
public class EmailNotifier extends Notifier {

    @Parameter(description = "Specify author of the message.", required = true)
    private String from;

    @Parameter(name = "to", description = "Address(es) of the primary recipient(s) of the message.", required = true)
    private List<String> recipients;

    @Parameter(description = "The addresses of others who are to receive the message, though the content of the message may not be directed at them.")
    private List<String> cc;

    @Parameter(description = "Addresses of recipients of the message whose addresses are not to be revealed to other recipients of the message.")
    private List<String> bcc;

    @Parameter(validator = SubjectValidator.class, description = "Short string identifying the topic of the message", required = true)
    private String subject;

    @Parameter(description = "The content of the message.")
    private String body = "";

    @Parameter(description = "The username to use if authentication is required.")
    private String username;

    @Parameter(description = "The password to use if authentication is required.", hidden = true)
    private String password;

    @Parameter(description = "Fully qualified domain name (FQDN) of SMTP service. It can be a FQDN or a keyword among [gmail, outlook] for loading predefined configurations.", required = true)
    private String host;

    @Parameter(description = "The port number of the mail server")
    private int port = 587;

    @Parameter(description = "Define whether authentification must be used. When enabled, username and password must also be set")
    private boolean auth = true;

    @Parameter(name = "starttls.enable", description = "If true, enables the use of the STARTTLS command (if supported by the server) to switch the connection to a TLS-protected connection before issuing any login commands.")
    private boolean enableStartTls = false;

    @Parameter(name = "ssl.trust", description = "If set, and a socket factory hasn't been specified, enables use of a MailSSLSocketFactory. If set to \"*\", all hosts are trusted. If set to a whitespace separated list of hosts, those hosts are trusted. Otherwise, trust depends on the certificate the server presents.")
    private String trustSsl = "*";

    public EmailNotifier() {

    }

    private EmailNotifier(boolean auth, List<String> bcc, String body, List<String> cc, boolean enableStartTls, String from, String host, int port, List<String> recipients, String subject, String trustSsl, String username, String password) {
        this.auth = auth;
        this.bcc = bcc;
        this.body = body;
        this.cc = cc;
        this.enableStartTls = enableStartTls;
        this.from = from;
        this.host = host;
        this.password = password;
        this.port = port;
        this.recipients = recipients;
        this.subject = subject;
        this.trustSsl = trustSsl;
        this.username = username;
    }

    @Override
    protected Check checkParameters(Parameters parameters) {
        if (auth) {
            if (username == null) {
                Check.failed("Missing required username");
            } else {
                if (password == null) {
                    String value = Console.readParameterValueFromStdin("password");

                    if (value == null) {
                        Check.failed("Missing required password");
                    } else {
                        password = value;
                    }
                }
            }
        }

        return Check.succeeded();
    }

    @Override
    protected void notify(Parameters parameters) throws NotificationException {
        Properties props = new Properties();

        if (log.isDebugEnabled()) {
            props.put("mail.debug", "true");
        }

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", Boolean.toString(enableStartTls));
        props.put("mail.smtp.ssl.trust", trustSsl);

        loadConfigurationForWellKnownServices(props);

        // accept to override all mail.xxx properties
        props.putAll(parameters.nameStartingWith("mail."));

        Session session = Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);
        Transport transport = null;

        try {
            if (bcc != null) {
                for (String email : bcc) {
                    message.addRecipients(Message.RecipientType.BCC, email);
                }
            }

            if (cc != null) {
                for (String email : cc) {
                    message.addRecipients(Message.RecipientType.CC, email);
                }
            }

            if (from != null) {
                message.addFrom(new Address[]{new InternetAddress(from)});
            }

            for (String recipient : recipients) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }

            message.setSubject(subject);
            message.setContent(body, "text/plain");

            transport = session.getTransport("smtp");

            log.debug("Connecting to " + host + " using SMTP protocol");
            transport.connect(username, password);

            log.debug("Sending message to recipients");
            transport.sendMessage(message, message.getAllRecipients());
        } catch (AddressException e) {
            throw new NotificationException(e);
        } catch (MessagingException e) {
            throw new NotificationException(e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    throw new NotificationException(e);
                }
            }
        }
    }

    private void loadConfigurationForWellKnownServices(Properties props) {
        String hostname = null;

        if (host.equals("gmail")) {
            hostname = "smtp.gmail.com";
        } else if (host.equals("outlook")) {
            hostname = "smtp-mail.outlook.com";
        }

        if (hostname != null) {
            props.put("mail.smtp.host", hostname);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", hostname);
        }
    }

    @Override
    public String getKey() {
        return "email";
    }

    @Override
    public String getName() {
        return "Email";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Send an email to the specified recipient(s).\nThe notifier is based on the Java Mail API\nhttps://javamail.java.net/nonav/docs/api/");
    }

    @Override
    public Optional<String> getUrl() {
        return Optional.empty();
    }

    public static final class SubjectValidator implements Validator<String> {

        @Override
        public void validate(String parameterName, String parameterValue) throws ValidationException {
            if (parameterValue.length() > 78) {
                throw new ValidationException(
                        "The specified subject is too long: " + parameterValue.length() + " characters specified but 78 allowed");
            }
        }

    }

    public static class Builder {

        private boolean auth = true;
        private String from;
        private List<String> recipients;
        private List<String> cc;
        private List<String> bcc;
        private String subject;
        private String body = "";
        private String username;
        private String password;
        private String host;
        private int port = 587;
        private boolean enableStartTls = false;
        private String trustSsl = "*";

        public Builder() {
        }

        public Builder setAuth(boolean auth) {
            this.auth = auth;
            return this;
        }

        public Builder setFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder addRecipient(String recipient) {
            if (this.recipients == null) {
                this.recipients = new ArrayList<>();
            }

            this.recipients.add(recipient);
            return this;
        }

        public Builder setRecipients(List<String> recipients) {
            this.recipients = recipients;
            return this;
        }

        public Builder addCc(String cc) {
            if (this.cc == null) {
                this.cc = new ArrayList<>();
            }

            this.cc.add(cc);
            return this;
        }

        public Builder setCc(List<String> cc) {
            this.cc = cc;
            return this;
        }

        public Builder addBcc(String bcc) {
            if (this.bcc == null) {
                this.bcc = new ArrayList<>();
            }

            this.bcc.add(bcc);
            return this;
        }

        public Builder setBcc(List<String> bcc) {
            this.bcc = bcc;
            return this;
        }

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setEnableStartTls(boolean enableStartTls) {
            this.enableStartTls = enableStartTls;
            return this;
        }

        public Builder setTrustSsl(String trustSsl) {
            this.trustSsl = trustSsl;
            return this;
        }

        public EmailNotifier build() {
            EmailNotifier emailNotifier = new EmailNotifier(auth, bcc, body, cc, enableStartTls, from, host, port, recipients, subject, trustSsl, username, password);
            return emailNotifier;
        }

    }

}
