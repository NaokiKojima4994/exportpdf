package com.exportpdf.exportpdf.controller;

import org.apache.pdfbox.pdmodel.PDDocument;  // PDFBoxのPDDocumentクラスをインポートするで
import org.apache.pdfbox.pdmodel.PDPage;  // PDFBoxのPDPageクラスをインポートするで
import org.apache.pdfbox.pdmodel.PDPageContentStream;  // PDFBoxのPDPageContentStreamクラスをインポートするで
import org.apache.pdfbox.pdmodel.font.PDType1Font;  // PDFBoxのPDType1Fontクラスをインポートするで
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;  // PDFBoxのPDImageXObjectクラスをインポートするで
import org.springframework.http.HttpHeaders;  // SpringのHttpHeadersクラスをインポートするで
import org.springframework.http.MediaType;  // SpringのMediaTypeクラスをインポートするで
import org.springframework.http.ResponseEntity;  // SpringのResponseEntityクラスをインポートするで
import org.springframework.web.bind.annotation.GetMapping;  // SpringのGetMappingアノテーションをインポートするで
import org.springframework.web.bind.annotation.RestController;  // SpringのRestControllerアノテーションをインポートするで

import java.io.ByteArrayOutputStream;  // JavaのByteArrayOutputStreamクラスをインポートするで
import java.io.IOException;  // JavaのIOExceptionクラスをインポートするで

@RestController  // このクラスをRESTコントローラーとして定義するで
public class PdfController {

    @GetMapping("/api/pdf/generate")  // このメソッドをGETリクエストのハンドラーとして定義するで
    public ResponseEntity<byte[]> generatePdf() throws IOException {  // PDF生成するメソッドや
        try (PDDocument document = new PDDocument()) {  // 新しいPDFドキュメント作るで
            addTitlePage(document);  // タイトルページ追加するで
            addContentPage(document);  // コンテンツページ追加するで

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  // バイト配列出力ストリーム作るで
            document.save(byteArrayOutputStream);  // PDFをバイト配列に保存するで

            HttpHeaders headers = new HttpHeaders();  // HTTPヘッダー作るで
            headers.setContentType(MediaType.APPLICATION_PDF);  // ヘッダーにコンテンツタイプを設定するで
            headers.setContentDispositionFormData("attachment", "complex_report.pdf");  // ヘッダーにコンテンツディスポジションを設定するで

            return ResponseEntity.ok()  // レスポンスを作成するで
                    .headers(headers)  // ヘッダーを設定するで
                    .body(byteArrayOutputStream.toByteArray());  // ボディにPDFを設定するで
        } catch (Exception e) {  // 例外が発生した場合
            e.printStackTrace();  // スタックトレースを出力するで
            return ResponseEntity.status(500).build();  // 500エラーを返すで
        }
    }

    private void addTitlePage(PDDocument document) throws IOException {  // タイトルページを追加するメソッドや
        PDPage titlePage = new PDPage();  // 新しいページ作るで
        document.addPage(titlePage);  // ドキュメントにページ追加や

        try (PDPageContentStream contentStream = new PDPageContentStream(document, titlePage)) {  // コンテンツストリームを作成するで
            contentStream.beginText();  // テキストの書き込み開始や
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 26);  // フォント設定するで
            contentStream.newLineAtOffset(100, 700);  // テキストの位置決めや
            contentStream.showText("Complex PDF Report");  // タイトルテキストや
            contentStream.endText();  // テキストの書き込み終了や
        }
    }

    private void addContentPage(PDDocument document) throws IOException {  // コンテンツページを追加するメソッドや
        PDPage contentPage = new PDPage();  // 新しいページ作るで
        document.addPage(contentPage);  // ドキュメントにページ追加や

        try (PDPageContentStream contentStream = new PDPageContentStream(document, contentPage)) {  // コンテンツストリームを作成するで
            contentStream.beginText();  // テキストの書き込み開始や
            contentStream.setFont(PDType1Font.HELVETICA, 12);  // フォント設定するで
            contentStream.newLineAtOffset(50, 700);  // テキストの位置決めや
            contentStream.showText("This is a more complex PDF document that includes:");  // 説明テキストや
            contentStream.newLineAtOffset(0, -20);  // 次の行に移動するで
            contentStream.showText("- Multiple pages");  // テキスト追加するで
            contentStream.newLineAtOffset(0, -20);  // 次の行に移動するで
            contentStream.showText("- Different fonts");  // テキスト追加するで
            contentStream.newLineAtOffset(0, -20);  // 次の行に移動するで
            contentStream.showText("- Images");  // テキスト追加するで
            contentStream.newLineAtOffset(0, -20);  // 次の行に移動するで
            contentStream.showText("- Tables");  // テキスト追加するで
            contentStream.endText();  // テキストの書き込み終了や

            // 画像追加するで
            PDImageXObject image = PDImageXObject.createFromFile("src/main/resources/static/sample_image.png", document);  // 画像を読み込むで
            contentStream.drawImage(image, 50, 500, 200, 150);  // 画像を指定した位置に描画するで

            // テーブル追加するで
            float margin = 50;  // マージン設定するで
            float yStart = 450;  // テーブルの開始位置や
            float tableWidth = contentPage.getMediaBox().getWidth() - 2 * margin;  // テーブルの幅計算するで
            float yPosition = yStart;  // Y位置の初期値設定や
            float rowHeight = 20;  // 行の高さ設定するで
            int numCols = 3;  // 列の数設定するで
            String[] headers = {"Header 1", "Header 2", "Header 3"};  // ヘッダーや
            String[][] content = {  // テーブルの内容や
                    {"Cell 1", "Cell 2", "Cell 3"},
                    {"Cell 4", "Cell 5", "Cell 6"},
                    {"Cell 7", "Cell 8", "Cell 9"}
            };

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);  // フォント設定や
            yPosition -= rowHeight;  // 行の高さ分だけ下げるで
            for (int i = 0; i < numCols; i++) {  // 各列のヘッダーを描画するで
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + i * (tableWidth / numCols), yPosition);  // 列ごとの位置設定や
                contentStream.showText(headers[i]);  // ヘッダーを描画するで
                contentStream.endText();
            }

            contentStream.setFont(PDType1Font.HELVETICA, 12);  // フォント設定や
            for (String[] row : content) {  // 各行の内容を描画するで
                yPosition -= rowHeight;  // 行の高さ分だけ下げるで
                for (int i = 0; i < row.length; i++) {  // 各列の内容を描画するで
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + i * (tableWidth / numCols), yPosition);  // 列ごとの位置設定や
                    contentStream.showText(row[i]);  // 各セルのテキストを描画するで
                    contentStream.endText();
                }
            }
        }
    }
}
