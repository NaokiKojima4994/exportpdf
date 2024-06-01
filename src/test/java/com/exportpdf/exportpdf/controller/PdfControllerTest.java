package com.exportpdf.exportpdf.controller;

import org.apache.pdfbox.cos.COSName;  // PDFBoxのCOSNameクラスをインポートするで
import org.apache.pdfbox.pdmodel.PDDocument;  // PDFBoxのPDDocumentクラスをインポートするで
import org.apache.pdfbox.pdmodel.PDPage;  // PDFBoxのPDPageクラスをインポートするで
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;  // PDFBoxのPDImageXObjectクラスをインポートするで
import org.apache.pdfbox.text.PDFTextStripper;  // PDFBoxのPDFTextStripperクラスをインポートするで
import org.junit.jupiter.api.Test;  // JUnitのTestアノテーションをインポートするで
import org.springframework.beans.factory.annotation.Autowired;  // SpringのAutowiredアノテーションをインポートするで
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;  // SpringのAutoConfigureMockMvcアノテーションをインポートするで
import org.springframework.boot.test.context.SpringBootTest;  // SpringのSpringBootTestアノテーションをインポートするで
import org.springframework.http.MediaType;  // SpringのMediaTypeクラスをインポートするで
import org.springframework.mock.web.MockHttpServletResponse;  // SpringのMockHttpServletResponseクラスをインポートするで
import org.springframework.test.web.servlet.MockMvc;  // SpringのMockMvcクラスをインポートするで

import javax.imageio.ImageIO;  // JavaのImageIOクラスをインポートするで
import java.awt.image.BufferedImage;  // JavaのBufferedImageクラスをインポートするで
import java.io.ByteArrayInputStream;  // JavaのByteArrayInputStreamクラスをインポートするで
import java.io.File;  // JavaのFileクラスをインポートするで
import java.io.IOException;  // JavaのIOExceptionクラスをインポートするで

import static org.assertj.core.api.Assertions.assertThat;  // AssertJのアサーションをインポートするで
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;  // MockMvcのgetメソッドをインポートするで

@SpringBootTest  // スプリングブートのテストコンテキスト設定や
@AutoConfigureMockMvc  // MockMvcの自動設定や
public class PdfControllerTest {

    @Autowired
    private MockMvc mockMvc;  // MockMvcのインジェクションや

    @Test
    public void testGeneratePdf() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/api/pdf/generate")  // GETリクエストを送るで
                .accept(MediaType.APPLICATION_PDF))  // PDFを受け取る設定や
                .andReturn()
                .getResponse();  // レスポンスを取得するで

        assertThat(response.getStatus()).isEqualTo(200);  // ステータスコードが200かチェックや
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_PDF_VALUE);  // コンテンツタイプがPDFかチェックや

        // PDF内容の検証
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(response.getContentAsByteArray()))) {  // PDFを読み込むで
            PDFTextStripper pdfStripper = new PDFTextStripper();  // PDFTextStripperを作成するで
            String pdfText = pdfStripper.getText(document);  // PDFからテキスト抽出するで

            assertThat(pdfText).contains("Complex PDF Report");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("This is a more complex PDF document that includes:");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("- Multiple pages");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("- Different fonts");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("- Images");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("- Tables");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Header 1");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Header 2");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Header 3");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Cell 1");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Cell 2");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Cell 3");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Cell 4");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Cell 5");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Cell 6");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Cell 7");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Cell 8");  // テキストが含まれているかチェックや
            assertThat(pdfText).contains("Cell 9");  // テキストが含まれているかチェックや

            // 画像の検証
            boolean imageFound = false;  // 画像が見つかったかどうかのフラグや
            BufferedImage expectedImage = ImageIO.read(new File("src/main/resources/static/sample_image.png"));  // 期待される画像を読み込むで
            for (PDPage page : document.getPages()) {  // 各ページをループするで
                Iterable<COSName> cosNames = page.getResources().getXObjectNames();  // リソースの名前を取得するで
                for (COSName cosName : cosNames) {  // 各リソースをチェックするで
                    if (page.getResources().isImageXObject(cosName)) {  // 画像かどうかチェックや
                        PDImageXObject image = (PDImageXObject) page.getResources().getXObject(cosName);  // 画像を取得するで
                        BufferedImage bufferedImage = image.getImage();  // 画像をバッファに読み込むで
                        assertThat(bufferedImage).isNotNull();  // 画像がnullやないかチェックや

                        // 画像が一致するかチェックや
                        assertThat(bufferedImage.getWidth()).isEqualTo(expectedImage.getWidth());  // 画像の幅が一致するかチェックや
                        assertThat(bufferedImage.getHeight()).isEqualTo(expectedImage.getHeight());  // 画像の高さが一致するかチェックや
                        for (int y = 0; y < bufferedImage.getHeight(); y++) {  // 各ピクセルをチェックするで
                            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                                assertThat(bufferedImage.getRGB(x, y)).isEqualTo(expectedImage.getRGB(x, y));  // ピクセルが一致するかチェックや
                            }
                        }

                        imageFound = true;  // 画像が見つかったで
                    }
                }
            }
            assertThat(imageFound).isTrue();  // 画像が見つかったかチェックや
        }
    }
}