package auto.util.convert;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Created by ZhangXu on 2017/12/27.
 * ref http://blog.csdn.net/u013725455/article/details/52091088
 */
public class QrCodeUtil {
    private static Logger log = LoggerFactory.getLogger(QrCodeUtil.class);
    private static final String defaultFormat = "png";

    private static final HashMap<EncodeHintType,Object>  defaultQrHints=new HashMap<>();
    private static final HashMap<EncodeHintType, String> defaultBrHints = new HashMap<>();
    private static final Map<DecodeHintType, Object>     readQrHints = new Hashtable<>();

    static {
        defaultQrHints.put(EncodeHintType.CHARACTER_SET, "utf8");
        defaultQrHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//纠错等级L,M,Q,H
        defaultQrHints.put(EncodeHintType.MARGIN, 2); //边距

        defaultBrHints.put(EncodeHintType.CHARACTER_SET, "utf8");

        readQrHints.put(DecodeHintType.CHARACTER_SET, "utf8");
    }

    /** 创建条形码 */
    public static void createBr(String content,String target) throws IOException {
        File file = new File(target);
        createBr(content,new FileOutputStream(file));
    }

    /** 创建条形码 */
    public static void createBr(String contents, OutputStream out){
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(contents,
                    BarcodeFormat.CODE_128, 250, 75, defaultBrHints);
            MatrixToImageWriter.writeToStream(matrix,defaultFormat,out);
        } catch (Exception e) {
            log.warn("{}",e);
        }
    }
    /** 创建二维码 */
    public static void createQr(String content,String target) throws IOException {
        File file = new File(target);
        createQr(content,new FileOutputStream(file));
    }
    /** 创建二维码 */
    public static void createQr(String content,OutputStream out) {
        try {
            BitMatrix bitMatrix=new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, 300, 300, defaultQrHints);
            MatrixToImageWriter.writeToStream(bitMatrix,defaultFormat,out);
        } catch (Exception e) {
            log.warn("{}",e);
        }
    }
    /** 读取条形码 */
    public String readBr(String imgPath) throws FileNotFoundException {
        return readBr(new FileInputStream(new File(imgPath)));
    }
    /** 读取条形码 */
    public String readBr(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            return new MultiFormatReader().decode(bitmap, null).getText();
        } catch (Exception e) {
            log.warn("{}",e);
        }
        return null;
    }
    /** 读取二维码 */
    public String readQr(String imgPath) throws FileNotFoundException {
        return readQr(new FileInputStream(new File(imgPath)));
    }
    /** 读取二维码 */
    public String readQr(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            return new MultiFormatReader().decode(bitmap, readQrHints).getText();
        } catch (Exception e) {
            log.warn("{}",e);
        }
        return null;
    }
    /**
     * @author Google
     */
    static class MatrixToImageWriter{
        private static final int BLACK = 0xFF000000;
        private static final int WHITE = 0xFFFFFFFF;

        private MatrixToImageWriter() {}

        private static BufferedImage toBufferedImage(BitMatrix matrix) {
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
                }
            }
            return image;
        }

        public static void writeToFile(BitMatrix matrix, String format, File file)
                throws IOException {
            BufferedImage image = toBufferedImage(matrix);
            if (!ImageIO.write(image, format, file)) {
                throw new IOException("Could not write an image of format " + format + " to " + file);
            }
        }

        public static void writeToStream(BitMatrix matrix, String format, OutputStream stream)
                throws IOException {
            BufferedImage image = toBufferedImage(matrix);
            if (!ImageIO.write(image, format, stream)) {
                throw new IOException("Could not write an image of format " + format);
            }
        }
    }
}
