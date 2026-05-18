package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.Inscricao;
import br.com.fatec.muttley.Muttley.repository.InscricaoRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificadoService {

    private final InscricaoRepository inscricaoRepository;
    private final RegrasMedalhaService regrasMedalhaService;

    public byte[] gerarCertificado(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Inscrição não encontrada"));

        if (inscricao.getStatus().name().equals("AGENDADO")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Certificado disponível apenas para eventos concluídos");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont fonteBold = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont fonteNormal = PdfFontFactory.createFont("Helvetica");

            document.add(new Paragraph("CERTIFICADO DE PARTICIPAÇÃO")
                    .setFont(fonteBold)
                    .setFontSize(22)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(80));

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Certificamos que")
                    .setFont(fonteNormal)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(inscricao.getParticipante().getNome())
                    .setFont(fonteBold)
                    .setFontSize(20)
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("participou do evento")
                    .setFont(fonteNormal)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(inscricao.getEvento().getDescricao())
                    .setFont(fonteBold)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER));

            String dataFormatada = inscricao.getEvento().getDataEvento()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String horario = inscricao.getEvento().getHoraInicio() + " às " +
                    inscricao.getEvento().getHoraFim();

            document.add(new Paragraph("realizado em " + dataFormatada + " das " + horario)
                    .setFont(fonteNormal)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            document.add(new Paragraph(
                    new Text("Carga horária equivalente: ")
                            .setFont(fonteNormal))
                    .add(new Text(inscricao.getEvento().getPontos() + " pontos")
                            .setFont(fonteBold))
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));

            Double pontosTotais = 0.0;
            List<Inscricao> todasInscricoes = inscricaoRepository
                    .findByParticipanteId(inscricao.getParticipante().getId(),
                            org.springframework.data.domain.Pageable.unpaged()).getContent();
            for (Inscricao i : todasInscricoes) {
                if (i.getStatus().name().equals("CONCLUIDO")) {
                    pontosTotais += i.getEvento().getPontos();
                }
            }
            String medalha = regrasMedalhaService.calcularMedalha(pontosTotais);
            String medalhaTexto = medalha != null ? medalha : "Sem medalha";

            document.add(new Paragraph(
                    new Text("Medalha do semestre: ")
                            .setFont(fonteNormal))
                    .add(new Text(medalhaTexto)
                            .setFont(fonteBold)
                            .setFontColor(ColorConstants.ORANGE))
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n\n\n"));

            document.add(new Paragraph("Mutley — Gestão de Eventos Acadêmicos")
                    .setFont(fonteNormal)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar certificado", e);
        }
    }
}
