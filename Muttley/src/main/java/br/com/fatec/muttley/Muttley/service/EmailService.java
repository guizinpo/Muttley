package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.Inscricao;
import br.com.fatec.muttley.Muttley.repository.InscricaoRepository;
import br.com.fatec.muttley.Muttley.entity.Palestrante;
import br.com.fatec.muttley.Muttley.repository.PalestranteRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final CertificadoService certificadoService;
    private final InscricaoRepository inscricaoRepository;
    private final PalestranteRepository palestranteRepository;

    public void enviarCertificado(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Inscrição não encontrada"));

        byte[] pdf = certificadoService.gerarCertificado(inscricaoId);

        String nomeEvento = inscricao.getEvento().getDescricao();
        String nomeParticipante = inscricao.getParticipante().getNome();
        String emailDestinatario = inscricao.getParticipante().getEmail();

        String nomeEventoCodificado;
        String orgCodificada;
        try {
            nomeEventoCodificado = java.net.URLEncoder.encode(nomeEvento, "UTF-8");
            orgCodificada = java.net.URLEncoder.encode("Fatec Zona Leste", "UTF-8");
        } catch (Exception ex) {
            nomeEventoCodificado = nomeEvento.replace(" ", "%20");
            orgCodificada = "Fatec+Zona+Leste";
        }

        String linkedinUrl = "https://www.linkedin.com/profile/add"
                + "?startTask=CERTIFICATION_NAME"
                + "&name=" + nomeEventoCodificado
                + "&organizationName=" + orgCodificada
                + "&issueYear=" + inscricao.getEvento().getDataEvento().getYear()
                + "&issueMonth=" + inscricao.getEvento().getDataEvento().getMonthValue();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDestinatario);
            helper.setSubject("Certificado de Participação — " + nomeEvento);

            String cpfCodificado;
            try {
                cpfCodificado = java.net.URLEncoder.encode(inscricao.getParticipante().getCpf(), "UTF-8");
            } catch (Exception ex) {
                cpfCodificado = inscricao.getParticipante().getCpf();
            }

            String certificadosUrl = "http://localhost:8080/certificados.html?cpf=" + cpfCodificado;

            helper.setText(
                    "<h2>Olá, " + nomeParticipante + "!</h2>" +
                            "<p>Segue em anexo o seu certificado de participação no evento <strong>" +
                            nomeEvento + "</strong>.</p>" +
                            "<p><a href='" + linkedinUrl + "' target='_blank'>Adicionar ao LinkedIn</a></p>" +
                            "<p><a href='" + certificadosUrl + "' target='_blank'>📋 Ver todos os meus certificados</a></p>" +
                            "<br><p>Atenciosamente,<br><strong>Mutley — Gestão de Eventos Acadêmicos</strong></p>",
                    true);

            helper.addAttachment("certificado.pdf", new org.springframework.core.io.ByteArrayResource(pdf));

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail", e);
        }
    }

    public void enviarCertificadoPalestrante(Long palestranteId) {
        Palestrante palestrante = palestranteRepository.findById(palestranteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Palestrante não encontrado"));

        byte[] pdf = certificadoService.gerarCertificadoPalestrante(palestranteId);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(palestrante.getEmail());
            helper.setSubject("Certificado de Palestrante — Mutley");
            helper.setText(
                    "<h2>Olá, " + palestrante.getNome() + "!</h2>" +
                            "<p>Segue em anexo o seu certificado de participação como palestrante nos eventos da Fatec.</p>" +
                            "<br><p>Atenciosamente,<br><strong>Mutley — Gestão de Eventos Acadêmicos</strong></p>",
                    true);

            helper.addAttachment("certificado-palestrante.pdf",
                    new org.springframework.core.io.ByteArrayResource(pdf));

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail", e);
        }
    }
}
