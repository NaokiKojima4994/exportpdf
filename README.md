# Spring BootとPDFBoxを使用したPDF生成

このプロジェクトは、Spring BootとPDFBoxを使用して複雑なPDFドキュメントを生成する方法を示します。生成されたPDFには、複数のページ、異なるフォント、画像、およびテーブルが含まれます。

## 必要要件

- Java 11以上
- Gradle 6.8以上

## 始め方

### リポジトリをクローン

```bash
git clone https://github.com/yourusername/pdf-generation.git
cd pdf-generation
```

### プロジェクトのビルド

```bash
./gradlew build
```

### アプリケーションの実行

```bash
./gradlew bootRun
```

アプリケーションは`http://localhost:8080`で起動します。

### PDFの生成

Webブラウザで以下のURLにアクセスすることで、PDFを生成できます。

```
http://localhost:8080/api/pdf/generate
```

PDFは自動的にダウンロードされます。

## プロジェクト構成

- **src/main/java/com/example/pdf**
    - **PdfController.java**: PDFを生成するエンドポイントを含むクラス。
- **src/test/java/com/example/pdf**
    - **PdfControllerTest.java**: PDF生成を検証するテストクラス。
- **src/main/resources/static**
    - **sample_image.png**: 生成されるPDFに含まれる画像。

## 依存関係

このプロジェクトでは、以下の依存関係を使用しています。

- Spring Boot Starter Web: Webアプリケーションを構築するため。
- PDFBox: PDFドキュメントを作成および操作するため。
- JUnit 5: テストの作成と実行のため。
- Spring Boot Starter Test: Spring Bootアプリケーションのテストのため。

### `build.gradle`

```gradle
plugins {
    id 'org.springframework.boot' version '2.6.3'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.apache.pdfbox:pdfbox:2.0.24'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.8.2'
}
```

## PdfController.java

```java
package com.example.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * PDF生成を行うコントローラークラス。
 */
@RestController
public class PdfController {

    /**
     * PDFを生成してHTTPレスポンスとして返すメソッド。
     *
     * @return 生成されたPDFを含むレスポンス
     * @throws IOException 入出力エラーが発生した場合にスローされる
     */
    @GetMapping("/api/pdf/generate")
    public ResponseEntity<byte[]> generatePdf() throws IOException {
        try (PDDocument document = new PDDocument()) {
            addTitlePage(document);
            addContentPage(document);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "complex_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * タイトルページをドキュメントに追加するメソッド。
     *
     * @param document PDFドキュメント
     * @throws IOException 入出力エラーが発生した場合にスローされる
     */
    private void addTitlePage(PDDocument document) throws IOException {
        PDPage titlePage = new PDPage();
        document.addPage(titlePage);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, titlePage)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 26);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Complex PDF Report");
            contentStream.endText();
        }
    }

    /**
     * コンテンツページをドキュメントに追加するメソッド。
     *
     * @param document PDFドキュメント
     * @throws IOException 入出力エラーが発生した場合にスローされる
     */
    private void addContentPage(PDDocument document) throws IOException {
        PDPage contentPage = new PDPage();
        document.addPage(contentPage);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, contentPage)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("This is a more complex PDF document that includes:");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("- Multiple pages");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("- Different fonts");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("- Images");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("- Tables");
            contentStream.endText();

            PDImageXObject image = PDImageXObject.createFromFile("src/main/resources/static/sample_image.png", document);
            contentStream.drawImage(image, 50, 500, 200, 150);

            float margin = 50;
            float yStart = 450;
            float tableWidth = contentPage.getMediaBox().getWidth() - 2 * margin;
            float yPosition = yStart;
            float rowHeight = 20;
            int numCols = 3;
            String[] headers = {"Header 1", "Header 2", "Header 3"};
            String[][] content = {
                    {"Cell 1", "Cell 2", "Cell 3"},
                    {"Cell 4", "Cell 5", "Cell 6"},
                    {"Cell 7", "Cell 8", "Cell 9"}
            };

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            yPosition -= rowHeight;
            for (int i = 0; i < numCols; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + i * (tableWidth / numCols), yPosition);
                contentStream.showText(headers[i]);
                contentStream.endText();
            }

            contentStream.setFont(PDType1Font.HELVETICA, 12);
            for (String[] row : content) {
                yPosition -= rowHeight;
                for (int i = 0; i < row.length; i++) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + i * (tableWidth / numCols), yPosition);
                    contentStream.showText(row[i]);
                    contentStream.endText();
                }
            }
        }
    }
}
```

## PdfControllerTest.java

```java
package com.example.pdf;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class PdfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGeneratePdf() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/api/pdf/generate")
                .accept(MediaType.APPLICATION_PDF))
                .andReturn()
                .getResponse();

        assert

That(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_PDF_VALUE);

        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String pdfText = pdfStripper.getText(document);

            assertThat(pdfText).contains("Complex PDF Report");
            assertThat(pdfText).contains("This is a more complex PDF document that includes:");
            assertThat(pdfText).contains("- Multiple pages");
            assertThat(pdfText).contains("- Different fonts");
            assertThat(pdfText).contains("- Images");
            assertThat(pdfText).contains("- Tables");
            assertThat(pdfText).contains("Header 1");
            assertThat(pdfText).contains("Header 2");
            assertThat(pdfText).contains("Header 3");
            assertThat(pdfText).contains("Cell 1");
            assertThat(pdfText).contains("Cell 2");
            assertThat(pdfText).contains("Cell 3");
            assertThat(pdfText).contains("Cell 4");
            assertThat(pdfText).contains("Cell 5");
            assertThat(pdfText).contains("Cell 6");
            assertThat(pdfText).contains("Cell 7");
            assertThat(pdfText).contains("Cell 8");
            assertThat(pdfText).contains("Cell 9");

            boolean imageFound = false;
            BufferedImage expectedImage = ImageIO.read(new File("src/main/resources/static/sample_image.png"));
            for (PDPage page : document.getPages()) {
                Iterable<COSName> cosNames = page.getResources().getXObjectNames();
                for (COSName cosName : cosNames) {
                    if (page.getResources().isImageXObject(cosName)) {
                        PDImageXObject image = (PDImageXObject) page.getResources().getXObject(cosName);
                        BufferedImage bufferedImage = image.getImage();
                        assertThat(bufferedImage).isNotNull();

                        assertThat(bufferedImage.getWidth()).isEqualTo(expectedImage.getWidth());
                        assertThat(bufferedImage.getHeight()).isEqualTo(expectedImage.getHeight());
                        for (int y = 0; y < bufferedImage.getHeight(); y++) {
                            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                                assertThat(bufferedImage.getRGB(x, y)).isEqualTo(expectedImage.getRGB(x, y));
                            }
                        }

                        imageFound = true;
                    }
                }
            }
            assertThat(imageFound).isTrue();
        }
    }
}
```

## ライセンス

このプロジェクトはMITライセンスの下でライセンスされています。
```

このREADMEファイルは、プロジェクトの概要、必要な環境、セットアップ方法、プロジェクト構成、依存関係、および主要なソースコードファイルを説明しています。これでプロジェクトを開始するための基本的な情報が網羅されています。