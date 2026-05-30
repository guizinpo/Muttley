package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.Palestrante;
import br.com.fatec.muttley.Muttley.entity.Evento;
import br.com.fatec.muttley.Muttley.entity.Inscricao;
import br.com.fatec.muttley.Muttley.repository.EventoRepository;
import br.com.fatec.muttley.Muttley.repository.InscricaoRepository;
import br.com.fatec.muttley.Muttley.repository.PalestranteRepository;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import br.com.fatec.muttley.Muttley.repository.MedalhaRepository;
import br.com.fatec.muttley.Muttley.entity.Medalha;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;

import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CertificadoService {

    private final InscricaoRepository inscricaoRepository;
    private final EventoRepository eventoRepository;
    private final PalestranteRepository palestranteRepository;
    private final MedalhaRepository medalhaRepository;

    // Cores do template
    private static final Color AMARELO      = new DeviceRgb(0xF5, 0xC5, 0x18); // amarelo dourado
    private static final Color VERMELHO     = new DeviceRgb(0xA3, 0x1F, 0x26); // vermelho escuro
    private static final Color CINZA_FUNDO  = new DeviceRgb(0xED, 0xED, 0xED); // cinza claro
    private static final Color CINZA_TEXTO  = new DeviceRgb(0x44, 0x44, 0x44); // cinza escuro
    private static final Color PRETO        = new DeviceRgb(0x1A, 0x1A, 0x1A);

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

            // A4 paisagem
            PageSize pageSize = PageSize.A4.rotate();
            pdf.addNewPage(pageSize);

            PdfPage page = pdf.getPage(1);
            PdfCanvas pdfCanvas = new PdfCanvas(page);

            float W = pageSize.getWidth();   // ~841
            float H = pageSize.getHeight();  // ~595

            // ── Fundo cinza claro ──────────────────────────────────────────
            pdfCanvas.setFillColor(CINZA_FUNDO);
            pdfCanvas.rectangle(0, 0, W, H);
            pdfCanvas.fill();

            // ── Canto superior direito — triângulo vermelho grande ─────────
            pdfCanvas.setFillColor(VERMELHO);
            pdfCanvas.moveTo(W, H);
            pdfCanvas.lineTo(W - 120, H);
            pdfCanvas.lineTo(W, H - 120);
            pdfCanvas.closePathFillStroke();

            // ── Canto superior direito — triângulo amarelo menor ───────────
            pdfCanvas.setFillColor(AMARELO);
            pdfCanvas.moveTo(W, H);
            pdfCanvas.lineTo(W - 60, H);
            pdfCanvas.lineTo(W, H - 60);
            pdfCanvas.closePathFillStroke();

            // ── Canto inferior esquerdo — triângulo vermelho grande ────────
            pdfCanvas.setFillColor(VERMELHO);
            pdfCanvas.moveTo(0, 0);
            pdfCanvas.lineTo(120, 0);
            pdfCanvas.lineTo(0, 120);
            pdfCanvas.closePathFillStroke();

            // ── Canto inferior esquerdo — triângulo amarelo menor ──────────
            pdfCanvas.setFillColor(AMARELO);
            pdfCanvas.moveTo(0, 0);
            pdfCanvas.lineTo(60, 0);
            pdfCanvas.lineTo(0, 60);
            pdfCanvas.closePathFillStroke();

            // ── Linha inferior amarela ─────────────────────────────────────
            pdfCanvas.setFillColor(AMARELO);
            pdfCanvas.rectangle(0, 0, W, 8);
            pdfCanvas.fill();

            // ── Linha superior amarela ─────────────────────────────────────
            pdfCanvas.setFillColor(AMARELO);
            pdfCanvas.rectangle(0, H - 8, W, 8);
            pdfCanvas.fill();

            pdfCanvas.release();

            // ── Texto via Document/Canvas ──────────────────────────────────
            PdfFont fonteBold   = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont fonteNormal = PdfFontFactory.createFont("Helvetica");

            Document document = new Document(pdf, pageSize);
            document.setMargins(40, 60, 40, 60);

            // Título espaçado "C E R T I F I C A D O"
            document.add(new Paragraph("C E R T I F I C A D O")
                    .setFont(fonteBold)
                    .setFontSize(36)
                    .setFontColor(AMARELO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30)
                    .setMarginBottom(16));

            // Subtítulo
            document.add(new Paragraph("Este certificado é concedido ao Aluno")
                    .setFont(fonteNormal)
                    .setFontSize(13)
                    .setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(6));

            // Nome do participante
            document.add(new Paragraph(inscricao.getParticipante().getNome())
                    .setFont(fonteBold)
                    .setFontSize(22)
                    .setFontColor(PRETO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(16));

            // Corpo do texto
            String dataFormatada = inscricao.getEvento().getDataEvento()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String descricao = inscricao.getEvento().getDescricao();

            Paragraph corpo = new Paragraph()
                    .add(new Text("Por participar do evento ").setFont(fonteNormal).setFontColor(CINZA_TEXTO))
                    .add(new Text(descricao).setFont(fonteBold).setFontColor(PRETO))
                    .add(new Text(" realizado no dia ").setFont(fonteNormal).setFontColor(CINZA_TEXTO))
                    .add(new Text(dataFormatada).setFont(fonteBold).setFontColor(PRETO))
                    .add(new Text(", promovido pela FATEC Zona Leste.").setFont(fonteNormal).setFontColor(CINZA_TEXTO))
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.JUSTIFIED)
                    .setMarginBottom(10);
            document.add(corpo);

            // Carga horária
            String horas = formatarHoras(inscricao.getEvento().getPontos());
            document.add(new Paragraph("O evento foi realizado com carga horária de " + horas + ".")
                    .setFont(fonteNormal)
                    .setFontSize(13)
                    .setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(20));

            // Data de emissão centralizada
            String hoje = java.time.LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            document.add(new Paragraph("São Paulo, " + hoje)
                    .setFont(fonteNormal)
                    .setFontSize(13)
                    .setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(30));

            // Linha de assinatura
            Canvas signCanvas = new Canvas(new PdfCanvas(page), new Rectangle(W / 2 - 120, 115, 240, 2));
            signCanvas.add(new Paragraph("_______________________________")
                    .setFontColor(CINZA_TEXTO)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER));
            signCanvas.close();

            document.add(new Paragraph("Coordenador do curso de Análise e Desenvolvimento de\nSistemas da FATEC Zona Leste")
                    .setFont(fonteNormal)
                    .setFontSize(10)
                    .setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(4));

            // Rodapé institucional
            document.add(new Paragraph("FATEC Zona Leste  ·  Centro Paula Souza  ·  Governo do Estado de São Paulo")
                    .setFont(fonteNormal)
                    .setFontSize(8)
                    .setFontColor(new DeviceRgb(0x99, 0x99, 0x99))
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar certificado", e);
        }
    }

    private String formatarHoras(Double pontos) {
        if (pontos == null) return "—";
        int horas = pontos.intValue();
        int minutos = (int) Math.round((pontos - horas) * 60);
        if (minutos == 0) return horas + "h";
        return horas + "h" + String.format("%02d", minutos);
    }

    public byte[] gerarCertificadoPalestrante(Long palestranteId) {
        Palestrante palestrante = palestranteRepository.findById(palestranteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Palestrante não encontrado"));

        List<Evento> eventos = eventoRepository.findByPalestranteId(palestranteId);

        if (eventos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Palestrante não possui eventos cadastrados");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);

            PageSize pageSize = PageSize.A4.rotate();
            pdf.addNewPage(pageSize);

            PdfPage page = pdf.getPage(1);
            PdfCanvas pdfCanvas = new PdfCanvas(page);

            float W = pageSize.getWidth();
            float H = pageSize.getHeight();

            // Fundo cinza
            pdfCanvas.setFillColor(CINZA_FUNDO);
            pdfCanvas.rectangle(0, 0, W, H);
            pdfCanvas.fill();

            // Triângulo vermelho superior direito
            pdfCanvas.setFillColor(VERMELHO);
            pdfCanvas.moveTo(W, H);
            pdfCanvas.lineTo(W - 120, H);
            pdfCanvas.lineTo(W, H - 120);
            pdfCanvas.closePathFillStroke();

            // Triângulo amarelo superior direito
            pdfCanvas.setFillColor(AMARELO);
            pdfCanvas.moveTo(W, H);
            pdfCanvas.lineTo(W - 60, H);
            pdfCanvas.lineTo(W, H - 60);
            pdfCanvas.closePathFillStroke();

            // Triângulo vermelho inferior esquerdo
            pdfCanvas.setFillColor(VERMELHO);
            pdfCanvas.moveTo(0, 0);
            pdfCanvas.lineTo(120, 0);
            pdfCanvas.lineTo(0, 120);
            pdfCanvas.closePathFillStroke();

            // Triângulo amarelo inferior esquerdo
            pdfCanvas.setFillColor(AMARELO);
            pdfCanvas.moveTo(0, 0);
            pdfCanvas.lineTo(60, 0);
            pdfCanvas.lineTo(0, 60);
            pdfCanvas.closePathFillStroke();

            // Linhas amarelas
            pdfCanvas.setFillColor(AMARELO);
            pdfCanvas.rectangle(0, 0, W, 8);
            pdfCanvas.fill();
            pdfCanvas.rectangle(0, H - 8, W, 8);
            pdfCanvas.fill();

            pdfCanvas.release();

            PdfFont fonteBold = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont fonteNormal = PdfFontFactory.createFont("Helvetica");

            Document document = new Document(pdf, pageSize);
            document.setMargins(40, 60, 40, 60);

            document.add(new Paragraph("C E R T I F I C A D O")
                    .setFont(fonteBold)
                    .setFontSize(36)
                    .setFontColor(AMARELO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30)
                    .setMarginBottom(16));

            document.add(new Paragraph("Este certificado é concedido ao Palestrante")
                    .setFont(fonteNormal)
                    .setFontSize(13)
                    .setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(6));

            document.add(new Paragraph(palestrante.getNome())
                    .setFont(fonteBold)
                    .setFontSize(22)
                    .setFontColor(PRETO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(8));

            if (palestrante.getEmpresa() != null && !palestrante.getEmpresa().isBlank()) {
                document.add(new Paragraph(palestrante.getEmpresa())
                        .setFont(fonteNormal)
                        .setFontSize(12)
                        .setFontColor(CINZA_TEXTO)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(16));
            }

            document.add(new Paragraph("Por atuar como palestrante nos seguintes eventos promovidos pela FATEC Zona Leste:")
                    .setFont(fonteNormal)
                    .setFontSize(13)
                    .setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(12));

            for (Evento evento : eventos) {
                String dataFormatada = evento.getDataEvento()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                document.add(new Paragraph("• " + evento.getDescricao() + " — " + dataFormatada)
                        .setFont(fonteBold)
                        .setFontSize(12)
                        .setFontColor(PRETO)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(4));
            }

            String hoje = java.time.LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            document.add(new Paragraph("São Paulo, " + hoje)
                    .setFont(fonteNormal)
                    .setFontSize(13)
                    .setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20)
                    .setMarginBottom(30));

            Canvas signCanvas = new Canvas(new PdfCanvas(page), new Rectangle(W / 2 - 120, 115, 240, 2));
            signCanvas.add(new Paragraph("_______________________________")
                    .setFontColor(CINZA_TEXTO)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER));
            signCanvas.close();

            document.add(new Paragraph("Coordenador do curso de Análise e Desenvolvimento de\nSistemas da FATEC Zona Leste")
                    .setFont(fonteNormal)
                    .setFontSize(10)
                    .setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(4));

            document.add(new Paragraph("FATEC Zona Leste  ·  Centro Paula Souza  ·  Governo do Estado de São Paulo")
                    .setFont(fonteNormal)
                    .setFontSize(8)
                    .setFontColor(new DeviceRgb(0x99, 0x99, 0x99))
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar certificado do palestrante", e);
        }
    }

    public byte[] gerarCertificadoMedalha(String nomeParticipante, String nomeMedalha, Double pontos) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);

            PageSize pageSize = PageSize.A4.rotate();
            pdf.addNewPage(pageSize);

            PdfPage page = pdf.getPage(1);
            PdfCanvas pdfCanvas = new PdfCanvas(page);

            float W = pageSize.getWidth();
            float H = pageSize.getHeight();

            pdfCanvas.setFillColor(CINZA_FUNDO);
            pdfCanvas.rectangle(0, 0, W, H);
            pdfCanvas.fill();

            pdfCanvas.setFillColor(VERMELHO);
            pdfCanvas.moveTo(W, H); pdfCanvas.lineTo(W - 120, H); pdfCanvas.lineTo(W, H - 120);
            pdfCanvas.closePathFillStroke();
            pdfCanvas.setFillColor(AMARELO);
            pdfCanvas.moveTo(W, H); pdfCanvas.lineTo(W - 60, H); pdfCanvas.lineTo(W, H - 60);
            pdfCanvas.closePathFillStroke();
            pdfCanvas.setFillColor(VERMELHO);
            pdfCanvas.moveTo(0, 0); pdfCanvas.lineTo(120, 0); pdfCanvas.lineTo(0, 120);
            pdfCanvas.closePathFillStroke();
            pdfCanvas.setFillColor(AMARELO);
            pdfCanvas.moveTo(0, 0); pdfCanvas.lineTo(60, 0); pdfCanvas.lineTo(0, 60);
            pdfCanvas.closePathFillStroke();
            pdfCanvas.setFillColor(AMARELO);
            pdfCanvas.rectangle(0, 0, W, 8); pdfCanvas.fill();
            pdfCanvas.rectangle(0, H - 8, W, 8); pdfCanvas.fill();
            pdfCanvas.release();

            PdfFont fonteBold = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont fonteNormal = PdfFontFactory.createFont("Helvetica");

            Document document = new Document(pdf, pageSize);
            document.setMargins(40, 60, 40, 60);

            document.add(new Paragraph("C E R T I F I C A D O  D E  M E D A L H A")
                    .setFont(fonteBold).setFontSize(30).setFontColor(AMARELO)
                    .setTextAlignment(TextAlignment.CENTER).setMarginTop(30).setMarginBottom(16));

            document.add(new Paragraph("Este certificado é concedido ao Aluno")
                    .setFont(fonteNormal).setFontSize(13).setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(6));

            document.add(new Paragraph(nomeParticipante)
                    .setFont(fonteBold).setFontSize(22).setFontColor(PRETO)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(16));

            // Imagem da medalha
            Medalha medalhaObj = medalhaRepository.findAll().stream()
                    .filter(m -> m.getNome().equals(nomeMedalha))
                    .findFirst().orElse(null);

            if (medalhaObj != null && medalhaObj.getImagemUrl() != null) {
                try {
                    java.io.File imgFile = new java.io.File("." + medalhaObj.getImagemUrl());
                    if (imgFile.exists()) {
                        Image img = new Image(ImageDataFactory.create(imgFile.getAbsolutePath()));
                        img.setWidth(80).setHeight(80).setHorizontalAlignment(
                                com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                        document.add(img);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar imagem da medalha: " + e.getMessage());
                }
            }

            document.add(new Paragraph()
                    .add(new Text("Por atingir a medalha ").setFont(fonteNormal).setFontColor(CINZA_TEXTO))
                    .add(new Text(nomeMedalha).setFont(fonteBold).setFontColor(PRETO))
                    .add(new Text(" com ").setFont(fonteNormal).setFontColor(CINZA_TEXTO))
                    .add(new Text(String.format("%.1f", pontos) + " pontos").setFont(fonteBold).setFontColor(PRETO))
                    .add(new Text(" acumulados no semestre, promovido pela FATEC Zona Leste.").setFont(fonteNormal).setFontColor(CINZA_TEXTO))
                    .setFontSize(13).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

            String hoje = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            document.add(new Paragraph("São Paulo, " + hoje)
                    .setFont(fonteNormal).setFontSize(13).setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(30));

            // Remove o Canvas signCanvas e troca por:
            document.add(new Paragraph("___________________________")
                    .setFont(fonteNormal)
                    .setFontSize(11)
                    .setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(4));

            document.add(new Paragraph("Coordenador do curso de Análise e Desenvolvimento de\nSistemas da FATEC Zona Leste")
                    .setFont(fonteNormal).setFontSize(10).setFontColor(CINZA_TEXTO)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(4));

            document.add(new Paragraph("FATEC Zona Leste  ·  Centro Paula Souza  ·  Governo do Estado de São Paulo")
                    .setFont(fonteNormal).setFontSize(8).setFontColor(new DeviceRgb(0x99, 0x99, 0x99))
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar certificado de medalha", e);
        }
    }
}