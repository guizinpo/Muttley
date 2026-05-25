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

        String linkedinUrl = "https://www.linkedin.com/profile/add"
                + "?startTask=CERTIFICATION_NAME"
                + "&name=" + nomeEvento.replace(" ", "%20")
                + "&organizationName=Fatec"
                + "&issueYear=" + inscricao.getEvento().getDataEvento().getYear()
                + "&issueMonth=" + inscricao.getEvento().getDataEvento().getMonthValue();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDestinatario);
            helper.setSubject("Certificado de Participação — " + nomeEvento);
            helper.setText(
                    "<h2>Olá, " + nomeParticipante + "!</h2>" +
                            "<p>Segue em anexo o seu certificado de participação no evento <strong>" +
                            nomeEvento + "</strong>.</p>" +
                            "<p><a href='" + linkedinUrl + "' target='_blank'>" +
                            "Adicionar ao LinkedIn</a></p>" +
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
