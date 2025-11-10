package kr.co.syrup.adreport.framework.utils;


import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

@Slf4j
@Component
public class AESUtils {

    private final String ALGO = "AES";

    @Value("${aes.keyvalue}")
    private String keyValue;

    public String encrypt(String Data) {

        String encryptedValue = null;
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(Data.getBytes());
//        String encryptedValue = new BASE64Encoder().encode(encVal);
            encryptedValue = DatatypeConverter.printBase64Binary(encVal);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ResultCodeEnum.ERROR_AESCODEC, e);
        }
        return encryptedValue;
    }

    public String decrypt(String encryptedData)  {
        String decryptedValue = null;
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
//        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
            byte[] decordedValue = DatatypeConverter.parseBase64Binary(encryptedData);
            byte[] decValue = c.doFinal(decordedValue);
            decryptedValue = new String(decValue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ResultCodeEnum.ERROR_AESCODEC, e);
        }

        return decryptedValue;
    }

    private Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue.getBytes(), ALGO);
        return key;
    }

    public static void main(String[] args) throws Exception {
    }
}