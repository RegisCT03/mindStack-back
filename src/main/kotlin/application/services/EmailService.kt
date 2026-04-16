package com.MindStack.application.services

import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import java.util.Properties

interface IEmailService {
    fun sendOtp(toEmail: String, toName: String, code: String)
}


class SmtpEmailService(
    private val host: String     = System.getenv("SMTP_HOST")     ?: "smtp.gmail.com",
    private val port: String     = System.getenv("SMTP_PORT")     ?: "587",
    private val user: String     = System.getenv("SMTP_USER")     ?: "",
    private val password: String = System.getenv("SMTP_PASSWORD") ?: "",
    private val from: String     = System.getenv("SMTP_FROM")     ?: user
) : IEmailService {

    override fun sendOtp(toEmail: String, toName: String, code: String) {
        val props = Properties().apply {
            put("mail.smtp.auth",            "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host",            host)
            put("mail.smtp.port",            port)
            put("mail.smtp.ssl.trust",       host)
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(user, password)
        })

        val msg = MimeMessage(session).apply {
            setFrom(InternetAddress(from, "MindStack"))
            setRecipient(Message.RecipientType.TO, InternetAddress(toEmail, toName))
            subject = "Tu código de verificación — MindStack"
            setContent(buildHtml(toName, code), "text/html; charset=utf-8")
        }

        Transport.send(msg)
    }

    private fun buildHtml(name: String, code: String) = """
        <!DOCTYPE html>
        <html lang="es">
        <head><meta charset="UTF-8"></head>
        <body style="margin:0;padding:0;background:#f4f6f9;font-family:Arial,sans-serif;">
          <table width="100%" cellpadding="0" cellspacing="0">
            <tr><td align="center" style="padding:40px 16px;">
              <table width="480" cellpadding="0" cellspacing="0"
                     style="background:#fff;border-radius:12px;
                            box-shadow:0 2px 8px rgba(0,0,0,.08);">
                <tr>
                  <td align="center"
                      style="background:#4f46e5;border-radius:12px 12px 0 0;padding:28px 24px;">
                    <h1 style="margin:0;color:#fff;font-size:22px;">🧠 MindStack</h1>
                  </td>
                </tr>
                <tr>
                  <td style="padding:32px 36px;">
                    <p style="margin:0 0 12px;font-size:16px;color:#374151;">
                      Hola, <strong>$name</strong>
                    </p>
                    <p style="margin:0 0 24px;font-size:15px;color:#6b7280;">
                      Ingresa este código para completar tu inicio de sesión.
                      Expira en <strong>10 minutos</strong>.
                    </p>
                    <table width="100%" cellpadding="0" cellspacing="0">
                      <tr>
                        <td align="center"
                            style="background:#f0f0ff;border:2px dashed #4f46e5;
                                   border-radius:10px;padding:20px;">
                          <span style="font-size:38px;font-weight:bold;
                                       letter-spacing:12px;color:#4f46e5;">
                            $code
                          </span>
                        </td>
                      </tr>
                    </table>
                    <p style="margin:24px 0 0;font-size:13px;color:#9ca3af;">
                      Si no solicitaste este código, ignora este correo.
                    </p>
                  </td>
                </tr>
                <tr>
                  <td align="center"
                      style="padding:16px;border-top:1px solid #e5e7eb;
                             font-size:12px;color:#9ca3af;">
                    © ${java.time.Year.now().value} MindStack
                  </td>
                </tr>
              </table>
            </td></tr>
          </table>
        </body>
        </html>
    """.trimIndent()
}