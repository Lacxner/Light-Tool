package org.light.lighttoolspringbootstarter.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

/**
 * 条形码和二维码工具类
 * @author Gaoziyang
 * @since 2022-11-18 11:51:15
 */
public class CodeUtils {
    /**
     * 生成条形码并转为 Base64 编码
     * @param content 条形码内容
     * @param width 宽度
     * @param height 高度
     * @return 条形码的 Base64 编码
     */
    public static String generateBarCodeBase64(String content, int width, int height) {
        return imageToBse64(generateBarCodeByteArray(content, BarcodeFormat.CODE_128, width, height));
    }

    /**
     * 生成二维码并转为 Base64 编码
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @return 条形码的 Base64 编码
     */
    public static String generateQRCodeBase64(String content, int width, int height) {
        return imageToBse64(generateBarCodeByteArray(content, BarcodeFormat.QR_CODE, width, height));
    }

    /**
     * 生成字节数组形式的条形码码
     * @param content 条形码内容
     * @param barcodeFormat 条形码格式
     * @param width 宽度
     * @param height 高度
     * @return 条形码字节数组
     */
    public static byte[] generateBarCodeByteArray(String content, BarcodeFormat barcodeFormat, int width, int height) {
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 容错级别 这里选择最高H级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // 生成条形码图片
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, barcodeFormat, width, height, hints);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    /**
     * 将图片进行 Base64 编码
     * @param src 图片字节
     * @return Base64 编码
     */
    public static String imageToBse64(byte[] src) {
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(src);
    }
}
