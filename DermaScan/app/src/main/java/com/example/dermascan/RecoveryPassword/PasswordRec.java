package com.example.dermascan.RecoveryPassword;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.util.Random;

public class PasswordRec {
    private String recipient, subject, body, finalbody, code;
    private String host, port, username, password;
    private Properties props;
    private Session session;

    // Constructor
    public PasswordRec(String recipient) {
        this.recipient = recipient;
        this.subject = "Redefinição de Senha - Derma Scan";
        this.body = "Olá " + this.recipient + "\n\nRecebemos uma solicitação para redefinir a senha da sua conta.\n\nCódigo de Verficação: ";
        this.finalbody = "\n\nSe você não solicitou a redefinição de senha, ignore este e-mail com segurança, sua conta continuará protegida. \n\n Dica de segurança: Nunca compartilhe sua senha com ninguém e evite reutilizar senhas em diferentes serviços. \n\n Atenciosamente,\nEquipe DermaScan.";
        this.host = "smtp.gmail.com";
        this.port = "587";
        this.username = "dermaascan@gmail.com";
        this.password = "aukx kpee qciq nhau"; // Substitua pela sua senha ou use OAuth

        this.props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    // Método para gerar um código de recuperação aleatório
    public void generateCode() {
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            codeBuilder.append(random.nextInt(10));
        }
        this.code = codeBuilder.toString();
    }

    // Método para enviar o email
    public void sendEmail() {
        generateCode();
        DeleteInTime();
        // Criando a mensagem de email
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body + this.code + finalbody);

            // Enviando o email
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void DeleteInTime(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                code = null;

            }
        }, 60000*2); // 60.000 ms = 1 minuto

    }
    public String getCode() {
        return code;
    }
}
